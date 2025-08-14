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
import com.cosmic.snakegamecraft.util.Point;
import eu.hansolo.tilesfx.Command;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class FalloutGameModeController extends AbstractGameController {

    @FXML
    public AnchorPane rootPane;
    @FXML
    public Canvas gameCanvas;
    @FXML
    public Label scoreLabel;

    @FXML
    public ProgressBar radiationBar;

    private GameLoop gameLoop;

    private GameSettings gameSettings;

    private int[] iod_timer = new int[IODINE_MAX_COUNT];

    private IntegerProperty radiationLevel = new SimpleIntegerProperty(RAD_TOL);

    int speed = 1;


    @Override
    @FXML
    public void initialize() {

        Arrays.fill(iod_timer, -1);

        gameSettings = SettingsLoader.loadSettings(AppContext.getUsername());

        SpriteManager.loadSprites();

        PlaySound.loadSounds();

        itemManager = new ItemManager(GRID_SIZE);

        player = new Snake_Player(5, 5, FALLOUT_SNAKE_LENGTH, gameSettings.speedMultiplier());

        gameLoopMethod(gameSettings);

        startKeyHandler(gameCanvas);

        radiationBar.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percent = (double) radiationLevel.get() / RAD_TOL;
            if (percent > 0.5) {
                return "-fx-accent: #00ff00;"; // Green
            } else if (percent > 0.2) {
                return "-fx-accent: #ffaa00;"; // Orange
            } else {
                return "-fx-accent: #ff0000;"; // Red
            }
        }, radiationLevel));




    }

    @Override
    protected void gameLoopMethod(GameSettings settings) {

        gameLoop = new GameLoop(settings.speedMultiplier() * STANDARD_SPEED) {

            @Override
            public void update() {
                player.update();

                updateRadiation();


                int highscoreBefore = player.getCurrentHighscore();

                // Check for item collection
                Item collectedItem = itemManager.checkCollision(player.getHeadX(), player.getHeadY());

                if (collectedItem != null) {
                    typeCheck(collectedItem, this.getCurrentSpeed());

                }

                scoreLabel.setText("Score: " + player.getCurrentHighscore());
                showScoreIncrease(player.getCurrentHighscore() - highscoreBefore, rootPane, scoreLabel);

                speed = (int) (settings.speedMultiplier() * this.getCurrentSpeed());
                // Render items

                spawnItems();

                boolean isDead = (player.checkSelfCollision() || player.checkWallCollision() || player.checkRadiationDeath());

                if (isDead) {
                    System.out.println("Player is dead, stopping game loop.");
                    gameLoop.stop();
                    showGameOverDialog();

                    return;
                }

                player.increaseHighscoreOvertime(LIVE_POINTS_PER_TICK);


                drawFrame(gameCanvas, GameMode.FALLOUT);
            }
        };

        gameLoop.start();
    }

    private void updateRadiation() {

        radiationLevel.set(player.getRadiationTolerance());

        radiationBar.setProgress((double) player.getRadiationTolerance() / RAD_TOL);

        player.decreaseRadiationTolerance(1);
    }


    @Override
    protected void spawnItems() {

        List<Point> blockedPoints = new ArrayList<>(player.getOccupiedPoints());
        blockedPoints.addAll(
                IntStream.range(0, 20)
                        .mapToObj(x -> new Point(x, 0))
                        .toList()
        );

        itemManager.spawnItem(ItemType.IODINE, blockedPoints, 1);

    }

    @Override
    protected void resetHighscore() {
        scoreLabel.setText("Score: " + player.getCurrentHighscore());
    }



}
