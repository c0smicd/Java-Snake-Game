package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.logic.ItemType;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class ClassicGameModeController extends AbstractGameController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;


    private GameLoop gameLoop;

    @Override
    @FXML
    public void initialize() {
        // Get settings
        GameSettings settings = SettingsLoader.loadSettings(AppContext.getUsername());

        // Load sprites
        SpriteManager.loadSprites();

        // Load ItemManager
        itemManager = new ItemManager(GRID_SIZE);

        player = new Snake_Player(5, 5, INITIAL_SNAKE_LENGTH, settings.getSpeedMultiplier());


        gameLoopMethod(settings);

        startKeyHandler(gameCanvas);

    }


    @Override
    protected void gameLoopMethod(GameSettings settings) {
        gameLoop = new GameLoop(settings.getSpeedMultiplier() * STANDARD_SPEED) {

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

                boolean isDead = (player.checkSelfCollision() || player.checkWallCollision(GRID_SIZE));

                if (isDead) {
                    System.out.println("Player is dead, stopping game loop.");
                    gameLoop.stop();
                    showGameOverDialog();
                    return;
                }


                drawBackground(gameCanvas);
            }
        };

        gameLoop.start();
    }


    @Override
    protected void spawnItems() {
        itemManager.spawnItem(ItemType.APPLE, player.getOccupiedPoints());
    }


}
