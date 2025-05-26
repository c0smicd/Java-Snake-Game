package com.cosmic.snakegamecraft.logic;

import com.cosmic.snakegamecraft.util.Point;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemManager {
    private final List<Item> items = new ArrayList<>();
    private final Random rand = new Random();
    private final int gridSize;


    public ItemManager(int gridSize) {
        this.gridSize = gridSize;

    }

    public void spawnItem(ItemType type, List<Point> occupied){

        if(exitsItem(type)){
            return; // Item of this type already exists, do not spawn again
        }

        int  x, y;
        do{
            x = rand.nextInt(gridSize);
            y = rand.nextInt(gridSize);
        } while(isOccupied(x, y, occupied));


        Image sprite = switch(type) {
            case APPLE -> SpriteManager.getApple();
            case BAD_APPLE -> null;
            case GOLDEN_APPLE -> null;
            case RAINBOW_APPLE -> null;
        };

        items.add(new Item(type, x, y, sprite));
    }

    private boolean isOccupied(int x, int y, List<Point> occupied) {
        return occupied.stream().anyMatch(p -> p.x() == x && p.y() == y);
    }

    public void render(GraphicsContext gc, double tileSize){
        for(Item item : items){
            gc.drawImage(item.getSprite(), item.getX() * tileSize, item.getY() * tileSize, tileSize, tileSize);
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

    private boolean exitsItem(ItemType type) {
        return items.stream().anyMatch(item -> item.getType() == type);
    }




}
