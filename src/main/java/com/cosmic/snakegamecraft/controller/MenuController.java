package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Random;

import static com.cosmic.snakegamecraft.util.Tips.*;


public class MenuController {

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    private GameSettings settings;

    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane loginOverlay;
    @FXML
    private TextField usernameField;
    @FXML
    private Label tipBox;
    @FXML
    private AnchorPane settingsOverlay;
    @FXML
    private Slider speedSlider;
    @FXML
    private Label speedValueLabel;


    private final Random rand = new Random();

    private Timeline repeat;


    //TODO: Outsource the random tip function into TIPS
    @FXML
    public void initialize() {

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedValueLabel.setText( "Speed: " + String.format("%.1f", newVal.doubleValue())); // format to one decimal place
        });

        if(AppContext.isLoggedIn()){
            // For restart

            String tip = getTipRandom(); // Last tip is for not logged in

            settings = AppContext.getSettings();
            loginButton.setTooltip(new Tooltip("You are logged in as " + AppContext.getUsername()));
            speedSlider.setValue(settings.speedMultiplier());
            speedValueLabel.setText("Speed: " + settings.speedMultiplier());
            tipBox.setText(tip);
        }else{
            loginButton.setTooltip(new Tooltip("You are not logged in. Click to log in."));
            tipBox.setText(getLoginTip());

        }


        Timeline tipCycle = new Timeline(
                new KeyFrame(Duration.minutes(3), e -> {
                    String newTip = getTipRandom();
                    if(!AppContext.isLoggedIn()){
                        tipBox.setText(getLoginTip());
                    }else{
                        tipBox.setText(newTip);
                    }
                })
        );

        tipCycle.setCycleCount(Timeline.INDEFINITE);
        tipCycle.play();



        maybeStartBuzzingTipBox();

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


    // TODO: Should only handle settings if logged in maybe
    @FXML
    public void handleSettings() {
        settingsOverlay.setVisible(true);
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
            settings = SettingsLoader.loadSettings(AppContext.getUsername());
            speedSlider.setValue(settings.speedMultiplier());
            speedValueLabel.setText("Speed: " + String.format("%.1f", settings.speedMultiplier()));
            tipBox.setText(getTipRandom());

            AppContext.setSettings(settings);

            // Stop buzzing
            repeat.stop();
        }
    }

    @FXML
    private void cancelLogin() {
        loginOverlay.setVisible(false);
    }

    @FXML
    private void handleTipClick(){

        if(!AppContext.isLoggedIn()) return;
        String newTip = getTipRandom();

        tipBox.setText(newTip);
    }

    @FXML
    private void saveSettings(){
        if(AppContext.getUsername() == "guest") {
            settingsOverlay.setVisible(false);
            return;
        }

        double speed = (double) Math.round((speedSlider.getValue() * 10)) / 10; // truncate to one decimal place
        System.out.println(speed);
        settings = new GameSettings(speed);

        SettingsLoader.saveSettings(new GameSettings(speed), AppContext.getUsername());
        settingsOverlay.setVisible(false);

        AppContext.setSettings(settings);
    }

    @FXML
    private void cancelSettings(){
        settingsOverlay.setVisible(false);
    }

    private void maybeStartBuzzingTipBox() {
        if (!AppContext.isLoggedIn()) {
            TranslateTransition buzz = new TranslateTransition(Duration.millis(100), tipBox);
            buzz.setFromX(-3);
            buzz.setToX(3);
            buzz.setAutoReverse(true);
            buzz.setCycleCount(6); // back and forth 3 times

            repeat = new Timeline(
                    new KeyFrame(Duration.seconds(0), e -> buzz.play()),
                    new KeyFrame(Duration.seconds(6)) // repeat every 6 seconds
            );
            repeat.setCycleCount(Animation.INDEFINITE);
            repeat.play();


        }
    }

}
