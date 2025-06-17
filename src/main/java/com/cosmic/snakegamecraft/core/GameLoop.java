package com.cosmic.snakegamecraft.core;

import javafx.animation.AnimationTimer;

/**
 * Abstract class for a game loop that extends AnimationTimer.
 * This class provides a mechanism to update the game state at a specified ticks per second (TPS).
 */
public abstract class GameLoop extends AnimationTimer {

    private long lastUpdate = 0;
    private long interval;
    private double standardTps;
    private SPEED currentSpeed = SPEED.SLOW; // Default speed

    private enum SPEED {
        SLOW(1),
        FAST(3),
        FASTER(5),
        ULTRA_FAST(7);

        private final double tps;

        SPEED(double tps) {
            this.tps = tps;
        }

        public double getTps() {
            return tps;
        }
    }

    public enum SPEEDUPDATE {
        UP,
        DOWN;
    }



    public GameLoop(double tps) {
        this.standardTps = tps;
        this.interval = (long) (1_000_000_000 / tps); // Convert TPS to nanoseconds
    }

    @Override
    public void handle(long now) {
        if(now - lastUpdate >= interval) {
            lastUpdate = now;
            update();
        }
    }

    /**
     * Abstract method to update the game state.
     * This method should be implemented by subclasses to define the game logic.
     */
    public abstract void update();

    /**
     * Sets the ticks per second for the game loop.
     * This will reset the last update time and recalculate the interval.
     *
     * @Note Should be used to adjust the game speed dynamically, especially in modern mode.
     *
     * @param tps The desired ticks per second.
     */
    public void setTicksPerSecond(SPEEDUPDATE tps) {
        this.lastUpdate = 0; // Reset last update time



    }
}


