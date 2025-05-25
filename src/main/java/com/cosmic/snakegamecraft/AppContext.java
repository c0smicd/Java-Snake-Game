package com.cosmic.snakegamecraft;

import javafx.stage.Stage;

public class AppContext {

    private static Stage stage;
    private static String username;

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
}
