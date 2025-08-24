package com.cosmic.snakegamecraft.logic;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

import static com.cosmic.snakegamecraft.logic.SpriteManager.RESOURCE_PATH;

public class PlaySound {

    public enum Sound {
        APPLE,
        SPEEDUP
    }

    private static Media appleSound;
    private static Media speedUpSound;

    public static void loadSounds() {
        appleSound = load("apple.mp3");
        speedUpSound = load("speedup.mp3");
    }

    private static Media load(String path) {
        return new Media(Objects.requireNonNull(
                PlaySound.class.getResource(RESOURCE_PATH + path)
        ).toExternalForm());
    }

    public static void playSound(Sound sound) {
        MediaPlayer mediaPlayer;
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
