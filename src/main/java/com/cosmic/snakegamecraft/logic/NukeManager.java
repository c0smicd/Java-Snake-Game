package com.cosmic.snakegamecraft.logic;

import com.cosmic.snakegamecraft.util.Point;
import com.cosmic.snakegamecraft.enums.Radiation;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class NukeManager {

    private List<Nuke> nukes = new ArrayList<>();
    private Radiation currentRadiation = Radiation.LOW;

    interface State {

        void handle(Nuke nuke);

    }

    class LaserState implements State {

        @Override
        public void handle(Nuke nuke) {
            nuke.setSprite(SpriteManager.getLaser());
        }
    }

    class ExplodeState implements State {

        @Override
        public void handle(Nuke nuke) {
            nuke.setSprite(SpriteManager.getMushroomCloud());
        }
    }

    class AfterEffektState implements State {

        private boolean handled = false;


        @Override
        public void handle(Nuke nuke) {

            if(handled) return;

            // Fallout um das 2x2-Areal
            List<Point> wastePositions = new ArrayList<>();
            int nx = nuke.x;
            int ny = nuke.y;
            // Alle Felder um das 2x2-Areal
            for(int dx = -1; dx <= 2; dx++) {
                for(int dy = -1; dy <= 2; dy++) {
                    // Nur Felder außerhalb des 2x2-Areals
                    if (dx < 0 || dx > 1 || dy < 0 || dy > 1) {
                        wastePositions.add(new Point(nx + dx, ny + dy));
                    }
                }
            }
            // Filtere ungültige Positionen (z.B. außerhalb des Spielfelds)
            wastePositions = wastePositions.stream().filter(p -> p.x() >= 0 && p.x() < GRID_SIZE && p.y() >= 0 && p.y() < GRID_SIZE).toList();

            wastePositions = wastePositions.stream().filter(_ -> {
                Random random = new Random();
                return random.nextInt(100) < WASTE_SPAWN_CHANCE; // Random chance to spawn waste
            }).toList();

            nuke.wastePositions = wastePositions;

            nuke.setSprite(SpriteManager.getBgTileFalloutWasted());

            handled = true;

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

        private List<Point> wastePositions;

        private Image sprite;

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

        public Image getSprite() {
            return sprite;
        }

        public void setSprite(Image sprite) {
            this.sprite = sprite;
        }


        public State getCurrentState() {
            return currentState;
        }

        public void setWastePositions(List<Point> wastePositions) {
            this.wastePositions = wastePositions;
        }

        public List<Point> getWastePositions() {
            return wastePositions;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public void dropNuke(List<Point> occupied, int speed) {
        int emptySlot = findEmptyNukeSlot();

        if (emptySlot == -1) {
            return; // max nuke count reached
        }

        Random rand = new Random();

        if(rand.nextDouble(100) > BOMB_SPAWN_CHANCE / speed) return; // Do not spawn nuke based on chance

        int[] ticksPerState = {rand.nextInt(5 * speed,10 * speed), rand.nextInt(5 * speed,20 * speed), rand.nextInt(20 * speed,50 * speed), 1};

        int x, y;

        // Search a free 2x2 area
        do {
            x = rand.nextInt(GRID_SIZE);
            y = rand.nextInt(GRID_SIZE);
        } while (isOccupied(x, y, occupied));

        Nuke nuke = new Nuke(x, y, ticksPerState);

        if(emptySlot == nukes.size()) nukes.add(nuke);
        else nukes.set(emptySlot, nuke);
    }

    public void handleNukeTick() {
        for (int i = 0; i < nukes.size(); i++) {
            if (nukes.get(i) != null) {
                nukes.get(i).tick();

                if(!(nukes.get(i).getCurrentState() instanceof LaserState)) {
                    changeRadiationLevel();
                }

                if (nukes.get(i).getCurrentState() instanceof DespawnState) {
                    nukes.set(i, null); // Remove nuke after despawn state
                }
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Nuke nuke : nukes) {
            if (nuke == null) continue;

            Image sprite = nuke.getSprite();

            // Draw nuke
            gc.drawImage(sprite, nuke.x * TILE_SIZE, nuke.y * TILE_SIZE, TILE_SIZE * 2, TILE_SIZE * 2);

            // If AfterEffektSate draw waste
            if (nuke.getCurrentState() instanceof AfterEffektState) {
                for (Point point : nuke.getWastePositions()) {
                    gc.drawImage(sprite, point.x() * TILE_SIZE, point.y() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Get plate based on radiation level
        Image plateToRender = switch (currentRadiation) {
            case LOW -> SpriteManager.getLowRadiation();
            case MEDIUM -> SpriteManager.getMediumRadiation();
            case HIGH -> SpriteManager.getHighRadiation();
        };

        // Scale factor to fit height 64
        double availableHeight = 64.0;
        double scale = availableHeight / plateToRender.getHeight();

        double scaledWidth = plateToRender.getWidth() * scale;
        double scaledHeight = availableHeight;

        // Position on the right side, starting at y = 640
        double x = 640 - scaledWidth;
        double y = 640;

        gc.drawImage(plateToRender, x - 10, y, scaledWidth, scaledHeight);

    }

    private int findEmptyNukeSlot() {
        if(nukes.size() < NUKE_MAX_COUNT - 1) return nukes.size();

        for (int i = 0; i < nukes.size(); i++) {
            if(nukes.get(i) == null) return i;
        }

        return -1;
    }

    public boolean checkNukeCollision(int x, int y) {
        for (Nuke nuke : nukes) {
            if (nuke != null && !(nuke.getCurrentState() instanceof LaserState) && nuke.getX() == x && nuke.getY() == y) {
                return true; // Collision with a nuke
            }
        }
        return false; // No collision
    }

    public boolean checkWasteCollision(int x, int y) {
        for (Nuke nuke : nukes) {
            if (nuke != null && nuke.wastePositions != null) {
                for (Point waste : nuke.wastePositions) {
                    if (waste.x() == x && waste.y() == y) {
                        return true; // Collision with waste
                    }

                }
            }
        }
        return false; // No collision with waste
    }


    private boolean isOccupied(int x, int y, List<Point> occupied) {
        return occupied.stream().anyMatch(p -> p.x() == x && p.y() == y) || nukes.stream().anyMatch(nuke -> nuke != null && nuke.getX() == x && nuke.getY() == y);
    }

    private int getRealSizeNukes(){
        int size = 0;
        for(Nuke nuke : nukes) {
            if(nuke != null && !(nuke.getCurrentState() instanceof LaserState)) size++;
        }

        return size;
    }

    private void changeRadiationLevel(){
        int realSize = getRealSizeNukes();

        if (realSize > 3) this.currentRadiation = Radiation.HIGH;
        else if(realSize > 2) this.currentRadiation = Radiation.MEDIUM;
        else this.currentRadiation = Radiation.LOW;
    }

    public Radiation getCurrentRadiation() {
        return currentRadiation;
    }

}
