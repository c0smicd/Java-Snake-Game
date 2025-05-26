package com.cosmic.snakegamecraft.entity;

import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.LinkedList;
import java.util.Objects;

public class Snake_Player extends Entity {

    public static class Segment {
        int x,y;

        public Segment(int x, int y) {this.x = x; this.y = y;}
    }

    private final LinkedList<Segment> body = new LinkedList<>();
    private Image headSprite, bodySprite, tailSprite, rotatedSprite;
    private int invurnabilityTicks = 0; // Timer for invincibility effect

    public Snake_Player(int startX, int startY){
        this.x = startX;
        this.y = startY;
        this.direction = Direction.RIGHT;
        body.add(new Segment(startX, startY));
        headSprite = SpriteManager.getSnakeHead();
        bodySprite = SpriteManager.getSnakeBody();
        tailSprite = SpriteManager.getSnakeTail();
        rotatedSprite = SpriteManager.getSnakeRotate();
    }

    /**
     * Grows the snake by adding a new segment at the end of the body.
     */
    public void grow(){
        Segment last = body.getLast();
        body.add(new Segment(last.x,last.y));
    }

    /**
     * Shrinks the snake by removing the last segment.
     */
    public void shrink(){
        if(body.size() > 1) {
            body.removeLast(); // Remove the last segment to shrink the snake
        }
    }

    /**
     * Activates invincibility for a short duration.
     */
    public void rainbowApple(){
        invurnabilityTicks = 20;
    }

    public boolean isInvulnerable() {
        return invurnabilityTicks > 0;
    }



    @Override
    public void update() {

        // Handle invincibility effect
        if(invurnabilityTicks > 0) invurnabilityTicks--;

        // Shift body segments

        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).x = body.get(i - 1).x;
            body.get(i).y = body.get(i - 1).y;
        }

        Segment head = body.getFirst();
        switch (direction) {
            case UP -> head.y--;
            case DOWN -> head.y++;
            case LEFT -> head.x--;
            case RIGHT -> head.x++;
        }

    }

    private double TILE_SIZE = 32;
    @Override
    public void render(GraphicsContext gc) {


        for (int i = 0; i < body.size(); i++) {
            Segment curr = body.get(i);
            Image spriteToDraw = null;

            if(i==0){
                // Draw head
                spriteToDraw = headSprite;
            }
            else if(i == body.size()-1){
                // Draw tail
                spriteToDraw = tailSprite;
            }else{

                Segment prev = body.get(i - 1);
                Segment next = body.get(i + 1);

                boolean turnRightDown = prev.y - 1 == curr.y && next.x - 1 == curr.x;
                boolean turnRightUp = prev.y + 1 == curr.y && next.x - 1 == curr.x;
                boolean turnLeftDown = prev.y - 1 == curr.y && next.x + 1 == curr.x;
                boolean turnLeftUp = prev.y + 1 == curr.y && next.x + 1 == curr.x;

                boolean horizontal = prev.y == curr.y && next.y == curr.y;
                boolean vertical = prev.x == curr.x && next.x == curr.x;

                if(horizontal || vertical) {
                    // Draw body
                    spriteToDraw = bodySprite;
                }
                else{
                    ImageView rotatedImage = new ImageView(rotatedSprite);

                    if(turnRightDown){
                        rotatedImage.setRotate(-45); // Right Down (└)
                        spriteToDraw = rotatedImage.getImage();
                    }
                    else if(turnRightUp){
                        spriteToDraw = rotatedSprite; // Right Up, already in correct orientation (┌)
                    }
                    else if(turnLeftDown){
                        rotatedImage.setRotate(90); // Left Down (┘)
                        spriteToDraw = rotatedImage.getImage();

                    }else if(turnLeftUp){
                        rotatedImage.setRotate(45); // Left Up (┐)
                        spriteToDraw = rotatedImage.getImage();
                    }
                }
            }
            gc.drawImage(Objects.requireNonNull(spriteToDraw), curr.x * TILE_SIZE, curr.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        }


    }

    public int getHeadX() {
        return body.getFirst().x;
    }

    public int getHeadY() {
        return body.getFirst().y;
    }
}
