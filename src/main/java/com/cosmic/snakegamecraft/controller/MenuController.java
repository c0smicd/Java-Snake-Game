package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

import javax.tools.Tool;


public class MenuController {

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane loginOverlay;
    @FXML
    private TextField usernameField;

    @FXML
    public void initialize() {
        if(AppContext.isLoggedIn()){
            loginButton.setTooltip(new Tooltip("You are logged in as " + AppContext.getUsername()));
        }else{
            loginButton.setTooltip(new Tooltip("You are not logged in. Click to log in."));
        }
    }

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


    @FXML
    private void handleLogin() {
        loginOverlay.setVisible(true);
        usernameField.setText("");
    }

    @FXML
    private void submitLogin() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty()) {
            AppContext.setUsername(username);
        }
        loginOverlay.setVisible(false);
        loginButton.setTooltip(new Tooltip("You are logged in as " + AppContext.getUsername()));
        SettingsLoader.loadSettings(AppContext.getUsername());
    }

    @FXML
    private void cancelLogin() {
        loginOverlay.setVisible(false);
    }

}
