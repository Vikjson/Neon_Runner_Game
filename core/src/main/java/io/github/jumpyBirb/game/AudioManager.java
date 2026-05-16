package io.github.jumpyBirb.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import io.github.jumpyBirb.graphics.GameAssets;

public class AudioManager {
    private final Music introMusic;
    private final Music menuMusic;
    private final Music gameMusic;
    private final Sound jumpSound;
    private final Sound crashSound;
    private boolean sound = true;
    private boolean music = true;

    public AudioManager(GameAssets assets) {
        this.introMusic = assets.introMusic;
        this.menuMusic = assets.menuMusic;
        this.gameMusic = assets.gameMusic;
        this.jumpSound = assets.jumpSound;
        this.crashSound = assets.crashSound;
    }

    public void playIntroMusic() {
        if (!music) {
            return;
        } else {
            stopAllMusic();
            introMusic.play();
        }
        return;
    }

    public void playMenuMusic() {
        if (!music) {
            return;
        } else {
            stopAllMusic();
            menuMusic.play();
        }
        return;
    }

    public void playGameMusic() {
        if (!music) {
            return;
        } else {
            stopAllMusic();
            gameMusic.play();
        }
        return;
    }

    public void playJump() {
        if (!sound) {
            return;
        } else {
            jumpSound.play(0.09f);
        }
        return;
    }

    public void stopJump() {
        if (!sound) {
            return;
        } else {
            jumpSound.stop();
        }
        return;
    }

    public void playCrash() {
        if (!sound) {
            return;
        } else {
            crashSound.play(0.25f);
        }
        return;
    }

    private void stopAllMusic() {
        if (!music) {
            return;
        } else {
            introMusic.stop();
            menuMusic.stop();
            gameMusic.stop();
        }
        return;
    }

    public void muteSound() {
        sound = false;
    }

    public void muteMusic() {
        stopAllMusic();
        music = false;
    }

    public void unMuteSound() {
        sound = true;
    }

    public void unMuteMusic() {
        music = true;
        playMenuMusic();
    }

    public void unlockAudio() {

        introMusic.play();
        introMusic.stop();

        menuMusic.play();
        menuMusic.stop();

        gameMusic.play();
        gameMusic.stop();

        jumpSound.play(0f);
        crashSound.play(0f);
    }

}
