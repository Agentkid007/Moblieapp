package com.example.pantrynerd.model;

//this stores information about a single ingredient in the users pantry
//like how many items, units, and expiry date
// Assignment 2.2 - pantry management one row of the pantry: name, quantity, unit and an optional expiry date
public class Ingred {

    private long id = -1;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // optional for the user

    //empty constructor is used when creating a new ingredient
    public Ingred() {

    }

    //this is a full constructor when reading an existing ingredients data
    public Ingred(long id, String name, double quantity, String unit, String expiryDate){
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    //used by AddEditIngredActivity to check if the ingredient is new or not
    //so can either update or create a new one

    public boolean isNew() {
        return id == -1;
    }
}

