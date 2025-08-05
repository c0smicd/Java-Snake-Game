package com.cosmic.snakegamecraft.entity;


import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.logic.PlaySound;
import com.cosmic.snakegamecraft.logic.SpriteManager;
import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.util.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class Snake_Player extends Entity {

    private final LinkedList<Segment> body = new LinkedList<>();
    private final Image headSprite;
    private final Image bodySprite;
    private final Image tailSprite;
    private final double speedMultiplier;
    private int invulnerabilityTicks = 0; // Timer for invincibility effect
    private int speedUpTicks = 0;
    private int currentHighscore = 0;


    private int radiationTolerance = RAD_TOL;

    private GameMode gameMode = AppContext.getGameMode();
    public Snake_Player(int startX, int startY, int initialLength, double speedMultiplier) {
        this.x = startX;
        this.y = startY;
        this.direction = Direction.RIGHT;
        this.speedMultiplier = speedMultiplier;

        // Initialize the snake body with the specified initial length
        for (int i = 0; i < initialLength; i++) {
            body.add(new Segment(startX - i, startY));
        }

        headSprite = SpriteManager.getSnakeHead(gameMode);
        bodySprite = SpriteManager.getSnakeBody(gameMode);
        tailSprite = SpriteManager.getSnakeTail();
    }

    /**
     * Grows the snake by adding a new segment at the end of the body.
     */
    public void grow() {
        Segment last = body.getLast();
        body.add(new Segment(last.x, last.y));

        PlaySound.playSound(PlaySound.Sound.APPLE);

        increaseHighscore((int) (NORMAL_APPLE_POINTS * speedMultiplier)); // Increase score based on speed multiplier
    }

    /**
     * Shrinks the snake by removing the last segment.
     */
    public void shrink() {
        body.removeLast(); // Remove the last segment to shrink the snake

        increaseHighscore((int) (SHRINK_POINTS * speedMultiplier));
    }

    /**
     * Checks if the snake can shrink.
     *
     * @return true if the snake has more than one segment, false otherwise.
     */
    public boolean checkShrink() {
        return body.size() == 2;
    }

    /**
     * Activates invincibility for a short duration.
     */
    public void star(double currentSpeed) {
        /* Speed multiplier speeds up the game internal update, thus
        the invincibility effect lasts shorter in real time. Hence, multiplying by speedMultiplier.
         */
        invulnerabilityTicks = (int) (INVULNERABILITY_DURATION * speedMultiplier * currentSpeed);

        // This gets handled here: ModernGameModeController.handleInvulnerability();
        // increaseHighscore((int) (30 * speedMultiplier));

    }

    public void speedUp(double currentSpeed) {

        PlaySound.playSound(PlaySound.Sound.SPEEDUP);

        speedUpTicks = (int) (SPEED_UP_DURATION * speedMultiplier * currentSpeed);
    }

    public void increaseHighscoreOvertime(int amount) {
        increaseHighscore(amount);
    }

    /**
     * Checks if the snake is currently invulnerable
     *
     * @return true if the snake is currently invulnerable, false otherwise
     */
    public boolean isInvulnerable() {
        return invulnerabilityTicks > 0;
    }

    public boolean isSpeedUp() {
        return speedUpTicks > 0;
    }

    @Override
    public void update() {

        if (queuedDirection != null) {
            this.direction = queuedDirection;
            queuedDirection = null; // Reset queued direction after applying it
        }

        // Handle invincibility effect
        if (invulnerabilityTicks > 0) invulnerabilityTicks--;

        if (speedUpTicks > 0) speedUpTicks--;

        // Shift body segments

        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).x = body.get(i - 1).x;
            body.get(i).y = body.get(i - 1).y;
        }

        Segment head = body.getFirst();
        switch (direction) {
            case UP -> head.y--;
            case DOWN -> head.y++;
            case LEFT -> head.x--;
            case RIGHT -> head.x++;
        }

    }

    public void increaseRadiationTolerance(int radiationTolerance) {

        System.out.println(this.radiationTolerance);

        this.radiationTolerance += radiationTolerance;

        System.out.println(this.radiationTolerance);
    }

    public void decreaseRadiationTolerance(int radiationTolerance) {
        this.radiationTolerance -= radiationTolerance;
    }

    public int getRadiationTolerance() {
        return this.radiationTolerance;
    }


    public boolean checkRadiationDeath(){
        return radiationTolerance <= 0;
    }

    //TODO: Should be defenetly be cleaned up
    //TODO: Check if player has invurnerability effect, if so, draw head and body in different color
    @Override
    public void render(GraphicsContext gc) {


        for (int i = 0; i < body.size(); i++) {
            Segment curr = body.get(i);
            Image spriteToDraw;


            ImageView rotatedImage;

            if (i == 0) {
                // Draw head
                rotatedImage = new ImageView(headSprite);

                spriteToDraw = rotateImage(rotatedImage);

            } else if (i == body.size() - 1) {
                // Draw tail

                //FIXME: This is not working properly, tail direction is not correct
                Segment tail = body.get(i);
                Segment beforeTail = body.get(i - 1);

                Direction tailDirection;


                if (beforeTail.x < tail.x) {
                    tailDirection = Direction.LEFT; // Tail is facing left
                } else if (beforeTail.x > tail.x) {
                    tailDirection = Direction.RIGHT; // Tail is facing right
                } else if (beforeTail.y < tail.y) {
                    tailDirection = Direction.UP; // Tail is facing up
                } else {
                    tailDirection = Direction.DOWN; // Tail is facing down
                }

                // System.out.println("Tail direction: " + tailDirection);


                rotatedImage = new ImageView(tailSprite);

                // spriteToDraw = rotateImage(rotatedImage, tailDirection);

                // NOTE: The snake tail rotates prematurely, thus until i have time, only the body part gets rendered as tail
                spriteToDraw = bodySprite;


            } else {

                spriteToDraw = bodySprite;

            }
            //spriteToDraw = rotateImage(params, rotatedImage);

            gc.drawImage(Objects.requireNonNull(spriteToDraw), curr.x * TILE_SIZE, curr.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);


        }


    }

    private void increaseHighscore(int amount) {
        currentHighscore += amount;
    }

    public boolean checkSelfCollision() {
        Segment head = body.getFirst();

        for (int i = 1; i < body.size(); i++) {
            Segment segment = body.get(i);
            if (segment.x == head.x && segment.y == head.y) {
                return true; // Collision with itself
            }
        }

        return false;
    }

    public boolean checkWallCollision() {

        if (invulnerabilityTicks > 0) {

            wrapAround(GRID_SIZE);
            return false;
        }


        Segment head = body.getFirst();
        return head.x < 0 || head.x >= GRID_SIZE || head.y < 0 || head.y >= GRID_SIZE;
    }

    public int getHeadX() {
        return body.getFirst().x;
    }

    public int getHeadY() {
        return body.getFirst().y;
    }

    public List<Point> getOccupiedPoints() {
        return body.stream()
                .map(segment -> new Point(segment.x, segment.y))
                .toList();
    }

    public int getCurrentHighscore() {
        return currentHighscore;
    }

    public boolean canSpeedUp() {
        return body.size() % (SPEED_UP_LENGTH - 1) == 0; // Can speed up if the snake's length is a multiple of SPEED_UP_LENGTH
    }

    private void wrapAround(int gridSize) {

        Segment head = body.getFirst();

        if (head.x < 0) head.x = gridSize - 1;
        else if (head.x >= gridSize) head.x = 0;
        if (head.y < 0) head.y = gridSize - 1;
        else if (head.y >= gridSize) head.y = 0;
    }

    public static class Segment {
        int x, y;

        public Segment(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
