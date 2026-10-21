package com.example.pantrynerd;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pantrynerd.db.DatabaseHelper;
import com.example.pantrynerd.model.Recipe;
import com.example.pantrynerd.model.RecipeIngred;

//this shows the recipes details, like the name and ingredients list and steps

// Assignment 2.2 - recipe detail screen shows the full ingredient list and the method for the selected recipe
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        //used back arrow of toolbar which closes the current screen and back to where it was
        //opened from
        toolbar.setNavigationOnClickListener(view -> finish());

        // [Assignment 3.1 - Intents] receives the recipe id sent by SuggestedRecipesActivity
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe;
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            recipe = databaseHelper.getRecipe(recipeId);
        }

        if (recipe == null) {
            finish();
            return;
        }

        toolbar.setTitle(recipe.getName());

        TextView recipeNameTextView = findViewById(R.id.recipeNameTextView);
        TextView ingredientsTextView = findViewById(R.id.ingredientsTextView);
        TextView stepsTextView = findViewById(R.id.stepsTextView);

        recipeNameTextView.setText(recipe.getName());

        //builds a list of the ingredients needed with bullet points
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (RecipeIngred ingredient : recipe.getIngreds()) {
            ingredientsBuilder.append("• ").append(ingredient.toDisplayString()).append("\n");
        }
        ingredientsTextView.setText(ingredientsBuilder.toString().trim());

        stepsTextView.setText(recipe.getSteps());
    }
}
