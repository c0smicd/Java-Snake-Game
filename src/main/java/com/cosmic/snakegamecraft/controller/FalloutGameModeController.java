package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.core.GameLoop;
import com.cosmic.snakegamecraft.entity.Snake_Player;
import com.cosmic.snakegamecraft.logic.*;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.ui.GameSettings;
import com.cosmic.snakegamecraft.util.Item;
import com.cosmic.snakegamecraft.enums.ItemType;
import com.cosmic.snakegamecraft.util.Point;
import com.cosmic.snakegamecraft.enums.Radiation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    int speed = 1;
    private GameLoop gameLoop;
    private GameSettings gameSettings;
    private final int[] iod_timer = new int[IODINE_MAX_COUNT];
    private final IntegerProperty radiationLevel = new SimpleIntegerProperty(RAD_TOL);

    private int radiationPerTick = 1;

    @Override
    @FXML
    public void initialize() {

        Arrays.fill(iod_timer, -1);

        gameSettings = SettingsLoader.loadSettings(AppContext.getUsername());

        SpriteManager.loadSprites();

        //PlaySound.loadSounds();

        itemManager = new ItemManager(GRID_SIZE);
        nukeManager = new NukeManager();

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
                spawnNukes();

                boolean[] checkNukeDeath = nukeHandler(); // Also forwards the tick of the nuke

                boolean isDead = (player.checkSelfCollision()
                        || player.checkWallCollision()
                        || player.checkRadiationDeath()
                        || checkNukeDeath[0]
                        || checkNukeDeath[1]);

                if (isDead) {
                    System.out.println("Player is dead, stopping game loop.");
                    stop();
                    showGameOverDialog();

                    return;
                }

                player.increaseHighscoreOvertime(POINTS_PER_TICK_FALLOUT);
                changeRadiationPerTick();


                drawFrame(gameCanvas, GameMode.FALLOUT);
            }
        };

        gameLoop.start();
    }

    private void updateRadiation() {

        radiationLevel.set(player.getRadiationTolerance());

        radiationBar.setProgress((double) player.getRadiationTolerance() / RAD_TOL);

        player.decreaseRadiationTolerance(radiationPerTick);
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
        itemManager.spawnItem(ItemType.IODINE_STACK, blockedPoints, 1);

    }

    @Override
    protected void resetHighscore() {
        scoreLabel.setText("Score: " + player.getCurrentHighscore());
    }

    private void spawnNukes() {

        int size = 20;
        List<Point> blockedPoints = new ArrayList<>(player.getOccupiedPoints());

        // first 2 rows
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(x -> new Point(x, 0))
                        .toList()
        );
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(x -> new Point(x, 1))
                        .toList()
        );

        // last 2 rows
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(x -> new Point(x, size - 1))
                        .toList()
        );
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(x -> new Point(x, size - 2))
                        .toList()
        );

        // first 2 columns
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(y -> new Point(0, y))
                        .toList()
        );
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(y -> new Point(1, y))
                        .toList()
        );

        // last 2 columns
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(y -> new Point(size - 1, y))
                        .toList()
        );
        blockedPoints.addAll(
                IntStream.range(0, size)
                        .mapToObj(y -> new Point(size - 2, y))
                        .toList()
        );

        nukeManager.dropNuke(blockedPoints, speed);

    }

    /**
     * Handles all nukes. Ticking them forward into the next possible state and checking if
     * the player collides with a nuke or the radiation field.
     *
     * @return An array of booleans, where the first element indicates if the player died by a nuke and the second element indicates if the player is in a radiation field.
     */
    private boolean[] nukeHandler() {
        nukeManager.handleNukeTick();

        boolean checkNukeDeath = nukeManager.checkNukeCollision(player.getHeadX(), player.getHeadY());
        boolean checkWasteField = nukeManager.checkWasteCollision(player.getHeadX(), player.getHeadY());

        return new boolean[]{checkNukeDeath, checkWasteField};
    }

    private void changeRadiationPerTick() {

        Radiation radiation = nukeManager.getCurrentRadiation();

        switch (radiation) {
            case LOW -> radiationPerTick = 1;
            case MEDIUM -> radiationPerTick = 2;
            case HIGH -> radiationPerTick = 3;
        }

    }
}
