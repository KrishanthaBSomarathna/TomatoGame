package com.example.tomatogame.Model;
/**
 * Represents a user in the game, containing username and score.
 * This class is used to store and retrieve user information from Firebase Realtime Database.
 */
public class Users {
    private String userName;
    private String score;


    public Users(String userName, String score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public String getScore() {
        return score;
    }

}
