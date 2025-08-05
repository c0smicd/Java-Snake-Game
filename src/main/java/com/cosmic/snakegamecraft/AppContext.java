package com.cosmic.snakegamecraft;

import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import static com.cosmic.snakegamecraft.util.Constants.FALLOUT_MODE_THRESHOLD;
import static com.cosmic.snakegamecraft.util.Constants.MODERN_MODE_THRESHOLD;

public class AppContext {

    private static Stage stage;
    private static String username = "guest";
    private static GameMode gameMode = GameMode.CLASSIC;
    private static GameSettings settings;
    private static int highScore = -1;

    private static boolean canModernMode, canFalloutMode;

    public static void setStage(Stage s){
        stage = s;
    }

    public static Stage getStage(){
        return stage;
    }

    public static void setUsername(String name){
        username = name;
    }

    @NotNull
    public static String getUsername(){
        return username;
    }

    //TODO: If logged in, fetch highscore from HighscoreManager
    public static boolean isLoggedIn() {
        return !username.equals("guest");
    }

    public static void setGameMode(GameMode mode) {
        gameMode = mode;

        System.out.println("Setting game mode: " + gameMode);
    }

    public static GameMode getGameMode() {
        return gameMode;
    }

    public static void setSettings(GameSettings settings) {
        AppContext.settings = settings;
    }
    public static GameSettings getSettings() {
        assert(settings != null) : "Settings have not been initialized!";
        return settings;
    }

    public static void setCanModernMode(int highScore) {
        AppContext.canModernMode = highScore > MODERN_MODE_THRESHOLD;
    }

    public static void setCanFalloutMode(int highScore) {

        System.out.println("Setting canFallout mode");
        AppContext.canFalloutMode = highScore > FALLOUT_MODE_THRESHOLD;

        System.out.println("Can fallout mode: " + AppContext.canFalloutMode);
    }

    public static boolean isCanModernMode() {
        return canModernMode;
    }

    public static boolean isCanFalloutMode() {
        return canFalloutMode;
    }
}

