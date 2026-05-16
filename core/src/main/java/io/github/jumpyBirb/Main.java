package io.github.jumpyBirb;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;

import io.github.jumpyBirb.data.*;
import io.github.jumpyBirb.data.intro.Intro;
import io.github.jumpyBirb.game.*;
import io.github.jumpyBirb.graphics.GameAssets;
import io.github.jumpyBirb.graphics.GameRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jumpyBirb.platform.MobileKeyboardBridge;

import java.util.List;

/**
 * Main game entry point for JumpyBirb.
 *
 * <p>
 * This class is the central coordinator of the game.
 * It does not contain all game logic itself anymore, but instead creates
 * and connects the other classes that handle specific responsibilities.
 *
 * <p>
 * Main is responsible for:
 * <ul>
 * <li>creating and setting up core game objects</li>
 * <li>running the update loop every frame</li>
 * <li>sending the current game state to the renderer</li>
 * <li>handling high-level game flow such as start, restart, and game over</li>
 * </ul>
 *
 * <p>
 * Main is NOT responsible for detailed logic in every area:
 * <ul>
 * <li>{@link Player} handles player movement</li>
 * <li>{@link Score} handles score logic</li>
 * <li>{@link ObstacleManager} handles spawning and updating obstacles</li>
 * <li>{@link ParallaxBackground} handles parallax movement and drawing</li>
 * <li>{@link GameRenderer} handles rendering</li>
 * <li>{@link GameAssets} handles textures</li>
 * </ul>
 *
 * <p>
 * Why this class exists in this form:
 * Earlier, almost everything was inside Main, which made the file very long
 * and hard to understand. The current version keeps Main as the "orchestrator"
 * of the game while moving detailed behavior into separate classes.
 *
 * <p>
 * Game flow:
 * <ul>
 * <li>{@code create()} runs once when the game starts</li>
 * <li>{@code render()} runs every frame</li>
 * <li>{@code update()} updates game logic</li>
 * <li>{@code renderer.draw(...)} renders the current state</li>
 * <li>{@code dispose()} cleans up resources when the game closes</li>
 * </ul>
 */
public class Main extends ApplicationAdapter {
    private MobileKeyboardBridge keyboardBridge;
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float UI_WIDTH = 1920f;
    private static final float UI_HEIGHT = 1080f;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private static final float GRAVITY = 30f;
    private static final float JUMP_FORCE = 6.0f;
    private static final float PLAYER_START_X = 3f;
    private static final float PLAYER_START_Y = 4.5f;
    private static final float PLAYER_WIDTH = 1.4f;
    private static final float PLAYER_HEIGHT = 0.8f;

    private static final float POD_START_X = 1.9f;
    private static final float POD_START_Y = 0f;
    private static final float POD_WIDTH = 3.7f;
    private static final float POD_HEIGHT = 4.8f;
    private static final float POD_SPEED = 2.5f;

    private static final float OBSTACLE_WIDTH = 1.8f;
    private static final float OBSTACLE_HEIGHT = 4.5f;
    private static final float MIN_OBSTACLE_HEIGHT = 4.5f;

    private SpriteBatch batch;
    private BitmapFont uiFont;
    private BitmapFont gameUiFont;
    private BitmapFont highScoreFont;
    private GameAssets assets;
    private float startBlinkTimer = 0f;

    private Stage nameStage;
    private TextField nameField;
    private Skin skin;

    private Player player;
    private Score score;
    private ObstacleManager obstacleManager;
    private ParallaxBackground background;
    private GameRenderer renderer;
    private Menu menu;
    private Menu gameOverMenu;
    private Menu highScoreMenu;
    private Menu confirmMenu;
    private Settings settings;
    private Credits credits;
    private String playerName = "Player";
    private boolean scoreSaved = false;
    private float dyingTimer = 0f;
    private static final float DYING_DURATION = 1.2f;
    private boolean sound = true;
    private boolean music = true;
    private GameState previousState = null;

    private boolean gameHasStarted = false;

    private InputGate inputGate = new InputGate();

    private AudioManager audio;

    private GameState gameState;
    private long finalScore = 0;
    private Intro intro;

    private float screenWidth;
    private float screenHeight;
    private float ceiling;

    private float podX;
    private float podY;

    /**
     * Total time the current run has been active.
     *
     * <p>
     * Used for difficulty scaling such as:
     * <ul>
     * <li>stronger/weaker jump tuning</li>
     * <li>faster obstacle movement</li>
     * <li>smaller obstacle gap</li>
     * <li>shorter spawn interval</li>
     * </ul>
     */
    private float timePlaying = 0f;

