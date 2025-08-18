package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.logic.PlaySound;
import com.cosmic.snakegamecraft.logic.SettingsLoader;
import com.cosmic.snakegamecraft.logic.SpriteManager;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.util.Item;
import com.cosmic.snakegamecraft.util.ItemType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.Arrays;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class ModernGameModeController extends AbstractGameController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    private GameLoop gameLoop;


    private int[] bad_apple_timer = {-1, -1, -1};
    private int speed_timer = -1;

    private GameSettings gameSettings;

    private int speed = 1;

    @Override
    @FXML
    public void initialize() {

        gameSettings = SettingsLoader.loadSettings(AppContext.getUsername());

        SpriteManager.loadSprites();

        PlaySound.loadSounds();

        itemManager = new ItemManager(GRID_SIZE);

        player = new Snake_Player(5, 5, INITIAL_SNAKE_LENGTH, gameSettings.speedMultiplier());

        gameLoopMethod(gameSettings);

        startKeyHandler(gameCanvas);

    }


    @Override
    protected void gameLoopMethod(GameSettings settings) {

        gameLoop = new GameLoop(settings.speedMultiplier() * STANDARD_SPEED) {
            @Override
            public void update() {

                player.update();

                int highscoreBefore = player.getCurrentHighscore();

                // Check for item collection
                Item collectedItem = itemManager.checkCollision(player.getHeadX(), player.getHeadY());

                if (collectedItem != null) {

                    if (player.canSpeedUp() && (collectedItem.getType() == ItemType.APPLE || collectedItem.getType() == ItemType.GOLDEN_APPLE)) {
                        this.setTicksPerSecond(SPEEDUPDATE.UP);
                    }

                    if (typeCheck(collectedItem, this.getCurrentSpeed())) {
                        System.out.println("Speed Up item collected");
                        this.speedUpEffect(SPEEDUPDATE.UP);
                        speed_timer = (int) (SPEED_UP_DURATION * settings.speedMultiplier() * this.getCurrentSpeed());

                        System.out.println("Speed timer set to: " + speed_timer);
                    }


                }

                // Render items
                spawnItems();

                // Handle bad apple timer
                handleBadAppleTimer(settings, this.getCurrentSpeed());

                // Handle speed timer
                handleSpeedTimer();

                // A fucking mess, but it works. Only updates the highscore if the player is invulnerable over time.
                // The invulnerability effect is handled in the player class
                handleInvulnerability();

               // System.out.println("Current highscore: " + player.getCurrentHighscore());
                scoreLabel.setText("Score: " + player.getCurrentHighscore());


                showScoreIncrease(player.getCurrentHighscore() - highscoreBefore, rootPane, scoreLabel);

                boolean isDead = (player.checkSelfCollision()
                        || player.checkWallCollision()
                        || player.checkShrink()) && !player.isInvulnerable(); // If player got a shrink item, it can die if the size is under 3 tiles

                if (isDead) {

                    gameLoop.stop();
                    showGameOverDialog();

                    AppContext.setCanFalloutMode(player.getCurrentHighscore());

                    return;
                }


                speed = (int) (settings.speedMultiplier() * this.getCurrentSpeed());

                drawFrame(gameCanvas, GameMode.MODERN);
            }
        };

        gameLoop.start();

    }

    /**
     * Spawns items in the modern game. Every item can only appear once in the same frame.
     * <p>
     * This method should not spawn items with probability one (1.0), except for the apple. All other items should spawn randomly
     * with a certain chance
     */
    @Override
    protected void spawnItems() {
        Arrays.stream(ItemType.values()).forEach(type -> {
            if(type != ItemType.IODINE) itemManager.spawnItem(type, player.getOccupiedPoints(), speed);
        });
    }


    @Override
    protected void resetHighscore() {
        scoreLabel.setText("Score: " + player.getCurrentHighscore());
    }


    private void handleBadAppleTimer(GameSettings settings, double currentSpeed) {
        if (!itemManager.existsBadApple()) return;

        if (Arrays.stream(bad_apple_timer).anyMatch(t -> t == 0)) {
            itemManager.removeItem(ItemType.BAD_APPLE);
            bad_apple_timer = Arrays.stream(bad_apple_timer).map(t -> t == 0 ? -1 : t).toArray(); // Reset all bad apple timers
            return;
        }

        if (Arrays.stream(bad_apple_timer).anyMatch(t -> t == -1)) {
            bad_apple_timer = Arrays.stream(bad_apple_timer)
                    .map(t -> t == -1 ? (int) (BAD_APPLE_DURATION * settings.speedMultiplier() * currentSpeed) : t)
                    .toArray();
        } else {
            bad_apple_timer = Arrays.stream(bad_apple_timer)
                    .map(t -> t > 0 ? t - 1 : t) // Decrease all timers that are greater than 0
                    .toArray();
        }
    }

    /**
     * Handles the speed timer for the player.
     * If the timer reaches 0, the speed effect is removed.
     * If the timer is -1, no speed effect is active.
     *
     * @Note also Handles the SpeedHighscoreOvertime for the player.
     */
    private void handleSpeedTimer() {
        if (speed_timer == -1) return;


        if (speed_timer == 0) {
            gameLoop.speedUpEffect(GameLoop.SPEEDUPDATE.DOWN);
            speed_timer = -1;

        } else {
            player.increaseHighscoreOvertime((int) ((gameSettings.speedMultiplier() * SPEED_UP_POINTS) / 1.5));
            speed_timer--;

        }
    }


    private void handleInvulnerability() {
        if (player.isInvulnerable()) {
            player.increaseHighscoreOvertime((int) ((gameSettings.speedMultiplier() * STAR_POINTS) / 2));
        }
    }


}
