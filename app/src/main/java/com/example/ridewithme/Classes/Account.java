package com.example.ridewithme.Classes;

import java.util.ArrayList;

public class Account {

    private String name;

    private String email;
    private String password;
    private ArrayList<Tour> tours;
    public Account() {
    }

    public Account(String name, String email, String password, ArrayList<Tour> tours) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.tours = new ArrayList<>();
    }


    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Tour> getTours() {
        return tours;
    }

    public Account setTours(ArrayList<Tour> tours) {
        this.tours = tours;
        return this;
    }



}
