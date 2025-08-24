package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.PlaySound;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.util.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.enums.ItemType;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.logic.SettingsLoader;
import com.cosmic.snakegamecraft.logic.SpriteManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class ClassicGameModeController extends AbstractGameController {

    @FXML
    private AnchorPane rootPane;

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

        // Load sounds
        PlaySound.loadSounds();

        // Load ItemManager
        itemManager = new ItemManager(GRID_SIZE);

        player = new Snake_Player(5, 5, INITIAL_SNAKE_LENGTH, settings.speedMultiplier());


        gameLoopMethod(settings);

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
                    typeCheck(collectedItem, 0);
                    scoreLabel.setText("Score: " + player.getCurrentHighscore());
                    showScoreIncrease(player.getCurrentHighscore() - highscoreBefore, rootPane, scoreLabel);
                }
                // Render items

                spawnItems();

                boolean isDead = (player.checkSelfCollision() || player.checkWallCollision());

                if (isDead) {
                    System.out.println("Player is dead, stopping game loop.");
                    gameLoop.stop();
                    showGameOverDialog();

                    // Check if player can play modern mode
                    AppContext.setCanModernMode(player.getCurrentHighscore());

                    return;
                }


                drawFrame(gameCanvas, GameMode.CLASSIC);
            }
        };

        gameLoop.start();
    }


    @Override
    protected void spawnItems() {
        itemManager.spawnItem(ItemType.APPLE, player.getOccupiedPoints(), 1);
    }

    @Override
    protected void resetHighscore(){
        scoreLabel.setText("Score: " + player.getCurrentHighscore());
    }
}
