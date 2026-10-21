package com.example.pantrynerd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrynerd.adapter.RecipeAdapter;
import com.example.pantrynerd.db.DatabaseHelper;
import com.example.pantrynerd.model.Recipe;

import java.util.List;

//this will show both the suggested recipes and the close recipes which require a missing ingredient
//both of these lists will get reloaded using onResume so its always up to date

// Assignment 2.2 - Suggested Recipes screen] runs the strict-matching logic (Assignment 2.3, in DatabaseHelper)
// and lists only the recipes the user can make right now.
// the "Almost There" list under it is the optional stretch and is kept separate from the strict list.
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView suggestedRecyclerView;
    private RecyclerView closeRecyclerView;
    private TextView emptyView;
    private TextView closeHeaderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipe);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        suggestedRecyclerView = findViewById(R.id.suggestedRecyclerView);
        suggestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        closeRecyclerView = findViewById(R.id.closeRecyclerView);
        closeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyView = findViewById(R.id.emptyView);
        closeHeaderTextView = findViewById(R.id.closeHeaderTextView);

        NavHelper.setup(this, R.id.navigation_recipes);
    }

    // [Video concept: Activity lifecycle] both lists are reloaded every time the screen is shown, so adding or
    // removing a pantry ingredient changes the suggestions straight away (the add/remove demo in the video)
    @Override
    protected void onResume() {
        super.onResume();
        NavHelper.select(this, R.id.navigation_recipes);
        loadSuggestedRecipes();
        loadCloseRecipes();
    }

    //shows only the recipes the user can make from their pantry
    //otherwise will show a message saying there are no recipes
    // [Assignment 2.2 - feedback when zero recipes match] emptyView shows "No recipes match your pantry yet"
    // instead of a blank screen
    private void loadSuggestedRecipes() {
        List<Recipe> suggestedRecipes = databaseHelper.getSuggestedRecipes();

        if (suggestedRecipes.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            suggestedRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            suggestedRecyclerView.setVisibility(View.VISIBLE);
        }

        RecipeAdapter adapter = new RecipeAdapter(suggestedRecipes, this::openRecipeDetail);
        suggestedRecyclerView.setAdapter(adapter);
    }

    //this is the close recipes that are one ingredient away
    //the header will only show up if there are any recipes that are close
    // [Assignment 2.3 - optional stretch "Almost There"] separate list with its own header, hidden when empty
    private void loadCloseRecipes() {
        List<Recipe> closeRecipes = databaseHelper.getCloseRecipes();

        boolean hasCloseRecipes = !closeRecipes.isEmpty();
        closeHeaderTextView.setVisibility(hasCloseRecipes ? View.VISIBLE : View.GONE);
        closeRecyclerView.setVisibility(hasCloseRecipes ? View.VISIBLE : View.GONE);

        RecipeAdapter adapter = new RecipeAdapter(closeRecipes, this::openRecipeDetail);
        closeRecyclerView.setAdapter(adapter);
    }

    //this will open the recipe details for both lists
    // [Assignment 3.1 - Intents] only the recipe id is passed; RecipeDetailActivity loads the full recipe from the db
    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