    /**
     * Runs once when the game is first created.
     *
     * <p>
     * This method creates the rendering objects, loads assets,
     * reads screen size, creates the game systems, and sets the game to START
     * state.
     */
    @Override
    public void create() {
        if (isMobileWeb() && keyboardBridge != null) {
            keyboardBridge.enableKeyboard();
        }

        batch = new SpriteBatch();
        assets = new GameAssets();

        uiFont = assets.uiFont;
        gameUiFont = assets.gameUiFont;
        highScoreFont = assets.highScoreFont;

        highScoreMenu = new Menu(
            new String[]{"menu"},
            new GameState[]{GameState.MENU},
            assets.menuFont
        );

        settings = new Settings(assets.menuFont);
        menu = new Menu(
            new String[]{"start", "high score", "settings", "exit game"},
            new GameState[]{GameState.RUNNING, GameState.HIGH_SCORE, GameState.SETTINGS, GameState.EXIT},
            assets.menuFont
        );

        confirmMenu = new Menu(
            new String[]{"yes", "no"},
            new GameState[]{GameState.RESET_SCORE, GameState.SETTINGS},
            assets.menuFont
        );


        gameOverMenu = new Menu(
            new String[]{"play again", "settings", "exit game"},
            new GameState[]{GameState.RUNNING, GameState.SETTINGS, GameState.EXIT},
            assets.menuFont
        );
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(UI_WIDTH, UI_HEIGHT, uiCamera);
        uiViewport.apply();

        uiCamera.position.set(UI_WIDTH / 2f, UI_HEIGHT / 2f, 0);
        uiCamera.update();

        credits = new Credits(assets.creditsFont, assets.menuFont);
        intro = new Intro(assets.logoText, assets.introFont);
        gameState = GameState.NAME_INPUT;
        inputGate.block(1f);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // måste finnas i assets-foldern
        nameStage = new Stage(new FitViewport(UI_WIDTH, UI_HEIGHT));

        nameField = new TextField("", skin);
        nameField.setMessageText(playerName.trim());
        nameField.setMaxLength(12);
        nameField.setSize(300, 50);
        nameField.setPosition(
            UI_WIDTH / 2f - 150,
            UI_HEIGHT / 2f);
        nameField.setTextFieldFilter(new TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isLetterOrDigit(c) || c == '_';
            }
        });
        nameStage.addActor(nameField);

        screenWidth = WORLD_WIDTH;
        screenHeight = WORLD_HEIGHT;
        ceiling = WORLD_HEIGHT;

        renderer = new GameRenderer(batch, assets, camera, screenWidth, screenHeight);
        player = new Player(PLAYER_START_X, PLAYER_START_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
        score = new Score();
        obstacleManager = new ObstacleManager(
            OBSTACLE_WIDTH, MIN_OBSTACLE_HEIGHT, OBSTACLE_HEIGHT, screenWidth, screenHeight, assets.topObstacles,
            assets.bottomObstacles);
        background = new ParallaxBackground(
            assets.parallax1,
            assets.parallax2,
            assets.parallax3,
            screenWidth,
            screenHeight);

        podX = POD_START_X;
        podY = POD_START_Y;

        audio = new AudioManager(assets);


    }

    public Main() {
    }

    public Main(MobileKeyboardBridge keyboardBridge) {
        this.keyboardBridge = keyboardBridge;
    }

    /**
     * Runs once every frame.
     *
     * <p>
     * This is the main game loop.
     * First the game logic is updated, then the current state is rendered.
     */
    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        update();
        handleCursor();


        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);

        switch (gameState) {

            case INTRO:
                batch.begin();

                batch.draw(assets.background, 0, 0, UI_WIDTH, UI_HEIGHT);
                batch.draw(assets.parallax1, 0, 0, UI_WIDTH, UI_HEIGHT);
                batch.draw(assets.parallax2, 0, 0, UI_WIDTH, UI_HEIGHT);
                batch.draw(assets.parallax3, 0, 0, UI_WIDTH, UI_HEIGHT);
                batch.draw(assets.parallax4, 0, 0, UI_WIDTH, UI_HEIGHT);

                intro.render(batch, UI_WIDTH, UI_HEIGHT);

                batch.end();
                break;

            case START:
            case MENU:
                batch.begin();

                batch.draw(assets.menuBackground, 0, 0, UI_WIDTH, UI_HEIGHT);
                menu.render(batch, 100, UI_HEIGHT - 200);

                batch.end();
                break;

            case SETTINGS:
                batch.begin();

                batch.draw(assets.menuBackground, 0, 0, UI_WIDTH, UI_HEIGHT);
                settings.render(batch, 100, UI_HEIGHT - 200, music, sound);

                batch.end();
                break;

            case RUNNING:
            case DYING:
                viewport.apply();
                batch.setProjectionMatrix(camera.combined);

                renderer.draw(
                    background,
                    player,
                    obstacleManager.getPairs(),
                    score,
                    gameState,
                    finalScore,
                    podX,
                    podY,
                    POD_WIDTH,
                    POD_HEIGHT
                );

                if (gameState == GameState.RUNNING && !gameHasStarted) {
                    uiViewport.apply();
                    batch.setProjectionMatrix(uiCamera.combined);

                    batch.begin();

                    String msg = "Press SPACE to begin";

                    GlyphLayout layout = new GlyphLayout();
                    layout.setText(assets.gameUiFont, msg);

                    float x = UI_WIDTH - layout.width - 50;
                    float y = UI_HEIGHT / 2f;

                    float alpha = (float) Math.abs(Math.sin(startBlinkTimer * 3));

                    assets.gameUiFont.setColor(1, 1, 1, alpha);
                    assets.gameUiFont.draw(batch, msg, x, y);
                    assets.gameUiFont.setColor(Color.WHITE);

                    batch.end();
                }
                break;

            case CONFIRM_RESET:
                batch.begin();

                batch.draw(assets.menuBackground, 0, 0, UI_WIDTH, UI_HEIGHT);
                gameUiFont.draw(batch, "Reset High Score?", 100, 500);
                confirmMenu.render(batch, 100, 400);

                batch.end();
                break;

            case NAME_INPUT:
                batch.begin();

                batch.draw(assets.startBackground, 0, 0, UI_WIDTH, UI_HEIGHT);

                batch.end();

                Gdx.input.setInputProcessor(nameStage);

                nameStage.act(Gdx.graphics.getDeltaTime());
                nameStage.draw();

                batch.begin();

                uiFont.draw(batch, "Enter your name:",
                    UI_WIDTH / 2f - 150,
                    UI_HEIGHT / 2f + 80);

                uiFont.draw(batch, "Press SPACE to continue",
                    UI_WIDTH / 2f - 220,
                    UI_HEIGHT / 2f - 80);

                batch.end();
                break;

            case GAME_OVER:
                List<Highscore.Entry> top5 = Highscore.top(5);

                batch.begin();

                batch.draw(assets.gameOverBackground, 0, 0, UI_WIDTH, UI_HEIGHT);

                highScoreFont.draw(batch, "HIGH SCORE", 100, 450);
                highScoreFont.draw(batch, "Your score: " + finalScore, 100, 600);

                int y = 380;
                for (Highscore.Entry e : top5) {
                    highScoreFont.draw(batch, e.name + ": " + e.score, 100, y);
                    y -= assets.highScoreFont.getLineHeight() + 10;
                }

                gameOverMenu.render(batch, 1380, 300);

                batch.end();
                break;

            case HIGH_SCORE:
                List<Highscore.Entry> top10 = Highscore.top(10);

                batch.begin();

                batch.draw(assets.menuBackground, 0, 0, UI_WIDTH, UI_HEIGHT);

                gameUiFont.draw(batch, "high scores", 100, 850);

                int highScoreY = 750;
                for (Highscore.Entry e : top10) {
                    gameUiFont.draw(batch, e.name + ": " + e.score, 100, highScoreY);
                    highScoreY -= assets.gameUiFont.getLineHeight() + 10;
                }

                highScoreMenu.render(batch, 1550, 100);

                batch.end();
                break;

            case CREDITS:
                batch.begin();

                batch.draw(assets.menuBackground, 0, 0, UI_WIDTH, UI_HEIGHT);

                credits.render(batch, 100, UI_WIDTH - 50);

                batch.end();
                break;

            default:
                break;
        }
    }


    /**
     * Updates one frame of game logic.
     *
     * <p>
     * Main update flow:
     * <ul>
     * <li>if the game is not running, only listen for start/restart input</li>
     * <li>if the game is running, process jump input</li>
     * <li>update background, player, score, obstacles, and platform</li>
     * <li>check for game over conditions</li>
     * </ul>
     */
    private void update() {
        float delta = Gdx.graphics.getDeltaTime();

        inputGate.update(delta);

        if (gameState == GameState.INTRO) {
            intro.update(delta);

            if (inputGate.canAcceptInput() && skipPressed()) {
                intro.skip();
            }

            if (intro.isFinished()) {
                gameState = GameState.MENU;
                inputGate.block(1f);
                audio.playMenuMusic();
            }

            return;

        }

        if (gameState == GameState.EXIT) {
            Gdx.app.exit();
            return;
        }

        if (gameState == GameState.MENU) {
            if (inputGate.canAcceptInput()) {
                menu.update();
            }

            GameState next = menu.consumeNextState();
            if (next != null) {
                if (next == GameState.RUNNING) {
                    startGame();
                } else {
                    gameState = next;
                    inputGate.block(1f);
                }
            }
            return;
        }

        if (gameState == GameState.SETTINGS) {
            if (inputGate.canAcceptInput()) {
                settings.update();
            }


            GameState next = settings.consumeNextState();
            if (next != null) {
                inputGate.block(0.25f);

                if (next == GameState.MUSIC) {
                    if (music) {
                        audio.muteMusic();
                        music = false;
                    } else {
                        audio.unMuteMusic();
                        music = true;
                    }
                    return;
                }

                if (next == GameState.SOUND) {
                    if (sound) {
                        audio.muteSound();
                        sound = false;
                    } else {
                        audio.unMuteSound();
                        sound = true;
                    }
                    return;
                }

                if (next == GameState.CREDITS) {
                    credits.reset();
                }

                gameState = next;
            }

            return;
        }

        if (gameState == GameState.NAME_INPUT) {

            Gdx.input.setInputProcessor(nameStage);

            nameStage.act(Gdx.graphics.getDeltaTime());
            nameStage.draw();
            if (isMobileWeb() && keyboardBridge != null) {

                String mobileText = keyboardBridge.getInputText();

                if (!nameField.getText().equals(mobileText)) {
                    nameField.setText(mobileText);
                }
            }
            gameState = GameState.NAME_INPUT;

            // ⭐ 1. Ge fokus vid första tangent (MEN inte SPACE/ENTER/mouse)
            if (!nameField.hasKeyboardFocus() && Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {

                if (!Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                    && !Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                    && !Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                    nameStage.setKeyboardFocus(nameField);
                }
            }

            // ⭐ 2. Confirm
            if (inputGate.canAcceptInput() && menuConfirmPressed()) {

                String input = nameField.getText().trim();

                if (input.isEmpty()) {
                    nameField.setMessageText("Player");
                    return;
                }

                playerName = input;

                if (isMobileWeb() && keyboardBridge != null) {
                    keyboardBridge.disableKeyboard();
                }

                Gdx.input.setInputProcessor(null);
                gameState = GameState.INTRO;

                inputGate.block(2f);
                audio.playIntroMusic();
            }
            return;
        }

        if (gameState == GameState.RUNNING && !gameHasStarted) {
            startBlinkTimer += delta;
        }

        if (gameState == GameState.HIGH_SCORE) {

            if (inputGate.canAcceptInput()) {
                highScoreMenu.update();
            }

            GameState next = highScoreMenu.consumeNextState();
            if (next != null) {
                gameState = next;
                inputGate.block(1f);
            }

            return;
        }

        if (gameState == GameState.DYING) {
            dyingTimer += delta;

            if (dyingTimer >= DYING_DURATION) {

                if (!scoreSaved) {
                    Highscore.save(playerName, (int) finalScore);
                    scoreSaved = true;
                }

                gameState = GameState.GAME_OVER;
                inputGate.block(1f);
                audio.playMenuMusic();
            }

            return;
        }


        if (gameState == GameState.CONFIRM_RESET) {

            if (inputGate.canAcceptInput()) {
                confirmMenu.update();
            }

            GameState next = confirmMenu.consumeNextState();

            if (next != null) {
                if (next == GameState.RESET_SCORE) {
                    Highscore.cleanHighScore();
                }

                gameState = GameState.SETTINGS;
                inputGate.block(0.25f);
            }

            return;
        }

        if (gameState == GameState.GAME_OVER) {

            if (inputGate.canAcceptInput()) {
                gameOverMenu.update();
            }

            GameState next = gameOverMenu.consumeNextState();
            if (next != null) {
                inputGate.block(0.25f);

                if (next == GameState.RUNNING) {
                    startGame();
                } else {
                    gameState = next;
                }
            }

            return;
        }

        if (gameState == GameState.CREDITS) {
            if (inputGate.canAcceptInput()) {
                credits.update();
            }

            GameState next = credits.consumeNextState();
            if (next != null) {
                gameState = next;
                inputGate.block(0.5f);
            }

            return;
        }

        if (jumpPressed() || touchPressed()) {
            player.jump(calculateJumpForce());
            gameHasStarted = true;
            audio.playJump();
        }

        if (!gameHasStarted) {
            return;
        }

        background.update(delta);
        player.update(delta, GRAVITY);
        player.clampToCeiling(ceiling);

        podX -= POD_SPEED * delta;
        timePlaying += delta;

        score.update(delta, true);
        obstacleManager.update(delta, timePlaying, getObstacleSpeed());

        if (player.hitsBottom() || player.hitsTop(ceiling) ||
            obstacleManager.collidesWith(
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight())) {

            gameOver();
        }

    }

    /**
     * Checks if jump/start input was pressed this frame.
     *
     * <p>
     * Supports both:
     * <ul>
     * <li>space key</li>
     * <li>left mouse button</li>
     * </ul>
     *
     * @return true if input was pressed this frame
     */


    private boolean jumpPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

    }

    private boolean menuConfirmPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

    }

    private boolean skipPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

    }

    private boolean isMobileWeb() {
        return Gdx.app.getType() == Application.ApplicationType.WebGL
            && Gdx.graphics.getWidth() < 1000;
    }

    private boolean touchPressed() {
        return Gdx.input.justTouched();
    }

    /**
     * Starts a new run.
     *
     * <p>
     * This resets the key game systems and state:
     * <ul>
     * <li>player position and velocity</li>
     * <li>score</li>
     * <li>obstacles</li>
     * <li>platform position</li>
     * <li>time-based difficulty scaling</li>
     * <li>final score display</li>
     * </ul>
     *
     * <p>
     * The player waits on the platform until the first jump input.
     */
    private void startGame() {
        player.reset(PLAYER_START_Y);
        score.reset();
        obstacleManager.reset();

        podX = POD_START_X;
        podY = POD_START_Y;
        timePlaying = 0f;
        finalScore = 0;

        scoreSaved = false;

        gameHasStarted = false;

        gameState = GameState.RUNNING;

        audio.playGameMusic();
    }

    private long getFinalScore() {
        return score.getVisualScore();
    }

    /**
     * Ends the current run and switches the game to GAME_OVER state.
     *
     * <p>
     * The current score is saved so it can still be displayed after the run ends.
     */


    private void gameOver() {
        if (gameState != GameState.DYING && gameState != GameState.GAME_OVER) {

            score.stopScore();
            finalScore = getFinalScore();

            gameState = GameState.DYING;
            dyingTimer = 0f;

            audio.stopJump();
            audio.playCrash();
        }
    }

    /**
     * Calculates the current jump force.
     *
     * <p>
     * As timePlaying increases, jump force is slightly reduced,
     * but never below 150.
     *
     * @return adjusted jump force
     */
    private float calculateJumpForce() {
        return Math.max(5f, JUMP_FORCE - (timePlaying / 10f) * 0.3f);
    }

    /**
     * Calculates current obstacle speed.
     *
     * <p>
     * Obstacle speed increases in steps over time to make the game harder.
     *
     * @return current obstacle movement speed
     */
    private float getObstacleSpeed() {
        return 4.0f + ((int) (timePlaying / 8)) * 0.8f;
    }

    /**
     * Updates the viewport when the game window is resized.
     * <p>
     * Ensures the game world keeps its aspect ratio and scales correctly
     * to different screen sizes.
     *
     * @param width  the new window width in pixels
     * @param height the new window width in pixels
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
        nameStage.getViewport().update(width, height, true);
    }

    private void handleCursor() {
        if (gameState != previousState) {

            if (gameState == GameState.NAME_INPUT) {
                Gdx.input.setCursorCatched(false);

            } else {

                if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
                    Gdx.input.setCursorCatched(true);
                }
            }

            previousState = gameState;
        }
    }

    /**
     * Cleans up resources when the game closes.
     *
     * <p>
     * Important:
     * LibGDX textures, SpriteBatch, and fonts must be disposed properly
     * to avoid memory leaks.
     */
    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        skin.dispose();
        nameStage.dispose();
    }
}
