package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jumpyBirb.game.GameState;

public class Settings {

    private int settingsIndex = 0;

    private final BitmapFont font;
    private final Viewport viewport;

    private float renderX;
    private float renderY;
    private boolean music = true;
    private boolean sound = true;

    private GameState nextState = null;

    private final String[] items = {
        "Reset High-Score",
        "Music ON/OFF",
        "Sound ON/OFF",
        "Credits",
        "Change Name",
        "Menu"
    };

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

        // KEYBOARD NAVIGATION

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            settingsIndex =
                (settingsIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            settingsIndex =
                (settingsIndex - 1 + items.length)
                    % items.length;
        }

        // KEYBOARD SELECT

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            select();
            return;
        }

        // TOUCH / MOBILE / HTML

        if (Gdx.input.justTouched()) {

            Vector3 touch =
                new Vector3(
                    Gdx.input.getX(),
                    Gdx.input.getY(),
                    0);

            viewport.unproject(touch);

            float lineHeight =
                font.getLineHeight() + 40;

            String[] displayItems = {
                "Reset High-Score",
                "Music: " + (music ? "ON" : "OFF"),
                "Sound: " + (sound ? "ON" : "OFF"),
                "Credits",
                "Change Name",
                "Menu"
            };

            for (int i = 0;
                 i < displayItems.length;
                 i++) {

                float itemY =
                    renderY - i * lineHeight;

                String renderedText =
                    (i == settingsIndex)
                        ? "> " + displayItems[i]
                        : displayItems[i];

                GlyphLayout layout =
                    new GlyphLayout(
                        font,
                        renderedText);

                boolean insideX =
                    touch.x >= renderX &&
                        touch.x <= renderX + layout.width;

                boolean insideY =
                    touch.y <= itemY &&
                        touch.y >= itemY - lineHeight;

                if (insideX && insideY) {

                    settingsIndex = i;
                    select();
                    return;
                }
            }
        }
    }

    private void select() {

        GameState state =
            getStateForIndex(settingsIndex);

        if (state != null) {
            nextState = state;
        }
    }

    private GameState getStateForIndex(int index) {

        switch (index) {

            case 0:
                return GameState.CONFIRM_RESET;

            case 1:
                return GameState.MUSIC;

            case 2:
                return GameState.SOUND;

            case 3:
                return GameState.CREDITS;

            case 4:
                return GameState.NAME_INPUT;

            case 5:
                return GameState.MENU;

            default:
                return null;
        }
    }

    public void render(SpriteBatch batch,
                       float startX,
                       float startY,
                       boolean music,
                       boolean sound) {
        this.music = music;
        this.sound = sound;

        renderX = startX;
        renderY = startY;

        float lineHeight =
            font.getLineHeight() + 40;

        String[] displayItems = {
            "Reset High-Score",
            "Music: " + (music ? "ON" : "OFF"),
            "Sound: " + (sound ? "ON" : "OFF"),
            "Credits",
            "Change Name",
            "Menu"
        };

        for (int i = 0;
             i < displayItems.length;
             i++) {

            String text =
                (i == settingsIndex)
                    ? "> " + displayItems[i]
                    : displayItems[i];

            font.draw(batch,
                text,
                startX,
                startY - i * lineHeight);
        }
    }

    public void reset() {
        settingsIndex = 0;
        nextState = null;
    }
}
