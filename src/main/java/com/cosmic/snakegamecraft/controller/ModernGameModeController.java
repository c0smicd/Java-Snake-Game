package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class ModernGameModeController extends AbstractGameController{

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    private GameLoop gameLoop;


    @Override
    @FXML
    public void initialize() {

        GameSettings gameSettings = SettingsLoader.loadSettings(AppContext.getUsername());

        SpriteManager.loadSprites();

        itemManager = new ItemManager(GRID_SIZE);

        player = new Snake_Player(5,5, INITIAL_SNAKE_LENGTH, gameSettings.speedMultiplier());

        gameLoopMethod(gameSettings);

        startKeyHandler(gameCanvas);

    }


    @Override
    protected void gameLoopMethod(GameSettings settings) {

        gameLoop = new GameLoop(settings.speedMultiplier() * STANDARD_SPEED) {
            @Override
            public void update() {

                player.update();

                // Check for item collection
                Item collectedItem = itemManager.checkCollision(player.getHeadX(), player.getHeadY());

                if (collectedItem != null) {
                    typeCheck(collectedItem);
                    scoreLabel.setText("Score: " + player.getHighscore());
                }
                // Render items

                spawnItems();

                boolean isDead = (player.checkSelfCollision()
                        || player.checkWallCollision(GRID_SIZE)
                        || !player.checkShrink()) && !player.isInvulnerable(); // If player got a shrink item, it can die if the size is under 3 tiles

                if (isDead) {
                    System.out.println("Player is dead, stopping game loop.");
                    gameLoop.stop();
                    showGameOverDialog();
                    return;
                }


                drawFrame(gameCanvas);
            }
        };

        gameLoop.start();

    }

    @Override
    protected void spawnItems() {

    }


    @Override
    protected void resetHighscore() {

    }
}
