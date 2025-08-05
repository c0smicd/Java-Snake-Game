package com.cosmic.snakegamecraft.core;

import javafx.animation.AnimationTimer;

import static com.cosmic.snakegamecraft.util.Constants.SPEED_UP_EFFECT;

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
        FAST(1.25),
        FASTER(1.5),
        ULTRA_FAST(2);

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
            //System.out.println("Updating game state at time: " + interval);
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


        switch(tps){
            case UP: {
                if(currentSpeed == SPEED.SLOW) {
                    currentSpeed = SPEED.FAST;

                    System.out.println("Current interval:" + interval);
                    this.interval =  (long) (1_000_000_000 / (standardTps * SPEED.FAST.getTps()));

                    System.out.println("New interval:" + interval);
                } else if(currentSpeed == SPEED.FAST) {
                    currentSpeed = SPEED.FASTER;
                    this.interval =  (long) (1_000_000_000 / (standardTps * SPEED.FASTER.getTps()));
                } else if(currentSpeed == SPEED.FASTER) {
                    currentSpeed = SPEED.ULTRA_FAST;
                    this.interval =  (long) (1_000_000_000 / (standardTps * SPEED.ULTRA_FAST.getTps()));
                }

                break;
            }
            case DOWN: {
                if(currentSpeed == SPEED.ULTRA_FAST) {
                    currentSpeed = SPEED.FASTER;
                    this.interval =  (long) (1_000_000_000 / (standardTps * SPEED.FASTER.getTps()));
                } else if(currentSpeed == SPEED.FASTER) {
                    currentSpeed = SPEED.FAST;
                    this.interval =  (long) (1_000_000_000 / (standardTps * SPEED.FAST.getTps()));
                } else if(currentSpeed == SPEED.FAST) {
                    currentSpeed = SPEED.SLOW;
                    this.interval =  (long) (1_000_000_000 / (standardTps * SPEED.SLOW.getTps()));
                }
            }

        }


    }

    public void speedUpEffect(SPEEDUPDATE update) {

        System.out.println("Speed up effect triggered: " + standardTps * currentSpeed.getTps() * SPEED_UP_EFFECT );
        switch(update) {
            case UP -> this.interval = (long) (1_000_000_000 / (standardTps * currentSpeed.getTps() * SPEED_UP_EFFECT));
            case DOWN -> this.interval = (long) (1_000_000_000 / (standardTps * currentSpeed.getTps()));

        }
    }

    public double getCurrentSpeed() {
        return currentSpeed.getTps();
    }
}


