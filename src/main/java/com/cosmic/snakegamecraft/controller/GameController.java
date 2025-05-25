package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    @FXML
    public void initialize() {
        // Get settings
        GameSettings settings = SettingsLoader.loadSettings(AppContext.getUsername());

        // Canvas resize logic (optional for now)
        gameCanvas.widthProperty().bind(gameCanvas.getScene().widthProperty());
        gameCanvas.heightProperty().bind(gameCanvas.getScene().heightProperty());

        // Set up game engine (next step)
        drawInitialGrid(settings.getGridSize());
    }

    private void drawInitialGrid(int gridSize) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        double tileSize = gameCanvas.getWidth() / gridSize;

        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setLineWidth(0.5);
        for (int i = 0; i <= gridSize; i++) {
            gc.strokeLine(i * tileSize, 0, i * tileSize, gameCanvas.getHeight()); // vertical lines
            gc.strokeLine(0, i * tileSize, gameCanvas.getWidth(), i * tileSize); // horizontal lines
        }
    }

}
