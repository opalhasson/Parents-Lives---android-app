package com.example.HW2.objects;

public class GameObject {

    private String PlayerName;
    private int score = 0;
    private int startObjectLocationX;
    private int startObjectLocationY;
    private int locationX;
    private int locationY;
    private String direction;


    public GameObject() {
    }

    public void initializationObject(int x, int y) {
        startObjectLocationX = locationX = x;
        startObjectLocationY = locationY = y;
        direction="";
    }

    public String getDirection() {
        return direction;
    }

    public GameObject setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public int getLocationX() {
        return locationX;
    }

    public GameObject setLocationX(int locationX) {
        this.locationX = locationX;
        return this;
    }

    public int getLocationY() {
        return locationY;
    }

    public GameObject setLocationY(int locationY) {
        this.locationY = locationY;
        return this;
    }

    public int getStartObjectLocationX() {
        return startObjectLocationX;
    }

    public int getStartObjectLocationY() {
        return startObjectLocationY;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }
}