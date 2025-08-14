package com.cosmic.snakegamecraft.logic;

import com.cosmic.snakegamecraft.util.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Random;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class NukeManager {

    private Nuke[] nukes = new Nuke[NUKE_MAX_COUNT];

    interface State {

        void handle(Nuke nuke);

    }

    class LaserState implements State {

        @Override
        public void handle(Nuke nuke) {
            // Handle laser state logic
            System.out.println("Handling laser state for nuke at (" + nuke.x + ", " + nuke.y + ")");
        }
    }

    class ExplodeState implements State {

        @Override
        public void handle(Nuke nuke) {
            // Handle explode state logic
            System.out.println("Handling explode state for nuke at (" + nuke.x + ", " + nuke.y + ")");
        }
    }

    class AfterEffektState implements State {

        @Override
        public void handle(Nuke nuke) {
            // Handle after-effect state logic
            System.out.println("Handling after-effect state for nuke at (" + nuke.x + ", " + nuke.y + ")");
        }
    }

    class DespawnState implements State {

        @Override
        public void handle(Nuke nuke) {
            // Despawning is handled in handleNukeTick
        }
    }

    class Nuke {
        private final int x,y;
        private final State[] states;
        private int stateIndex;
        private final int[] ticksRemaining;
        private State currentState;

        public Nuke(int x, int y, int[] ticksPerState) {
            this.x = x;
            this.y = y;
            this.states = new State[]{
                    new LaserState(),
                    new ExplodeState(),
                    new AfterEffektState(),
                    new DespawnState()
            };
            this.stateIndex = 0;
            this.currentState = states[stateIndex];
            this.ticksRemaining = ticksPerState;
        }



        public void tick() {
            ticksRemaining[stateIndex]--;
            if (ticksRemaining[stateIndex] <= 0) {
                // Move to next state
                stateIndex = (stateIndex + 1) % states.length;
                currentState = states[stateIndex];
            }
            currentState.handle(this);
        }

        public javafx.scene.image.Image[] getSprites() {
            // Return the sprites for the current state
            // This is a placeholder, replace with actual sprite retrieval logic
            return new Image[]{};
        }


        public State getCurrentState() {
            return currentState;
        }
    }

    public void dropNuke(List<Point> occupied, int speed) {
        if (findEmptyNukeSlot() == -1) {
            return; // max nuke count reached
        }

        Random rand = new Random();

        if(rand.nextInt(100) > BOMB_SPAWN_CHANCE / speed) return; // Do not spawn nuke based on chance


        int[] ticksPerState = { 5, 10, 15, 20 }; // Example ticks for each state
        Nuke nuke = new Nuke(0, 0, ticksPerState); // Replace with actual coordinates
        nukes[findEmptyNukeSlot()] = nuke;
    }

    public void handleNukeTick() {
        for (int i = 0; i < nukes.length; i++) {
            if (nukes[i] != null) {
                nukes[i].tick();
                if (nukes[i].getCurrentState() instanceof DespawnState) {
                    nukes[i] = null; // Remove nuke after despawn state
                }
            }
        }
    }

    public void render(GraphicsContext gc) {

        for(Nuke nuke : nukes){
            if(nuke != null){
                // When nuke is instance of AfterEffektState, draw the nuke and the waste
                if(nuke.getCurrentState() instanceof AfterEffektState){

                }else {
                    // Draw the nuke sprite based on its current state
                    Image[] sprites = nuke.getSprites();
                    if (sprites.length > 0) {
                        gc.drawImage(sprites[0], nuke.x * TILE_SIZE, nuke.y * TILE_SIZE, TILE_SIZE, TILE_SIZE); // Replace with actual tile size
                    }
                }
            }
        }
    }

    private int findEmptyNukeSlot() {
        for (int i = 0; i < nukes.length; i++) {
            if (nukes[i] == null) {
                return i;
            }
        }
        return -1; // No empty slot found
    }

}
