package com.cosmic.snakegamecraft;

import com.cosmic.snakegamecraft.ui.GameMode;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class AppContext {

    private static Stage stage;
    private static String username = "guest";
    private static double multiplier = 1.0;
    private static GameMode gameMode = GameMode.CLASSIC;

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

    public static void setMultiplier(double m) {
        multiplier = m;
    }
    public static double getMultiplier() {
        return multiplier;
    }
    public static boolean isLoggedIn() {
        return !username.equals("guest");
    }

    public static void setGameMode(GameMode mode) {
        gameMode = mode;
    }

    public static GameMode getGameMode() {
        return gameMode;
    }
}
