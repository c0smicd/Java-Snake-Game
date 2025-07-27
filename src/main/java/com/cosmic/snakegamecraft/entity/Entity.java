package com.cosmic.snakegamecraft.entity;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


public abstract class Entity {

    protected int x, y;
    protected Direction direction;
    protected Direction queuedDirection = null;



    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }


    /**
     * Updates the state of the entity.
     * This method should be overridden to implement specific update logic for the entity.
     */
    public abstract void update();

    /**
     * Renders the snake on the given GraphicsContext.
     * The snake is drawn segment by segment, with special handling for the head, tail, and turns.
     *
     * @param gc The GraphicsContext to draw on.
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Sets the direction of the entity.
     * This method prevents the entity from reversing direction directly.
     * <p>
     * @Note Use {@link #queuedDirection(Direction)} instead for player-controlled entities.
     * @param newDirection The new direction to set.
     */

    @Deprecated
    public void setDirection(Direction newDirection){


        // Prevent the snake from reversing direction directly
        if((this.direction == Direction.UP && newDirection != Direction.DOWN) ||
                (this.direction == Direction.DOWN && newDirection != Direction.UP) ||
                (this.direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (this.direction == Direction.RIGHT && newDirection != Direction.LEFT)){

            this.direction = newDirection;
        }
    }

    /**
     * Queues a new direction for the entity, especially for the player snake, since the keyhandler runs
     * asynchronously with the game loop.
     *
     * @param newDirection The new direction to queue.
     */
    public void queuedDirection(Direction newDirection) {
        // Prevent the snake from reversing direction directly
        if((this.direction == Direction.UP && newDirection != Direction.DOWN) ||
                (this.direction == Direction.DOWN && newDirection != Direction.UP) ||
                (this.direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (this.direction == Direction.RIGHT && newDirection != Direction.LEFT)){

            this.queuedDirection = newDirection;
        }

    }

    protected Image rotateImage (ImageView rotatedImage) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image spriteToDraw = null;
        switch(direction){
            case UP -> {
                rotatedImage.setRotate(-90); // Up
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case DOWN -> {
                rotatedImage.setRotate(90); // Down
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case LEFT -> {
                rotatedImage.setRotate(180); // Left
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case RIGHT -> {
                spriteToDraw = rotatedImage.snapshot(params, null);
            }

        }
        return spriteToDraw;
    }

    protected Image rotateImage (ImageView rotatedImage, Direction tailDirection) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image spriteToDraw = null;
        switch(direction){
            case UP -> {
                rotatedImage.setRotate(-90); // Up
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case DOWN -> {
                rotatedImage.setRotate(90); // Down
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case LEFT -> {
                rotatedImage.setRotate(180); // Left
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case RIGHT -> {
                spriteToDraw = rotatedImage.snapshot(params, null);
            }

        }
        return spriteToDraw;
    }



    public Direction getDirection() {
        return direction;
    }

    public boolean checkHit(Entity other) {
        // TODO: Implement hitscan logic for collision detection

        return false;
    }
}
