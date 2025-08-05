package com.cosmic.snakegamecraft.logic;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

public class PlaySound {

    public enum Sound {
        APPLE,
        SPEEDUP
    }

    private static MediaPlayer mediaPlayer;

    private static Media appleSound;
    private static Media speedUpSound;

    public static void loadSounds() {
        appleSound = load("apple.mp3");
        speedUpSound = load("speedUp.mp3");
    }

    private static Media load(String path) {
        return new Media(Objects.requireNonNull(PlaySound.class.getResource("/sounds/" + path)).toString());
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
        }
    }
}
