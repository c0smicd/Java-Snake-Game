package com.cosmic.snakegamecraft.entity;


import com.cosmic.snakegamecraft.util.Point;
import com.cosmic.snakegamecraft.util.SpriteManager;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.cosmic.snakegamecraft.util.Constants.TILE_SIZE;

public class Snake_Player extends Entity {

    public static class Segment {
        int x,y;

        public Segment(int x, int y) {this.x = x; this.y = y;}
    }

    private final LinkedList<Segment> body = new LinkedList<>();
    private final Image headSprite;
    private final Image bodySprite;
    private final Image tailSprite;
    private final Image rotatedSprite;
    private int invulnerabilityTicks = 0; // Timer for invincibility effect
    private int highscore;
    private final double speedMultiplier;

    public Snake_Player(int startX, int startY, int initialLength, double speedMultiplier) {
        this.x = startX;
        this.y = startY;
        this.direction = Direction.RIGHT;
        this.speedMultiplier = speedMultiplier;

        // Initialize the snake body with the specified initial length
        for (int i = 0; i < initialLength; i++) {
            body.add(new Segment(startX - i, startY));
        }

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

        increaseHighscore((int) (10 * speedMultiplier)); // Increase score based on speed multiplier
    }

    /**
     * Shrinks the snake by removing the last segment.
     */
    public void shrink(){
        if(body.size() > 1) {
            body.removeLast(); // Remove the last segment to shrink the snake
        }

        increaseHighscore(-5);
    }

    /**
     * Activates invincibility for a short duration.
     */
    public void rainbowApple(){
        invulnerabilityTicks = (int) (20 * speedMultiplier);
        /* Speed multiplier speeds up the game internal update, thus
        the invincibility effect lasts shorter in real time. Hence, multiplying by speedMultiplier.


         */


    }

    public boolean isInvulnerable() {
        return invulnerabilityTicks > 0;
    }



    @Override
    public void update() {

        // Handle invincibility effect
        if(invulnerabilityTicks > 0) invulnerabilityTicks--;

        // Shift body segments

        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).x = body.get(i - 1).x;
            body.get(i).y = body.get(i - 1).y;
        }
        System.out.println("Snake head position: " + body.getFirst().x + ", " + body.getFirst().y);

        Segment head = body.getFirst();
        switch (direction) {
            case UP -> head.y--;
            case DOWN -> head.y++;
            case LEFT -> head.x--;
            case RIGHT -> head.x++;
        }

    }


    @Override
    public void render(GraphicsContext gc) {


        for (int i = 0; i < body.size(); i++) {
            Segment curr = body.get(i);
            Image spriteToDraw = null;

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);


            ImageView rotatedImage = null;

            if(i==0){
                // Draw head
                rotatedImage = new ImageView(headSprite);

                spriteToDraw = rotateImage(params, rotatedImage);

            }
            else if(i == body.size()-1){
                // Draw tail

                //TODO: Draw tail based on the i-2 body direction
                rotatedImage = new ImageView(tailSprite);

                spriteToDraw = rotateImage(params, rotatedImage);

            }else{

                //TODO: Maybe not needed, body tiles on turn looks also good

                rotatedImage = new ImageView(rotatedSprite);

                Segment prev = body.get(i - 1);
                Segment next = body.get(i + 1);

                boolean turnRightDown = prev.y - 1 == curr.y && next.x - 1 == curr.x;
                boolean turnRightUp = prev.y + 1 == curr.y && next.x - 1 == curr.x;
                boolean turnLeftDown = prev.y - 1 == curr.y && next.x + 1 == curr.x;
                boolean turnLeftUp = prev.y + 1 == curr.y && next.x + 1 == curr.x;

                boolean horizontal = prev.y == curr.y && next.y == curr.y;
                boolean vertical = prev.x == curr.x && next.x == curr.x;

               if (horizontal || vertical) {
                    // Draw body
                    spriteToDraw = bodySprite;
                }else{
                   spriteToDraw = bodySprite;
               }
                /*
                else{

                    if(turnRightDown){
                        rotatedImage.setRotate(-90); // Right Down (└)
                        spriteToDraw = rotatedImage.snapshot(params, null);
                    }
                    else if(turnRightUp){
                        spriteToDraw = rotatedSprite; // Right Up, already in correct orientation (┌)
                    }
                    else if(turnLeftDown){
                        rotatedImage.setRotate(90); // Left Down (┘)
                        spriteToDraw = rotatedImage.snapshot(params, null);

                    }else if(turnLeftUp){
                        rotatedImage.setRotate(90); // Left Up (┐)
                        spriteToDraw = rotatedImage.snapshot(params, null);
                    }
                }

                 */
            }
            //spriteToDraw = rotateImage(params, rotatedImage);

            gc.drawImage(Objects.requireNonNull(spriteToDraw), curr.x * TILE_SIZE, curr.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);



        }


    }

    private Image rotateImage(SnapshotParameters params, ImageView rotatedImage) {
        Image spriteToDraw = null;
        switch(direction){
            case UP -> {
                rotatedImage.setRotate(-90); // Up
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case DOWN -> {
                rotatedImage.setRotate(90); // Down
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case LEFT -> {
                rotatedImage.setRotate(180); // Left
                spriteToDraw = rotatedImage.snapshot(params, null);
            }
            case RIGHT -> {
                spriteToDraw = rotatedImage.snapshot(params, null);
            }

        }
        return spriteToDraw;
    }

    private void increaseHighscore(int amount) {
        highscore += amount;
    }


    public boolean checkSelfCollision(){
        Segment head = body.getFirst();

        for (int i = 1; i < body.size(); i++) {
            Segment segment = body.get(i);
            if (segment.x == head.x && segment.y == head.y) {
                return true; // Collision with itself
            }
        }

        return false;
    }

    public boolean checkWallCollision(int gridSize) {
        Segment head = body.getFirst();
        return head.x < 0 || head.x >= gridSize || head.y < 0 || head.y >= gridSize;
    }

    public int getHeadX() {
        return body.getFirst().x;
    }

    public int getHeadY() {
        return body.getFirst().y;
    }

    public List<Point> getOccupiedPoints() {
        return body.stream()
                .map(segment -> new Point(segment.x, segment.y))
                .toList();
    }

    public int getHighscore() {
        return highscore;
    }
}
