package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Entity;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.HighscoreManager;
import com.cosmic.snakegamecraft.logic.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.logic.ItemType;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import static com.cosmic.snakegamecraft.util.Constants.*;

/**
 * Abstract base class for game controllers, providing common functionality
 */

@SuppressWarnings("all")
abstract class AbstractGameController {

    protected Snake_Player player;
    protected ItemManager itemManager;

    protected GameMode gameMode = AppContext.getGameMode();

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());


    /**
     * Initializes the game controller.
     * This method should be implemented by subclasses to set up the game state.
     *
     * ? Loads settings, sprites, initializes player and item manager, and starts the game loop.
     */
    @FXML
    public abstract void initialize();

    /**
     * Draws the game background and renders the player and items.
     *
     *  @Note This method should eventually call the corresponding @see BackgroundManager
     *
     * @param gameCanvas The canvas to draw on.
     */

    protected void drawFrame(Canvas gameCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                gc.drawImage(SpriteManager.getBgTile1(), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Score background
        for (int x = 0; x < GRID_SIZE; x++) {
            gc.drawImage(SpriteManager.getBgScore(), x * 64, 640, 64, 64);
        }

        // Bottom / Top grass background score
        for (int x = 0; x < GRID_SIZE; x++){
            gc.drawImage(SpriteManager.getBgGrassBottom(), x * 64, 640, 64, 64);
            gc.drawImage(SpriteManager.getBgGrassTop(), x * 64, 640, 64, 64);
        }


        player.render(gc);

        System.out.println("Render item");
        itemManager.render(gc);

    }

    /**
     * Displays a game over dialog with options to restart or go back to the menu.
     */
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
            result.ifPresent(button -> {
                if (button == restartButton) {
                    restartGame();

                } else if (button == menuButton) {
                    // Go back to the menu
                    sceneManager.showMenu();
                }
            });

        });
    }

    /**
     * Restarts the game by resetting the player and item manager, and reinitializing the game loop.
     */
    private void restartGame() {
        GameSettings settings = SettingsLoader.loadSettings(AppContext.getUsername());

        player = new Snake_Player(5, 5, INITIAL_SNAKE_LENGTH, settings.speedMultiplier());

        // Reset player highscore
        resetHighscore();

        itemManager = new ItemManager(GRID_SIZE);

        // Restart game loop
        gameLoopMethod(settings);

    }

    /**
     * Checks the type of the collided item and updates the player's state accordingly.
     *
     * @param collidedItem The item that was collected by the player.
     */
    protected boolean typeCheck(Item collidedItem) {
        boolean isSpeedUp = false;
        switch (collidedItem.getType()) {
            case APPLE -> player.grow();
            case BAD_APPLE -> player.shrink();
            case STAR -> player.star();
            case GOLDEN_APPLE -> {

                player.grow();
                player.grow(); // Golden apple gives two segments
            }
            case SPEED_UP -> {
                isSpeedUp = true;

                player.speed();
            }
        }

        itemManager.removeItem(collidedItem); // Remove item s.t. the next item can be spawned

        return isSpeedUp;
    }

    /**
     * Starts the key handler for player movement.
     *
     * ? Runs asynchronously due to JavaFX initializaition constraints.
     *
     * @param gameCanvas The canvas where the game is rendered.
     */
    protected void startKeyHandler(Canvas gameCanvas) {
        // Directional input handling, handled via a queue to prevent asynchronous issues
        Platform.runLater(() -> {
            gameCanvas.getScene().setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP, W -> player.queuedDirection(Entity.Direction.UP);
                    case DOWN, S -> player.queuedDirection(Entity.Direction.DOWN);
                    case LEFT, A -> player.queuedDirection(Entity.Direction.LEFT);
                    case RIGHT, D -> player.queuedDirection(Entity.Direction.RIGHT);
                }
            });
        });
    }

    /**
     * Abstract method to be implemented by subclasses to define the game loop logic.
     *
     * @Note ? In this method a new GameLoop instance is created, which runs the game logic.
     *
     * @see GameLoop
     * @param settings The game settings to be used in the game loop.
     *
     */
    protected abstract void gameLoopMethod(GameSettings settings);

    /**
     * Abstract method to spawn items in the game.
     * This method should be implemented by subclasses to define what items are spawned.
     */
    protected abstract void spawnItems();




    protected abstract void resetHighscore();

}
