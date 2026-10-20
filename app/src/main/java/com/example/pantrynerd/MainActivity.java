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

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private TextView emptyView;

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
        fab.setOnClickListener(view -> startActivity(
                new Intent(MainActivity.this, AddEditIngredActivity.class)));

        NavHelper.setup(this, R.id.navigation_pantry);
    }

    // the list will always update when the app is opened
    @Override
    protected void onResume() {
        super.onResume();
        NavHelper.select(this, R.id.navigation_pantry);
        loadPantryItems();
    }

    //this will load all the ingredients from the db and will say if its empty
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
