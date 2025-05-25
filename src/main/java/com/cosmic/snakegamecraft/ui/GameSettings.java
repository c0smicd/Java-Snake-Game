package com.cosmic.snakegamecraft.ui;

public class GameSettings {
    private int gridSize;
    private double speedMultiplier;


    public GameSettings(int gridSize, double speedMultiplier) {
        this.gridSize = gridSize;
        this.speedMultiplier = speedMultiplier;
    }

    public int getGridSize() {
        return gridSize;
    }


    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

}
