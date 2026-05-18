package io.github.jumpyBirb.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.List;
import java.util.ArrayList;

/**
 * Holds and manages all textures used in the game.
 *
 * <p>This class centralizes asset loading so that:
 * <ul>
 *     <li>textures are created in one place</li>
 *     <li>supports grouped textures (e.g., multiple obstacle variations)</li>
 *     <li>other classes (like Main or GameRenderer) can easily access them</li>
 *     <li>resource cleanup is handled consistently</li>
 * </ul>
 *
 * <p>Why this class exists:
 * Previously, textures were created directly in Main, which made it cluttered
 * and harder to manage. By moving them into this class, we separate resource
 * handling from game logic.
 *
 * <p>Obstacle textures are stored as lists rather than single textures.
 * This allows obstacles to be randomly assigned different appearances
 * at spawn time, improving visual variety without changing game logic.
 *
 * <p>Design note:
 * Separating textures into lists allows systems like ObstacleManager
 * to perform random selection without hardcoding assets in gameplay logic.
 *
 * <p>Important note:
 * Each Texture allocates memory (often on the GPU). Therefore, all textures
 * must be properly disposed when the game closes to avoid memory leaks.
 *
 * <p>Usage:
 * <ul>
 *     <li>Create one instance of GameAssets in Main</li>
 *      <li>Pass it to classes that need textures (e.g. GameRenderer, ObstacleManager)</li> *     <li>Call {@code dispose()} when the game shuts down</li>
 * </ul>
 *
 * <p>Possible future improvements:
 * <ul>
 *     <li>use LibGDX AssetManager for asynchronous loading</li>
 *     <li>group assets by type (UI, player, obstacles, etc.)</li>
 *     <li>add sounds/music here as well</li>
 * </ul>
 */
public class GameAssets {
    public final Texture background = new Texture("background.png");
    public final Texture player = new Texture("player.png");
    public final Texture playerIdle = new Texture("motorbike1.png");
    public final Texture playerRiseFast = new Texture("motorbike4.png");
    public final Texture playerRise = new Texture("motorbike3.png");
    public final Texture playerFall = new Texture("motorbike5.png");
    public final Texture playerFallFast = new Texture("motorbike2.png");
    public final Texture playerCrash = new Texture("motorbikeCrash.png");
    public final Texture pod = new Texture("podPlatform.png");
    public final Texture parallax1 = new Texture("clouds-parallax1-3000x1080.png");
    public final Texture parallax2 = new Texture("city-parallax2-1920x1080.png");
    public final Texture parallax3 = new Texture("smog-parallax3-3000x1080.png");
    public final Texture parallax4 = new Texture("gradient-parallax4-1920x1080.png");
    public final Texture menuBackground = new Texture("menuBackground.jpg");
    public final Texture logo = new Texture("neon_rider_logo_hd2.png");
    public final Texture menuBackgroundNoBike = new Texture("menuBackground_empty.jpg");
    public final Texture gameOverBackground = new Texture("GameOverBackground.jpg");
    public final Texture startBackground = new Texture("startBackground.jpg");
    public final Texture waitingToStartBackground = new Texture("menuBackgroundWaitingToStart.png");

    public final Texture logoText = new Texture("neonRunnerTextTwoRows.png");

    public final BitmapFont creditsFont;
    public final BitmapFont introFont;
    public final BitmapFont uiFont;
    public final BitmapFont gameUiFont;
    public final BitmapFont menuFont;
    public final BitmapFont highScoreFont;

    public final List<Texture> bottomObstacles = new ArrayList<Texture>();
    public final List<Texture> topObstacles = new ArrayList<Texture>();

    public final Music introMusic = com.badlogic.gdx.Gdx.audio.newMusic(
        com.badlogic.gdx.Gdx.files.internal("intro-music.ogg")
    );

    public final Music menuMusic = com.badlogic.gdx.Gdx.audio.newMusic(
        com.badlogic.gdx.Gdx.files.internal("menu-music.ogg")
    );

    public final Music gameMusic = com.badlogic.gdx.Gdx.audio.newMusic(
        com.badlogic.gdx.Gdx.files.internal("game-music.ogg")
    );

    public final Sound jumpSound = com.badlogic.gdx.Gdx.audio.newSound(
        com.badlogic.gdx.Gdx.files.internal("jump.ogg")
    );

    public final Sound crashSound = com.badlogic.gdx.Gdx.audio.newSound(
        com.badlogic.gdx.Gdx.files.internal("crash.ogg")
    );

    public GameAssets() {
        bottomObstacles.add(new Texture("retrowave_skyscrapers_bottom-01.png"));
        bottomObstacles.add(new Texture("retrowave_skyscrapers_bottom-02.png"));
        bottomObstacles.add(new Texture("retrowave_skyscrapers_bottom-03.png"));

        topObstacles.add(new Texture("retrowave_skyscrapers_top-01.png"));
        topObstacles.add(new Texture("retrowave_skyscrapers_top-02.png"));
        topObstacles.add(new Texture("retrowave_skyscrapers_top-03.png"));

        introMusic.setLooping(true);
        menuMusic.setLooping(true);
        gameMusic.setLooping(true);

        introMusic.setVolume(0.6f);
        menuMusic.setVolume(0.6f);
        gameMusic.setVolume(0.6f);


        creditsFont = new BitmapFont(Gdx.files.internal("ui/credits-intro-font.fnt"));
        creditsFont.getData().setScale(1.20f);
        introFont = new BitmapFont(Gdx.files.internal("ui/credits-intro-font.fnt"));
        uiFont = new BitmapFont(Gdx.files.internal("ui/ui-font.fnt"));
        uiFont.getData().setScale(1.5f);
        gameUiFont = new BitmapFont(Gdx.files.internal("ui/highscore-font.fnt"));
        gameUiFont.getData().setScale(1.5f);
        highScoreFont = new BitmapFont(Gdx.files.internal("ui/highscore-font.fnt"));
        highScoreFont.getData().setScale(1.5f);
        menuFont = new BitmapFont(Gdx.files.internal("ui/menu-large.fnt"));


    /*
        creditsFont = new BitmapFont();
        introFont = new BitmapFont();
        uiFont = new BitmapFont();
        gameUiFont = new BitmapFont();
        highScoreFont = new BitmapFont();
        menuFont = new BitmapFont();
 */
    }

    /**
     * Releases all textures from memory.
     *
     * <p>This method MUST be called when the game closes.
     * Otherwise, memory (especially GPU memory) will leak.
     */
    public void dispose() {
        background.dispose();
        player.dispose();
        playerRiseFast.dispose();
        playerRise.dispose();
        playerFall.dispose();
        playerFallFast.dispose();
        playerIdle.dispose();
        playerCrash.dispose();
        pod.dispose();
        parallax1.dispose();
        parallax2.dispose();
        parallax3.dispose();
        parallax4.dispose();
        menuBackground.dispose();
        menuBackgroundNoBike.dispose();
        gameOverBackground.dispose();
        waitingToStartBackground.dispose();
        logo.dispose();
        logoText.dispose();
        creditsFont.dispose();
        introFont.dispose();
        uiFont.dispose();
        gameUiFont.dispose();
        menuFont.dispose();
        startBackground.dispose();

        for (Texture t : bottomObstacles) {
            t.dispose();
        }
        for (Texture t : topObstacles) {
            t.dispose();
        }

        introMusic.dispose();
        menuMusic.dispose();
        gameMusic.dispose();
        jumpSound.dispose();
        crashSound.dispose();
    }
}
