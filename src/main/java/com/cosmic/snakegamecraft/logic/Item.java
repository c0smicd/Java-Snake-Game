package com.cosmic.snakegamecraft.logic;

import javafx.scene.image.Image;

public class Item {

    private final ItemType type;
    private final int x,y;
    private final Image sprite;

    public Item(ItemType type, int x, int y, Image sprite) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public ItemType getType() {
        return type;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Image getSprite() {
        return sprite;
    }

}
