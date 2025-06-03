package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Entity;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.HighscoreManager;
import com.cosmic.snakegamecraft.logic.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import static com.cosmic.snakegamecraft.util.Constants.GRID_SIZE;
import static com.cosmic.snakegamecraft.util.Constants.TILE_SIZE;
@SuppressWarnings("all")
public abstract class AbstractGameController {

    protected Snake_Player player;
    protected ItemManager itemManager;

    protected GameMode gameMode = AppContext.getGameMode();

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    protected void drawBackground(Canvas gameCanvas){
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());


        // TODO: Background Manager for background tiles, and maps
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                gc.drawImage(SpriteManager.getBgTile1(), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        for(int x = 0; x < GRID_SIZE; x++) {
            gc.drawImage(SpriteManager.getBgScore(), x * 64, 640, 64, 64);
        }


        player.render(gc);

        System.out.println("Render item");
        itemManager.render(gc);

    }

    protected void showGameOverDialog() {
        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("You have died!");
            alert.setContentText("Press Restart to play again or go Back to the Menu");

            ButtonType restartButton = new ButtonType("Restart");
            ButtonType menuButton = new ButtonType("Back to Menu");

            alert.getButtonTypes().setAll(restartButton, menuButton);

            HighscoreManager.saveScore(AppContext.getUsername(), player.getHighscore());

            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(button ->{
                if (button == restartButton){
                    restartGame();

                } else if (button == menuButton) {
                    // Go back to the menu
                    sceneManager.showMenu();
                }
            });

        });
    }

    private void restartGame(){
        GameSettings settings = SettingsLoader.loadSettings(AppContext.getUsername());

        player = new Snake_Player(5,5, 3, settings.getSpeedMultiplier());

        itemManager = new ItemManager(GRID_SIZE);

        // Restart game loop
        gameLoopMethod(settings);

    }

    protected void typeCheck(Item collidedItem) {
        switch (collidedItem.getType()) {
            case APPLE -> player.grow();
            case BAD_APPLE -> player.shrink();
            case RAINBOW_APPLE -> player.rainbowApple();
            case GOLDEN_APPLE -> {

                player.grow();
                player.grow(); // Golden apple gives two segments
            }
        }
        itemManager.removeItem(collidedItem);
    }

    protected void startKeyHandler(Canvas gameCanvas){
        // Directional input handling, handled via a queue to prevent asynchronous issues
        Platform.runLater(() -> {
            gameCanvas.getScene().setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP, W    -> player.queuedDirection(Entity.Direction.UP);
                    case DOWN, S  -> player.queuedDirection(Entity.Direction.DOWN);
                    case LEFT, A  -> player.queuedDirection(Entity.Direction.LEFT);
                    case RIGHT, D -> player.queuedDirection(Entity.Direction.RIGHT);
                }
            });
        });
    }

    protected abstract void gameLoopMethod(GameSettings settings);

    protected abstract void spawnItems();

    public abstract void initialize();

}
