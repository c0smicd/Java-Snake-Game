package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML
    private TextField gridField;

    @FXML
    private Slider speedSlider;

    @FXML
    private Label speedValueLabel;

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    @FXML
    public void initialize() {
        // Load current settings
        GameSettings settings = SettingsLoader.loadSettings(AppContext.getUsername());
        speedSlider.setValue(settings.getSpeedMultiplier());

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedValueLabel.setText(String.format("%.1f", newVal.doubleValue())); // format to one decimal place
        });
        speedValueLabel.setText(String.format("%.1f", speedSlider.getValue()));
    }

    @FXML
    private void handleSave(){

        if(AppContext.getUsername().equals("guest")) {
            return;
        }

        int grid = Integer.parseInt(gridField.getText());
        double speed = (speedSlider.getValue() * 10) / 10; // truncate to one decimal place
        SettingsLoader.saveSettings(new GameSettings(speed), AppContext.getUsername());
        sceneManager.showMenu();
    }

    @FXML
    private void handleBack() {
        sceneManager.showMenu();
    }
}
