package com.cosmic.snakegamecraft;

import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        AppContext.setStage(primaryStage);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResource("/icon/icon.png").toExternalForm()));

        new SceneManager(primaryStage).showMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
