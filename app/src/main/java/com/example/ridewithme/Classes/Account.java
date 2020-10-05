package com.example.ridewithme.Classes;

import java.util.ArrayList;

public class Account {

    private String name;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private ArrayList<Lonlat> location;

    public Account() {

    }

    public Account(String name, String lastName, String email, String password, ArrayList<Double> location, String username) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.location = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Lonlat> getLocation() {
        return location;
    }

    public String getUsername() {
        return username;
    }

    public String setName() {
        return name;
    }

    public String setLastName() {
        return lastName;
    }

    public String setEmail() {
        if (!email.contains("[a-z][A-Z]@gmail.com") || !email.contains("[a-z][A-Z]@walla.com")
                || !email.contains("[a-z][A-Z]@yahoo.com") || !email.contains("[a-z][A-Z]@mail.ru")) {
            return "error";
        }
        return email;
    }

    public String setPassword() {
        if(!(password.matches("[A-Z]+") && password.matches("[a-z]+") && password.matches("[0-9]+")) && password.length() < 8)
            return "error";
        return password;
    }

    public Account setUsername(String username) {
        this.username = username;
        return this;
    }
}
