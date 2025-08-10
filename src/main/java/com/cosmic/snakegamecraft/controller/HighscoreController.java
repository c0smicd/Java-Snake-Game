package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.logic.HighscoreManager;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static com.cosmic.snakegamecraft.util.Constants.SHOWN_HIGHSCORES;


public class HighscoreController {

    private SceneManager sceneManager = new SceneManager(AppContext.getStage());

    @FXML
    private AnchorPane highscorePane;

    @FXML
    private Button backToMenu;

    @FXML
    private TextFlow highScoreTextClassic;

    @FXML
    private TextFlow highScoreTextModern;

    @FXML
    private TextFlow highScoreTextFallout;


    @FXML
    private void initialize() {
        highScoreTextClassic.getChildren().clear();
        highScoreTextModern.getChildren().clear();
        highScoreTextFallout.getChildren().clear();

        highScoreTextClassic.getChildren().addAll(HighscoreManager.getTopEntries(GameMode.CLASSIC, AppContext.getUsername(), SHOWN_HIGHSCORES));
        highScoreTextModern.getChildren().addAll(HighscoreManager.getTopEntries(GameMode.MODERN, AppContext.getUsername(), SHOWN_HIGHSCORES));
        highscorePane.getChildren().addAll(HighscoreManager.getTopEntries(GameMode.FALLOUT, AppContext.getUsername(), SHOWN_HIGHSCORES));
    }

    @FXML
    private void handleBackToMenu(){
        sceneManager.showMenu();
    }
}
