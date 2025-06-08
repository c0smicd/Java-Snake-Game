package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.logic.HighscoreManager;
import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static com.cosmic.snakegamecraft.util.Constants.SHOWN_HIGHSCORES;


public class HighscoreController {

    private SceneManager sceneManager = new SceneManager(AppContext.getStage());

    @FXML
    private AnchorPane highscorePane;

    @FXML
    private Button backToMenu;

    @FXML
    private Label highScoreLabel;


    @FXML
    private void initialize() {
        highScoreLabel.setText(HighscoreManager.getTopEntries(AppContext.getUsername(), SHOWN_HIGHSCORES));
    }

    @FXML
    private void handleBackToMenu(){
        sceneManager.showMenu();
    }
}
