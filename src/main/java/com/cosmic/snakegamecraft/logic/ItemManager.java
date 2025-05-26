package com.cosmic.snakegamecraft.logic;

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
        spawnItem(ItemType.APPLE);
    }

    public void spawnItem(ItemType type){

        int x = rand.nextInt(gridSize);
        int y = rand.nextInt(gridSize);
        Image sprite = switch(type) {
            case APPLE -> SpriteManager.getApple();
            case BAD_APPLE -> null;
            case GOLDEN_APPLE -> null;
            case RAINBOW_APPLE -> null;
        };

        items.add(new Item(type, x, y, sprite));
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




}
