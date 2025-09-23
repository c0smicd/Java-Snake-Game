package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Entity;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.*;
import com.cosmic.snakegamecraft.util.Item;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Optional;

import static com.cosmic.snakegamecraft.util.Constants.*;

/**
 * Abstract base class for game controllers, providing common functionality
 */

@SuppressWarnings("all")
abstract class AbstractGameController {

    protected Snake_Player player;
    protected ItemManager itemManager;
    protected NukeManager nukeManager;

    protected GameMode gameMode = AppContext.getGameMode();

    private final SceneManager sceneManager = new SceneManager(AppContext.getStage());



    /**
     * Initializes the game controller.
     * This method should be implemented by subclasses to set up the game state.
     *
     * @Note Loads settings, sprites, initializes player and item manager, and starts the game loop.
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

    protected void drawFrame(Canvas gameCanvas, GameMode gameMode) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        Image backGroundImage = gameMode == GameMode.FALLOUT ? SpriteManager.getBgTileFallout() : SpriteManager.getBgTile1();


        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                gc.drawImage(backGroundImage, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
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

        itemManager.render(gc);

        if(gameMode == GameMode.FALLOUT) {
            nukeManager.render(gc);
        }

    }

    /**
     * Displays a game over dialog with options to restart or go back to the menu.
     */
    protected void showGameOverDialog() {

        PlaySound.playSound(PlaySound.Sound.GAMEOVER);

        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("You have died!");
            alert.setContentText("Press Restart to play again or go Back to the Menu");

            ButtonType restartButton = new ButtonType("Restart");
            ButtonType menuButton = new ButtonType("Back to Menu");

            alert.getButtonTypes().setAll(restartButton, menuButton);

            HighscoreManager.saveScore(AppContext.getGameMode(), AppContext.getUsername(), player.getCurrentHighscore());

            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(button -> {
                if (button == restartButton) {
                    PlaySound.stopSound();
                    restartGame();

                } else if (button == menuButton) {
                    // Go back to the
                    PlaySound.stopSound();
                    sceneManager.showMenu();
                }
            });

        });
    }

    protected void showPlayerWinDialog() {
        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("You Win!");
            alert.setHeaderText("Congratulations, you have won the game!");
            alert.setContentText("Press Restart to play again or go Back to the Menu");

            ButtonType restartButton = new ButtonType("Restart");
            ButtonType menuButton = new ButtonType("Back to Menu");

            alert.getButtonTypes().setAll(restartButton, menuButton);

            HighscoreManager.saveScore(AppContext.getGameMode(), AppContext.getUsername(), player.getCurrentHighscore() * 2); // Winning doubles the score

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



        player = new Snake_Player(5, 5, gameMode == GameMode.FALLOUT ?  FALLOUT_SNAKE_LENGTH : INITIAL_SNAKE_LENGTH, settings.speedMultiplier());

        // Reset player highscore
        resetHighscore();

        itemManager = new ItemManager(GRID_SIZE);
        nukeManager = new NukeManager();

        // Restart game loop
        gameLoopMethod(settings);

    }

    /**
     * Checks the type of the collided item and updates the player's state accordingly.
     *
     * @param collidedItem The item that was collected by the player.
     *
     * @return true if the item is a speed-up item, false otherwise.
     */
    protected boolean typeCheck(Item collidedItem, double currentSpeed) {
        boolean isSpeedUp = false;
        switch (collidedItem.getType()) {
            case APPLE -> player.grow();
            case BAD_APPLE -> player.shrink();
            case STAR -> player.star(currentSpeed);
            case GOLDEN_APPLE -> {

                player.grow();
                player.grow(); // Golden apple gives two segments
            }
            case SPEED_UP -> {
                isSpeedUp = true;
                player.speedUp(currentSpeed);
            }

            case IODINE -> {
                System.out.println("IODINE");

                player.iodine(currentSpeed);
            }
            case IODINE_STACK -> {
                player.iodineStack(currentSpeed);
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

    protected void showScoreIncrease(int increaseAmount, AnchorPane rootPane, Label scoreLabel) {

        if(increaseAmount == 0) {
            return; // No score increase to show
        }

        Text scoreText = new Text(increaseAmount > 0 ? "+" + increaseAmount : "-" + Math.abs(increaseAmount));
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        scoreText.setLayoutX(scoreLabel.getLayoutX() + scoreLabel.getWidth() + 10);
        scoreText.setLayoutY(scoreLabel.getLayoutY());

        rootPane.getChildren().add(scoreText); // rootPane is your scene's root (e.g., AnchorPane)

        // Move up
        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1), scoreText);
        moveUp.setByY(-20);

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), scoreText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Play together
        ParallelTransition animation = new ParallelTransition(moveUp, fadeOut);
        animation.setOnFinished(e -> rootPane.getChildren().remove(scoreText));
        animation.play();
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
