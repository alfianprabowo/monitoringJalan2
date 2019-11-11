package com.example.alfia.monitoringjalan.User;

public class User {

    private String username;
    private String password;
    private int structural;

    public User(){

    }

    public User(String username, String password, int structural) {
        this.username = username;
        this.password = password;
        this.structural = structural;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStructural() {
        return structural;
    }

    public void setStructural(int structural) {
        this.structural = structural;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
