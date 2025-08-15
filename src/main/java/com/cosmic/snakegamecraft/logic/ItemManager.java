package com.cosmic.snakegamecraft.logic;

import com.cosmic.snakegamecraft.util.Item;
import com.cosmic.snakegamecraft.util.ItemType;
import com.cosmic.snakegamecraft.util.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cosmic.snakegamecraft.util.Constants.*;

public class ItemManager {
    private final List<Item> items = new ArrayList<>();
    private final Random rand = new Random();
    private final int gridSize;


    public ItemManager(int gridSize) {
        this.gridSize = gridSize;

    }

    public void spawnItem(ItemType type, List<Point> occupied, int speed){

        int random = rand.nextInt(100);

        // For normal items which only spawn once (Apple, Goldenapple, speedup)
        if(exitsItem(type)) return;

        if(type == ItemType.BAD_APPLE && !canSpawnBadApple()) {
            return; // Item of this type already exists, do not spawn again
        }

        if(type == ItemType.IODINE && !canSpawnIodine()) {
            return;
        }
        if(type == ItemType.BAD_APPLE && random > BAD_APPLE_CHANCE / speed) {
            return;
        }else if(type == ItemType.STAR && random > STAR_CHANCE / speed) {
            return;
        } else if (type == ItemType.SPEED_UP && random > SPEED_UP_CHANCE / speed) {
            return;
        } else if (type == ItemType.GOLDEN_APPLE && random > GOLDEN_APPLE_CHANCE / speed) {
            return;
        }

        System.out.println("Spawn Iodine");

        int[] points = getFreePosition(occupied);

        Image sprite = getSpriteForType(type);

        items.add(new Item(type, points[0], points[1], sprite));
    }


    private int[] getFreePosition(List<Point> occupied) {
        int x, y;
        do {
            x = rand.nextInt(gridSize);
            y = rand.nextInt(gridSize);
        } while (isOccupied(x, y, occupied));
        return new int[]{x, y};
    }

    private Image getSpriteForType(ItemType type) {
        return switch (type) {
            case APPLE -> SpriteManager.getApple();
            case BAD_APPLE -> SpriteManager.getBadApple();
            case GOLDEN_APPLE -> SpriteManager.getGoldApple();
            case STAR -> SpriteManager.getStarItem();
            case SPEED_UP -> SpriteManager.getSpeedBoost();
            case IODINE -> SpriteManager.getIodine();
        };
    }

    private boolean isOccupied(int x, int y, List<Point> occupied) {
        return occupied.stream().anyMatch(p -> p.x() == x && p.y() == y) || items.stream().anyMatch(item -> item.getX() == x && item.getY() == y);
    }

    public void render(GraphicsContext gc){
        for(Item item : items){
            gc.drawImage(item.getSprite(), item.getX() * TILE_SIZE, item.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    public Item checkCollision(int x, int y){
        for (Item item : items){
            if(item.getX() == x && item.getY() == y){
                return item; // Return the item that was collected
            }
        }

        return null;
    }


    public void removeItem(Item item){
        items.remove(item);
    }

    public void removeItem(ItemType type) {

        for(Item item : items){
            if(item.getType() == type){
                items.remove(item);
                return; // Remove only the first item of this type
            }
        }
    }

    private boolean exitsItem(ItemType type) {
        return items.stream().anyMatch(item -> (type == ItemType.APPLE)
                || (type == ItemType.GOLDEN_APPLE)
                || (type == ItemType.SPEED_UP)
                || (type == ItemType.STAR) &&
                item.getType() == type);
    }


    public boolean existsBadApple() {
        return items.stream().anyMatch(item -> item.getType() == ItemType.BAD_APPLE);
    }


    private boolean canSpawnBadApple() {
        return items.stream().filter(item -> item.getType() == ItemType.BAD_APPLE).toList().size() < BAD_APPLE_MAX_COUNT;
    }

    private boolean canSpawnIodine() {
        return items.stream().filter(item -> item.getType() == ItemType.IODINE).toList().size() < IODINE_MAX_COUNT;
    }

    public List<Point> getOccupiedItemPoints() {
        List<Point> occupied = new ArrayList<>();
        for (Item item : items) {
            occupied.add(new Point(item.getX(), item.getY()));
        }
        return occupied;
    }

}

