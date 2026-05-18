package io.github.jumpyBirb.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.jumpyBirb.game.GameState;

public class Credits {

    private GameState nextState = null;

    private final BitmapFont creditFont;
    private final BitmapFont menuFont;

    private final GlyphLayout layout = new GlyphLayout();

    private String creditsText;

    private float scrollY;

    private static final float SCROLL_SPEED = 80f;

    public Credits(BitmapFont creditFont, BitmapFont menuFont) {

        this.creditFont = creditFont;
        this.menuFont = menuFont;

        FileHandle file = Gdx.files.internal("credits.txt");

        creditsText = file.readString();

        scrollY = -300f;
    }

    public void reset() {
        scrollY = -300f;
    }

    public void update() {

        float delta = Gdx.graphics.getDeltaTime();

        scrollY += SCROLL_SPEED * delta;

        if (Gdx.input.justTouched()
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            nextState = GameState.SETTINGS;
        }
    }

    public GameState consumeNextState() {
        GameState state = nextState;
        nextState = null;
        return state;
    }

    public void render(SpriteBatch batch, float startX, float rightX) {

        creditFont.draw(batch, creditsText, startX, scrollY);

        String backText = "> settings";

        layout.setText(menuFont, backText);

        float xUiText = rightX - layout.width;

        menuFont.draw(batch, backText, xUiText, 100);
    }
}
