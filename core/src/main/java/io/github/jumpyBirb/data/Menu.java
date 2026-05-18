package io.github.jumpyBirb.data;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jumpyBirb.game.GameState;

public class Menu {

    private int menuIndex = 0;
    private final String[] items;
    private GameState[] states;
    private GameState nextState = null;
    private final BitmapFont font;
    private float renderX;
    private float renderY;
    private final Viewport viewport;


    public Menu(String[] items, GameState[] states, BitmapFont font, Viewport viewport) {
        this.items = items;
        this.states = states;
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            menuIndex = (menuIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuIndex = (menuIndex - 1 + items.length) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            select();
            return;
        }

        if (Gdx.input.justTouched()) {

            Vector3 touch = new Vector3(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0
            );

            viewport.unproject(touch);

            float lineHeight = font.getLineHeight() + 40;

            for (int i = 0; i < items.length; i++) {

                float itemY =
                    renderY - i * lineHeight;

                String renderedText =
                    (i == menuIndex)
                        ? "> " + items[i]
                        : items[i];

                GlyphLayout layout =
                    new GlyphLayout(font, renderedText);

                boolean insideX =
                    touch.x >= renderX &&
                        touch.x <= renderX + layout.width;

                boolean insideY =
                    touch.y <= itemY &&
                        touch.y >= itemY - lineHeight;

                if (insideX && insideY) {

                    menuIndex = i;
                    select();
                    return;
                }
            }
        }
    }



    private void select() {

        if (menuIndex < 0
            || menuIndex >= states.length) {
            return;
        }

        nextState = states[menuIndex];
    }

    public void render(SpriteBatch batch, float startX, float startY) {

        renderX = startX;
        renderY = startY;

        float lineHeight = font.getLineHeight() + 40;

        for (int i = 0; i < items.length; i++) {

            String text =
                (i == menuIndex)
                    ? "> " + items[i]
                    : items[i];

            font.draw(batch,
                text,
                startX,
                startY - i * lineHeight);
        }
    }

    public void reset() {
        menuIndex = 0;
        nextState = null;
    }
}
