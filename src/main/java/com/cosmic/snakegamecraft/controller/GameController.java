package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Entity;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.Item;
import com.cosmic.snakegamecraft.logic.ItemManager;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.ui.SceneManager;
import com.cosmic.snakegamecraft.util.SettingsLoader;
import com.cosmic.snakegamecraft.util.SpriteManager;
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
        itemManager = new ItemManager(settings.getGridSize());

        player = new Snake_Player(5,5);


        gameLoop = new GameLoop(settings.getSpeedMultiplier()) {
            @Override
            public void update() {
                player.update();

                // Check for item collection
                Item collectedItem = itemManager.checkCollision(player.getHeadX(), player.getHeadY());

                if(collectedItem != null) {
                    switch(collectedItem.getType()) {
                        case APPLE -> player.grow();
                        case BAD_APPLE -> player.shrink();
                        case RAINBOW_APPLE -> player.rainbowApple();
                        case GOLDEN_APPLE -> {
                            player.grow();
                            player.grow(); // Golden apple gives two segments
                        }
                    }
                }


                drawFrame();
            }
        };

        gameLoop.start();

        gameCanvas.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> player.setDirection(Entity.Direction.UP);
                case DOWN -> player.setDirection(Entity.Direction.DOWN);
                case LEFT -> player.setDirection(Entity.Direction.LEFT);
                case RIGHT -> player.setDirection(Entity.Direction.RIGHT);
            }
        });
    }

    private void drawFrame() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());


        player.render(gc);
    }



}
