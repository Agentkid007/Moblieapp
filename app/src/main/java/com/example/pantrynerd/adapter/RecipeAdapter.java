package com.example.pantrynerd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrynerd.R;
import com.example.pantrynerd.model.Recipe;

import java.util.List;

// this recycler adapter will show the recipe list on the suggested recipes screen
// [Assignment 3.1 - RecyclerView with a custom Adapter] same idea as PantryAdapter but for List<Recipe>;
// used for both the strict suggestions list and the "Almost There" list
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override

    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingred, parent, false);
        return new RecipeViewHolder(view);
    }

    //fills a row with the recipe name and ingredients count
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.nameTextView.setText(recipe.getName());
        holder.ingredientCountTextView.setText(recipe.getIngredCount());
        holder.itemView.setOnClickListener(view -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    //this holds a reference to the rows textViews
    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView ingredientCountTextView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ingredientCountTextView = itemView.findViewById(R.id.ingredientCountTextView);
        }
    }
}
