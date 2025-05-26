package com.cosmic.snakegamecraft.core;

import javafx.animation.AnimationTimer;

public abstract class GameLoop extends AnimationTimer {

    private long lastUpdate = 0;
    private final long interval;

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
}
