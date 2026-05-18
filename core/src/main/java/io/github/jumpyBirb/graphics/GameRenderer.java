package io.github.jumpyBirb.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jumpyBirb.data.Obstacle;
import io.github.jumpyBirb.data.Player;
import io.github.jumpyBirb.data.Score;
import io.github.jumpyBirb.game.GameState;
import io.github.jumpyBirb.game.ObstaclePair;
import io.github.jumpyBirb.game.ParallaxBackground;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

import java.util.List;

/**
 * Responsible for drawing the current game state to the screen.
 *
 * <p>
 * This class handles rendering only. It does not update game logic,
 * move objects, handle collisions, or process input.
 *
 * <p>
 * Its job is to take the current state of the game and draw:
 * <ul>
 * <li>background and parallax layers</li>
 * <li>starting platform</li>
 * <li>player</li>
 * <li>obstacles</li>
 * <li>score text</li>
 * <li>game over text</li>
 * </ul>
 *
 * <p>
 * Why this class exists:
 * Previously, drawing code was inside Main, mixed together with update logic,
 * input handling, and game state changes. By moving rendering into its own
 * class,
 * the code becomes easier to read and easier to maintain.
 *
 * <p>
 * This class is implemented as a record because it mainly stores a small set
 * of rendering-related dependencies:
 * <ul>
 * <li>{@code SpriteBatch} for drawing</li>
 * <li>{@code BitmapFont} for text</li>
 * <li>{@code GameAssets} for textures</li>
 * <li>{@code OrthographicCamera} for setting the viewport size</li>
 * <li>screen size values for positioning and scaling</li>
 * </ul>
 *
 * <p>
 * Important design note:
 * GameRenderer should only draw what it is told to draw.
 * It should not decide game rules such as when the game starts, when the player
 * dies,
 * or how obstacles move.
 */
public class GameRenderer {
    private final SpriteBatch batch;
    private final GameAssets assets;
    private final OrthographicCamera camera;
    private final float worldWidth;
    private final float worldHeight;

    public GameRenderer(SpriteBatch batch,
                        GameAssets assets,
                        OrthographicCamera camera,
                        float worldWidth,
                        float worldHeight) {
        this.batch = batch;
        this.assets = assets;
        this.camera = camera;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    private Texture getPlayerTexture(Player player) {
        float velocity = player.getVelocity();

        if (velocity > 3f) {
            return assets.playerRiseFast;   // motorbike4
        } else if (velocity > 0.3f) {
            return assets.playerRise;       // motorbike3
        } else if (velocity < -3f) {
            return assets.playerFallFast;   // motorbike2
        } else if (velocity < -0.3f) {
            return assets.playerFall;       // motorbike5
        } else {
            return assets.playerIdle;       //  motorbike1 (start)
        }
    }

    private float getPlayerRotation(Player player) {
        float velocity = player.getVelocity();
        float clampedVelocity = Math.max(-8f, Math.min(8f, velocity));
        return clampedVelocity * 3f;
    }

    /**
     * Draws one full frame of the game.
     *
     * <p>
     * This method:
     * <ul>
     * <li>clears the screen</li>
     * <li>draws the static background</li>
     * <li>draws the parallax background layers</li>
     * <li>draws the platform, player, and obstacles</li>
     * <li>draws the current score</li>
     * <li>draws game over text if the game is over</li>
     * </ul>
     *
     * <p>
     * Everything passed in here should already have been updated elsewhere.
     * This method should only render the current state, not modify it.
     *
     * @param background parallax background system
     * @param player     current player object
     * @param score      score object used for displaying current score
     * @param gameState  current game state
     * @param finalScore score to display on game over
     * @param podX       x-position of the starting platform
     * @param podY       y-position of the starting platform
     * @param podWidth   width of the starting platform
     * @param podHeight  height of the starting platform
     */
    public void draw(
        ParallaxBackground background,
        Player player,
        List<ObstaclePair> pairs,
        Score score,
        GameState gameState,
        long finalScore,
        String playerName,
        float podX,
        float podY,
        float podWidth,
        float podHeight) {
        // RENDERER SKA INTE RITA I MENY, HIGH_SCORE, SETTINGS
        if (gameState != GameState.RUNNING && gameState != GameState.DYING && gameState != GameState.GAME_OVER) {
            return;
        }
        // Clear the screen before drawing the next frame.
        ScreenUtils.clear(Color.BLACK); //flyttar tillfälligt till main

        // describes where things in the game world should be rendered onto the screen.
        batch.setProjectionMatrix(camera.combined);

        // Begin one SpriteBatch drawing session.
        batch.begin();

        // Draw static base background first.
        batch.draw(assets.background, 0, 0, worldWidth, worldHeight);

        // Draw moving parallax layers on top of the base background.
        background.draw(batch);

        // Draw the starting platform.
        batch.draw(assets.pod, podX, podY, podWidth, podHeight);

        // Draw Obstacle
        for (ObstaclePair pair : pairs) {
            Obstacle bottom = pair.getBottom();
            Obstacle top = pair.getTop();

            batch.draw(
                bottom.getTexture(),
                bottom.getX(),
                bottom.getY(),
                bottom.getWidth(),
                bottom.getHeight());

            batch.draw(
                top.getTexture(),
                top.getX(),
                top.getY(),
                top.getWidth(),
                top.getHeight());
        }


        // Draw the player.
        Texture playerTexture;
        float rotation;

        if (gameState == GameState.DYING) {
            playerTexture = assets.playerCrash;
            rotation = getPlayerRotation(player);
        } else {
            playerTexture = getPlayerTexture(player);
            rotation = getPlayerRotation(player);
        }

        batch.draw(
            playerTexture,
            player.getX(),
            player.getY(),
            player.getWidth() / 2f,
            player.getHeight() / 2f,
            player.getWidth(),
            player.getHeight(),
            1f,
            1f,
            rotation,
            0,
            0,
            playerTexture.getWidth(),
            playerTexture.getHeight(),
            false,
            false
        );

        // End the SpriteBatch drawing session.
        batch.end();

        //

        batch.setProjectionMatrix(
            new Matrix4().setToOrtho2D(
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()));

        // Start new batch for score
        batch.begin();

        // Set size and color on font
        assets.gameUiFont.setUseIntegerPositions(false);
        assets.gameUiFont.setColor(Color.WHITE);

        // Draw the current score near the top of the screen.
               assets.gameUiFont.draw(batch, playerName + " score: " + score.getVisualScore(),
            80, Gdx.graphics.getHeight() - 40);


        // If the game is over, draw a game over message and final score.
        if (gameState == GameState.GAME_OVER) {
            assets.gameUiFont.draw(batch,
                "GAME OVER!\nYour score: " + finalScore,
                Gdx.graphics.getWidth() / 2f - 120,
                Gdx.graphics.getHeight() / 2f + 40);
        }

        // End the score drawing session.
        batch.end();

    }
}
