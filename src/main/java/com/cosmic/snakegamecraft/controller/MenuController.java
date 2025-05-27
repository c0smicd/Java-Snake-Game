package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.fxml.FXML;


public class MenuController {

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());


    @FXML
    public void handleClassicMode() {
        sceneManager.startGame(GameMode.CLASSIC);
    }

    @FXML
    public void handleModernMode() {
        sceneManager.startGame(GameMode.MODERN);
    }

    @FXML
    public void handleCrazyMode() {
        sceneManager.startGame(GameMode.CRAZY);
    }

    @FXML
    public void handleSettings() {
        sceneManager.showSettings();
    }

    @FXML
    public void handleHighscores() {
        sceneManager.showHighscores();
    }
}
