package com.cosmic.snakegamecraft.logic;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

public class PlaySound {
    private static MediaPlayer mediaPlayer = null;

    public enum Sound {
        APPLE,
        SPEEDUP,
        STAR,
        GAMEOVER
    }

    private static Media appleSound;
    private static Media speedUpSound;
    private static Media starSound;
    private static Media gameOverSound;

    public static void loadSounds() {
        appleSound = load("apple.mp3");
        speedUpSound = load("speedup.mp3");
        starSound = load("star.mp3");
        gameOverSound = load("gameOver.mp3");
    }

    private static Media load(String path) {
        return new Media(Objects.requireNonNull(
                PlaySound.class.getResource("/sounds/" + path)
        ).toExternalForm());
    }

    public static void playSound(Sound sound) {

        switch (sound) {
            case APPLE -> {
                mediaPlayer = new MediaPlayer(appleSound);
                mediaPlayer.play();
            }
            case SPEEDUP -> {
                mediaPlayer = new MediaPlayer(speedUpSound);
                mediaPlayer.play();
            }
            case STAR -> {
                mediaPlayer = new MediaPlayer(starSound);
                mediaPlayer.play();
            }
            case GAMEOVER -> {
                mediaPlayer = new MediaPlayer(gameOverSound);
                mediaPlayer.play();
            }
        }
    }

    public static void stopSound() {
        if (mediaPlayer != null && mediaPlayer.getMedia() == starSound) {
            mediaPlayer.stop();
        }
    }
}
