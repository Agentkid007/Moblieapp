package com.example.pantrynerd;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//this handles the bottom navigation to swap to the different screens
//each activity will call this class
// Assignment 3.1 - working navigation element] bottom navigation bar shared by the Pantry / Recipes / Settings screens,
// each tab starts an Intent to that Activity
final class NavHelper {
    private NavHelper() {
    }

    // currentItemId is the id of the activity that is currently open
    static void select(AppCompatActivity activity, int currentItemId) {
        BottomNavigationView bottomNavigation = activity.findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(currentItemId);
        }
    }
    static void setup(AppCompatActivity activity, int currentItemId) {
        BottomNavigationView bottomNavigation = activity.findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null){
            return;
        }

        bottomNavigation.setSelectedItemId(currentItemId);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentItemId) {
                return true;
            }

            //sets the tapped tabs id to the activity that will be opened
            Class<?> targetActivity = null;
            if (itemId == R.id.navigation_pantry) {
                targetActivity = MainActivity.class;
            } else if (itemId == R.id.navigation_recipes) {
                targetActivity = SuggestedRecipesActivity.class;
            } else if (itemId == R.id.navigation_settings) {
                targetActivity = SettingsActivity.class;
            }

            if (targetActivity != null) {
                // [Assignment 3.1 - Intents] CLEAR_TOP + SINGLE_TOP reuse a screen that is already open instead of stacking copies
                Intent intent = new Intent(activity, targetActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }
            return true;
        });
    }
}
