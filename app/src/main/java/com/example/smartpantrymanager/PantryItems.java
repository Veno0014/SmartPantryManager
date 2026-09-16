package com.example.smartpantrymanager;

public class PantryItems {

    // Pantry item details
    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;

    public PantryItems(int id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}