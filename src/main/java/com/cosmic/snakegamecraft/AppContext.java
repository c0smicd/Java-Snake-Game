package com.cosmic.snakegamecraft;

import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class AppContext {

    private static Stage stage;
    private static String username = "guest";
    private static GameMode gameMode = GameMode.CLASSIC;
    private static GameSettings settings;
    private static int highScore = -1;

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
}

