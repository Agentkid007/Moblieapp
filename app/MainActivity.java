package com.example.pantrynerd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrynerd.adapter.PantryAdapter;
import com.example.pantrynerd.db.DatabaseHelper;
import com.example.pantrynerd.model.Ingred;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

//this is the launcher screen and will display the users ingredients if they have any

// Assignment 2.2 - pantry list screen] shows every pantry item from the database in a RecyclerView
// Assignment 3.1 RecyclerView + custom Adapter (PantryAdapter), bottom navigation bar and Intents are all used here
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private TextView emptyView;

    // it inflates the layout, finds the views, sets the RecyclerView layout manager and sets up the bottom navigation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.emptyView);

        //fab will open the AddEditIngredActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        // [Assignment 3.1 - Intents] explicit Intent to AddEditIngredActivity with no extras = "add new" mode
        fab.setOnClickListener(view -> startActivity(
                new Intent(MainActivity.this, AddEditIngredActivity.class)));

        NavHelper.setup(this, R.id.navigation_pantry);
    }

    // the list will always update when the app is opened
    // [Video concept: Activity lifecycle] onResume runs every time this screen comes back to the front
    // (e.g. after saving or deleting in AddEditIngredActivity) so the list is reloaded from the database here
    // and always shows the latest data
    @Override
    protected void onResume() {
        super.onResume();
        NavHelper.select(this, R.id.navigation_pantry);
        loadPantryItems();
    }

    //this will load all the ingredients from the db and will say if its empty
    // Assignment 2.2 - Read gets the rows from SQLite through DatabaseHelper and gives them to the adapter
    private void loadPantryItems() {
        List<Ingred> ingredients = databaseHelper.getAllPantryIngreds();

        if (ingredients.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        PantryAdapter adapter = new PantryAdapter(ingredients, new PantryAdapter.OnItemActionListener() {
            //when the user clicks on an ingredient it will open the AddEditIngredActivity
            @Override
            public void onItemClick(Ingred ingredient) {
                // Assignment 3.1 - Intents same screen, but this time the ingredient id is passed as an extra
                // so AddEditIngredActivity knows to load that row and run in "edit" mode
                Intent intent = new Intent(MainActivity.this, AddEditIngredActivity.class);
                intent.putExtra(AddEditIngredActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Ingred ingredient) {
                confirmDelete(ingredient);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    //this will confirm if the user wants to actually delete the ingredient
    // Assignment 2.2 - Delete asks for confirmation, then deletes the row from the database and refreshes the list
    private void confirmDelete(Ingred ingredient) {
        new AlertDialog.Builder(this)
                .setTitle(ingredient.getName())
                .setMessage("Remove this ingredient from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deletePantryIngred(ingredient.getId());
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
