package com.example.pantrynerd.model;

import java.util.ArrayList;
import java.util.List;
//this stores information about a recipe and its ingredients
// Assignment 2.2 - recipe collection a recipe = name + preparation steps + list of required ingredients
public class Recipe {

    private final long id;
    private final String name;

    //this will return the steps of the recipe
    private final String steps;

    //empty at first but populates with databaseHelper
    private List<RecipeIngred> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getSteps() {
        return steps;
    }

    public List<RecipeIngred> getIngreds() {
        return ingredients;
    }

    public void setIngreds(List<RecipeIngred> ingredients) {
        this.ingredients = ingredients;
    }


    //helper class for the recyclerView (1 ingredient vs 2 ingredients [plural])
    public String getIngredCount() {
        int count = ingredients.size();
        return count + (count == 1 ? " ingredient" : " ingredients");
    }
}
