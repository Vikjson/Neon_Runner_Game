package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Menu {

    private int menuIndex = 0;
    private final String[] items;
    private GameState[] states;
    private GameState nextState = null;
    private final BitmapFont font;
    private float renderX;
    private float renderY;

    public Menu(String[] items, GameState[] states, BitmapFont font) {
        this.items = items;
        this.states = states;
        this.font = font;
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

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            menuIndex = (menuIndex + 1) % items.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuIndex = (menuIndex - 1 + items.length) % items.length;
        }

        // ⭐ MOBILE TOUCH
        if (Gdx.input.justTouched()) {

            float touchY =
                Gdx.graphics.getHeight() - Gdx.input.getY();

            float lineHeight =
                font.getLineHeight() + 10;

            for (int i = 0; i < items.length; i++) {

                float itemY =
                    renderY - i * lineHeight;

                if (touchY > itemY - lineHeight
                    && touchY < itemY + 10) {

                    menuIndex = i;
                    select();
                    break;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            select();
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            select();
        }
    }

    private void select() {
        nextState = states[menuIndex];
    }

    public void render(SpriteBatch batch, float startX, float startY) {

        renderX = startX;
        renderY = startY;

        float lineHeight = font.getLineHeight() + 10;

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
}
