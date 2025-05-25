package com.cosmic.snakegamecraft;

import atlantafx.base.theme.PrimerDark;
import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

         AppContext.setStage(primaryStage);
         primaryStage.setResizable(false);
         new SceneManager(primaryStage).showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
