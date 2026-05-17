package io.github.jumpyBirb.data;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jumpyBirb.game.GameState;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;


public class Settings {

    private int settingsIndex = 0;
    private final BitmapFont font;
    private boolean inResolutionMenu = false;
    private int resolutionIndex = 0;
    private boolean resolutionChanged = false;
    private float renderX;
    private float renderY;
    private final Viewport viewport;

     public boolean consumeResolutionChanged() {
        boolean temp = resolutionChanged;
        resolutionChanged = false;
        return temp;
    }

    private final String[] resolutions = {
        "1280 x 720",
        "1920 x 1080",
        "FULLSCREEN"
    };


    private final String[] items = {"Reset High-Score", "Music ON/OFF", "Sound ON/OFF", "FULLSCREEN ON/OFF", "CREDITS", "Change name", "MENU"};
    private GameState nextState = null;


    public Settings(BitmapFont font, Viewport viewport) {
        this.font = font;
        this.viewport = viewport;
    }

    public void update() {
        handleInput();
    }

    public GameState consumeNextState() {
        GameState state = nextState;
        nextState = null;
        return state;
    }

    private void handleInput() {

        // DESKTOP KEYBOARD

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            settingsIndex =
                (settingsIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            settingsIndex =
                (settingsIndex - 1 + items.length)
                    % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            select();
        }

        // DESKTOP MOUSE

        if (Gdx.app.getType()
            != Application.ApplicationType.WebGL
            && Gdx.input.isButtonJustPressed(
            Input.Buttons.LEFT)) {

            select();
        }

        // MOBILE / WEB TOUCH

        if (Gdx.input.justTouched()) {

            Vector3 touch =
                new Vector3(
                    Gdx.input.getX(),
                    Gdx.input.getY(),
                    0);

            viewport.unproject(touch);

            float touchX = touch.x;
            float touchY = touch.y;

            float lineHeight =
                font.getLineHeight() + 40;

            String[] displayItems = {
                "Reset High-Score",
                "Music: ON/OFF",
                "Sound: ON/OFF",
                "Fullscreen ON/OFF",
                "Credits",
                "Change Name",
                "Menu"
            };

            for (int i = 0;
                 i < displayItems.length;
                 i++) {

                float itemY =
                    renderY - i * lineHeight;

                GlyphLayout layout =
                    new GlyphLayout(
                        font,
                        displayItems[i]);

                float itemWidth =
                    layout.width;

                boolean insideX =
                    touchX >= renderX &&
                        touchX <= renderX + itemWidth;

                boolean insideY =
                    touchY >= itemY - lineHeight &&
                        touchY <= itemY + 40;

                if (insideX && insideY) {

                    settingsIndex = i;
                    select();
                    break;
                }
            }
        }
    }

    private void select() {

        switch (settingsIndex) {

            case 0:
                nextState = GameState.CONFIRM_RESET;
                break;

            case 1:
                nextState = GameState.MUSIC;
                break;

            case 2:
                nextState = GameState.SOUND;
                break;

            case 3:
                toggleFullscreen();
                break;

            case 4:
                nextState = GameState.CREDITS;
                break;

            case 5:
                nextState = GameState.NAME_INPUT;
                break;

            case 6:
                nextState = GameState.MENU;
                break;

            default:
                nextState = null;
                break;
        }
    }

    public void render(SpriteBatch batch, float startX, float startY, boolean music, boolean sound) {

        renderX = startX;
        renderY = startY;
        float lineHeight = font.getLineHeight() + 40;

        String[] displayItems = {
            "Reset High-Score",
            "Music: " + (music ? "ON" : "OFF"),
            "Sound: " + (sound ? "ON" : "OFF"),
            "Fullscreen ON/OFF ",
            "Credits",
            "Change Name",
            "Menu"
        };

        for (int i = 0; i < displayItems.length; i++) {
            String text = (i == settingsIndex) ? "> " + displayItems[i] : displayItems[i];
            font.draw(batch, text, startX, startY - i * lineHeight);
        }
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }
}
