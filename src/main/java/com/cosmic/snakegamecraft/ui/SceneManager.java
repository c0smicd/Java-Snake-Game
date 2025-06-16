package com.cosmic.snakegamecraft.ui;

import com.cosmic.snakegamecraft.AppContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }


    public void showMenu(){
        switchScene("menu-view.fxml", "Main Menu");
    }

    public void showSettings(){
        switchScene("settings-view.fxml", "Settings");
    }

    public void startGame(GameMode mode){

        switch (mode){
            case CLASSIC -> switchScene("classic-game-view.fxml", "Game - Classic Mode");
            case MODERN -> switchScene("game-modern-view.fxml", "Game - Modern Mode");
            case CRAZY -> switchScene("game-crazy-view.fxml", "Game - Crazy Mode");
        }

        AppContext.setGameMode(mode);
    }

    public void showHighscores(){
        switchScene("highscore-view.fxml", "Highscores");
    }

    public void switchScene(String fxmlFile, String title) {
        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/" + fxmlFile)));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


}
