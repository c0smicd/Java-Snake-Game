package com.cosmic.snakegamecraft.util;

import javafx.scene.image.Image;

import java.util.Objects;

public class SpriteManager {

    private static final String SPRITE_PATH = "/images/";


    private static Image snakeHead;
    private static Image snakeBody;
    private static Image snakeTail;
    private static Image snakeRotate;
    private static Image apple;
    private static Image badApple;
    private static Image speedBoost;
    private static Image starItem;
    private static Image goldApple;
    private static Image bgTile1;
    private static Image bgTile2;
    private static Image bgGrassLeft;
    private static Image bgGrassRight;
    private static Image bgGrassTop;
    private static Image bgGrassBottom;
    private static Image bgScore;



    public static void loadSprites(){

        // Loading snake sprites from the resources folder
        snakeHead = load("snakes/player/head_snake.png");
        snakeBody = load("snakes/player/body_snake.png");
        snakeTail = load("snakes/player/tail_snake.png");
        snakeRotate = load("snakes/player/rotated_snake.png");

        // Loading items sprite
        apple = load("items/apple_snake.png");
        badApple = load("items/bad_apple.png");
        speedBoost = load("items/speed_boost.png");
        starItem = load("items/star.png");
        goldApple = load("items/gold_apple.png");

        // Loading background sprites
        bgTile1 = load("playground/grass-tile.png");
        bgTile2 = load("playground/grass-tile-2.png");
        bgGrassLeft = load("playground/grass_left.png");
        bgGrassRight = load("playground/grass_right.png");
        bgGrassTop = load("playground/grass_top.png");
        bgGrassBottom = load("playground/grass_bot.png");
        bgScore = load("playground/background-score.png");

    }

    private static Image load(String path){
        return new Image(Objects.requireNonNull(SpriteManager.class.getResourceAsStream(SPRITE_PATH + path)));
    }

    public static Image getSnakeHead() {
        return snakeHead;
    }
    public static Image getSnakeBody() {
        return snakeBody;
    }
    public static Image getSnakeTail() {
        return snakeTail;
    }
    public static Image getSnakeRotate() {
        return snakeRotate;
    }
    public static Image getApple() {
        return apple;
    }
    public static Image getBadApple() {
        return badApple;
    }
    public static Image getSpeedBoost() {
        return speedBoost;
    }
    public static Image getStarItem() {
        return starItem;
    }
    public static Image getGoldApple() {
        return goldApple;
    }
    public static Image getBgTile1() {
        return bgTile1;
    }
    public static Image getBgScore() {
        return bgScore;
    }
    public static Image getBgTile2() {
        return bgTile2;
    }
    public static Image getBgGrassLeft() {
        return bgGrassLeft;
    }
    public static Image getBgGrassRight() {
        return bgGrassRight;
    }
    public static Image getBgGrassTop() {
        return bgGrassTop;
    }
    public static Image getBgGrassBottom() {
        return bgGrassBottom;
    }
    public static String getSpritePath() {
        return SPRITE_PATH;
    }

}
