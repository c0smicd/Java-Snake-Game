package com.cosmic.snakegamecraft.core;

import javafx.animation.AnimationTimer;

/**
 * Abstract class for a game loop that extends AnimationTimer.
 * This class provides a mechanism to update the game state at a specified ticks per second (TPS).
 */
public abstract class GameLoop extends AnimationTimer {

    private long lastUpdate = 0;
    private  long interval;

    public GameLoop(double tps) {
        this.interval = (long) (1_000_000_000 / tps); // Convert TPS to nanoseconds
    }

    @Override
    public void handle(long now) {
        if(now - lastUpdate >= interval) {
            lastUpdate = now;
            update();
        }
    }

    public abstract void update();

    /**
     * Sets the ticks per second for the game loop.
     * This will reset the last update time and recalculate the interval.
     *
     * @param tps The desired ticks per second.
     *
     * ? Should be used to adjust the game speed dynamically, especially in modern mode.
     */
    public void setTicksPerSecond(double tps) {
        this.lastUpdate = 0; // Reset last update time
        this.interval = (long) (1_000_000_000 / tps); // Recalculate interval
    }
}
