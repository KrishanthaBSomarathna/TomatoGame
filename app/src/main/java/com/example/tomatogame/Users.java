package com.example.tomatogame;

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
