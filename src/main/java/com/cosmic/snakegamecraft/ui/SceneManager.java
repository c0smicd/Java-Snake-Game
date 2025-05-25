package com.cosmic.snakegamecraft.ui;

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

    public void showLogin(){
        switchScene("login-view.fxml", "Login");
    }

    public void showMenu(String username){
        switchScene("menu-view.fxml", "Main Menu");

        //TODO: Pass username to controller
    }

    public void showSettings(){
        switchScene("settings-view.fxml", "Settings");
    }

    public void startGame(GameMode mode){
        switchScene("game-view.fxml", "Game - " + mode.toString());
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
