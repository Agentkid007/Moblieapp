package com.example.pantrynerd.model;

//this handles one ingredient requirement which belongs to a recipe, like 1 egg or 200ml milk
//and will check the users pantry if they have it
// Assignment 2.3 this is what a recipe needs - the strict matching compares each one against the pantry
public class RecipeIngred {

    private final String name;
    private final double quantity;
    private final String unit;
    public RecipeIngred(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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

    public String toDisplayString() {
        String quantityText = quantity == Math.floor(quantity) ? String.valueOf((int) quantity) :
                String.valueOf(quantity);
        String unitText = (unit == null || unit.trim().isEmpty()) ? "" : unit + " ";
        return quantityText + " " + unitText + name;
    }
}
