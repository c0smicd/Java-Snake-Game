package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.tools.Tool;
import java.util.Random;

import static com.cosmic.snakegamecraft.util.Tips.TIPS;


public class MenuController {

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane loginOverlay;
    @FXML
    private TextField usernameField;
    @FXML
    private Label tipBox;


    @FXML
    public void initialize() {

        Random rand = new Random();

        String tip = TIPS.get(rand.nextInt(TIPS.size()));
        if(AppContext.isLoggedIn()){
            loginButton.setTooltip(new Tooltip("You are logged in as " + AppContext.getUsername()));
            tipBox.setText(tip);
        }else{
            loginButton.setTooltip(new Tooltip("You are not logged in. Click to log in."));
            tipBox.setText(TIPS.getLast());

        }


        Timeline tipCycle = new Timeline(
                new KeyFrame(Duration.minutes(3), e -> {
                    String newTip = TIPS.get(rand.nextInt(TIPS.size()));
                    if(!AppContext.isLoggedIn()){
                        tipBox.setText(TIPS.getLast());
                    }else{
                        tipBox.setText(newTip);
                    }
                })
        );

        tipCycle.setCycleCount(Timeline.INDEFINITE);
        tipCycle.play();



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
            loginOverlay.setVisible(false);
            loginButton.setTooltip(new Tooltip("You are logged in as " + AppContext.getUsername()));
            SettingsLoader.loadSettings(AppContext.getUsername());
        }
    }

    @FXML
    private void cancelLogin() {
        loginOverlay.setVisible(false);
    }

    @FXML
    private void handleTipClick(){
        String newTip = TIPS.get(new Random().nextInt(TIPS.size()));

        tipBox.setText(newTip);
    }

}
