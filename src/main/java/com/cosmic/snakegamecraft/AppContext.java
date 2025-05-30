package com.cosmic.snakegamecraft;

import javafx.stage.Stage;

public class AppContext {

    private static Stage stage;
    private static String username = "guest";
    private static double multiplier = 1.0;

    public static void setStage(Stage s){
        stage = s;
    }

    public static Stage getStage(){
        return stage;
    }

    public static void setUsername(String name){
        username = name;
    }

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
}
