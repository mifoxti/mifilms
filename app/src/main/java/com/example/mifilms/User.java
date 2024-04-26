package com.example.mifilms;

public class User {
    public String id, usname, pass, adult, playlists, subus;


    public User() {

    }

    public User(String id, String usname, String pass, String adult, String playlists, String subus) {
        this.id = id;
        this.usname = usname;
        this.pass = pass;
        this.adult = adult;
        this.playlists = playlists;
        this.subus = subus;
    }

    public User(String id, String usname, String pass) {
        this.id = id;
        this.usname = usname;
        this.pass = pass;
        this.adult = "1";
        this.playlists = "Избранное;";
        this.subus = "None";
    }

    public String getPass(){
        return this.pass;
    }
}