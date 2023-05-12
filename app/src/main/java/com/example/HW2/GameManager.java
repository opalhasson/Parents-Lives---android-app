package com.example.HW2;

import com.example.HW2.objects.Coin;
import com.example.HW2.objects.GameObject;

public class GameManager {
    private final int MAX_LIVES = 3;
    private final int NUM_OF_PATHS = 5;
    private final int LENGTH_OF_PATHS = 7;

    private int lives = MAX_LIVES;
    private GameObject player;
    private GameObject Object[];
    private Coin coin;

    private String DIRECTION[] = {
            "UP",
            "DOWN",
            "LEFT",
            "RIGHT"
    };

    private Boolean isCrash = false;

    public GameManager() {
        player = new GameObject();
        Object = new GameObject[NUM_OF_PATHS];
        Object[0] = new GameObject();
        Object[1] = new GameObject();
        Object[2] = new GameObject();
        Object[3] = new GameObject();
        Object[4] = new GameObject();

        coin = new Coin();
        ObjectsLocations();
    }

    public GameObject getPlayer() {
        return player;
    }

    public GameObject[] getObject() {
        return Object;
    }

    public Boolean getCrash() {
        return isCrash;
    }

    public GameManager setCrash(Boolean crash) {
        isCrash = crash;
        return this;
    }

    public int getLives() {
        return lives;
    }

    public void reduceLives() {
        lives--;
    }

    public void ObjectsLocations() {
        player.initializationObject(LENGTH_OF_PATHS, 2);
        Object[0].initializationObject(0, 0);
        Object[1].initializationObject(0, 1);
        Object[2].initializationObject(0, 2);
        Object[3].initializationObject(0, 3);
        Object[4].initializationObject(0, 4);
    }

    public Coin getCoin() {
        return coin;
    }

    public void randomCoinDirectionMove() {
        if (coin.getCoinX() < LENGTH_OF_PATHS)
            coin.setCoinX(coin.getCoinX() + 1);
        else coin.setCoinX(-1);
    }

    public void randomObjectDirectionMove() {
        int r = (int) (Math.random() * Object.length);
        if (Object[r].getLocationX() == -1 || Object[r].getLocationX() == 0 || Object[r].getLocationX() == LENGTH_OF_PATHS) {
            Object[r].setDirection(DIRECTION[(int) (Math.random() * 2)]);
            if (Object[r].getDirection() == "DOWN")
                Object[r].setLocationX(Object[r].getStartObjectLocationX());
        }

        for (int i = 0; i < Object.length; i++) {
            if (Object[i].getDirection() == "DOWN") {
                if (Object[i].getLocationX() < LENGTH_OF_PATHS)
                    Object[i].setLocationX(Object[i].getLocationX() + 1);
                else Object[i].setDirection("");
            } else Object[i].setLocationX(-1);
        }
    }

    public void playerMove(String direction) {
        switch (direction) {
            case "LEFT":
                if (player.getLocationY() > 0 ) {
                    player.setLocationY(player.getLocationY() - 1);
                }
                break;
            case "RIGHT":
                if (player.getLocationY() < NUM_OF_PATHS - 1) {
                    player.setLocationY(player.getLocationY() + 1);
                }
                break;
        }
    }

    public void checkCrash() {
        for (int i = 0; i < Object.length; i++) {
            if (player.getLocationX() == Object[i].getLocationX() && player.getLocationY() == Object[i].getLocationY()) {
                isCrash = true;
                Object[i].setLocationX(-1);
                if (lives > 0)
                    reduceLives();
            }
        }
    }

    public boolean coinAndPlayerInTheSamePlace() {
        if (player.getLocationX() == coin.getCoinX() && player.getLocationY() == coin.getCoinY())
            return true;
        else return false;
    }
}





























