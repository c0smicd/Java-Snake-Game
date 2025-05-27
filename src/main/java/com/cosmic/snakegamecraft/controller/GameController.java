package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Entity;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.HighscoreManager;
import com.cosmic.snakegamecraft.logic.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.logic.ItemType;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.Optional;

import static com.cosmic.snakegamecraft.util.Constants.GRID_SIZE;
import static com.cosmic.snakegamecraft.util.Constants.TILE_SIZE;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());

    private GameLoop gameLoop;
    private Snake_Player player;
    private ItemManager itemManager;

    @FXML
    public void initialize() {
        // Get settings
        GameSettings settings = SettingsLoader.loadSettings(AppContext.getUsername());

        // Load sprites
        SpriteManager.loadSprites();

        // Load ItemManager
        itemManager = new ItemManager(GRID_SIZE);

        player = new Snake_Player(5,5, 3, settings.getSpeedMultiplier());


        gameLoopMethod(settings);


        // Directional input handling
        // TODO: Has problems with multiple key presses, needs to be fixed
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

    private void drawFrame() {
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

    private void showGameOverDialog() {
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
                    sceneManager.showMenu(AppContext.getUsername());
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

    private void gameLoopMethod(GameSettings settings) {
        gameLoop = new GameLoop(settings.getSpeedMultiplier()) {

            @Override
            public void update() {
                player.update();

                // Check for item collection
                Item collectedItem = itemManager.checkCollision(player.getHeadX(), player.getHeadY());

                if(collectedItem != null) {
                    typeCheck(collectedItem);
                    scoreLabel.setText("Score: "+player.getHighscore());
                }
                // Render items
                itemManager.spawnItem(ItemType.APPLE, player.getOccupiedPoints());

                boolean isDead = (!player.isInvulnerable() && player.checkSelfCollision() || player.checkWallCollision(GRID_SIZE));

                if(isDead){
                    System.out.println("Player is dead, stopping game loop.");
                    gameLoop.stop();
                    showGameOverDialog();
                    return;
                }


                drawFrame();
            }
        };

        gameLoop.start();
    }

    private void typeCheck(Item collidedItem) {
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


}
