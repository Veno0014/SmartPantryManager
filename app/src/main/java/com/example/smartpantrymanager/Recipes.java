package com.example.smartpantrymanager;

public class Recipes {

    // Recipe details
    private int id;
    private String name;
    private String method;

    // Recipe constructor
    public Recipes(int id, String name, String method) {
        this.id = id;
        this.name = name;
        this.method = method;
    }

    // gets recipe id
    public int getId() {
        return id;
    }

    //gets recipe name
    public String getName() {
        return name;
    }

    // gets recipe method
    public String getMethod() {
        return method;
    }
}