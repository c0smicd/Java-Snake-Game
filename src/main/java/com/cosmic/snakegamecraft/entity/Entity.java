package com.cosmic.snakegamecraft.entity;

import javafx.scene.canvas.GraphicsContext;


public abstract class Entity {

    protected int x, y;
    protected Direction direction;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;
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
     *
     * @param newDirection The new direction to set.
     */
    public void setDirection(Direction newDirection){


        // Prevent the snake from reversing direction directly
        if((this.direction == Direction.UP && newDirection != Direction.DOWN) ||
                (this.direction == Direction.DOWN && newDirection != Direction.UP) ||
                (this.direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (this.direction == Direction.RIGHT && newDirection != Direction.LEFT)){

            this.direction = newDirection;
        }
    }

    public Direction getDirection() {
        return direction;
    }
}
