package com.example.pantrynerd.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrynerd.R;
import com.example.pantrynerd.model.Ingred;

import java.util.List;

// this recycler adapter will draw the pantry list on the main screen.
// each row will show the ingredient name, quantity and unit.
// Assignment 3.1 - RecyclerView with a custom Adapter [Video concept: RecyclerView + Adapter]
// the adapter sits between the data (List<Ingred> from the database) and the RecyclerView:
// onCreateViewHolder inflates a row, onBindViewHolder fills it with one ingredient, getItemCount says how many rows.
// the ViewHolder keeps the row views so findViewById is not repeated while scrolling.
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder>{

    public interface OnItemActionListener {
        void onItemClick(Ingred ingredient);
        void onDeleteClick(Ingred ingredient);
    }

    private final List<Ingred> ingredients;
    private final OnItemActionListener listener;
    public PantryAdapter(List<Ingred> ingredients, OnItemActionListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    //this is called by the recycler view when it needs a new row view

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingred_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    //called by the recycler view when it needs to bind data to a row view
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Ingred ingredient = ingredients.get(position);

        String quantityText = ingredient.getQuantity() == Math.floor(ingredient.getQuantity())
                ? String.valueOf((int) ingredient.getQuantity()) : String.valueOf(ingredient.getQuantity());

        holder.nameTextView.setText(ingredient.getName());
        holder.quantityTextView.setText(quantityText + " " + ingredient.getUnit());

        //hides expiry date if the user doesn't add one
        if (ingredient.getExpiryDate() != null && !ingredient.getExpiryDate().isEmpty()) {
            holder.expiryDateTextView.setText(ingredient.getExpiryDate());
            holder.expiryDateTextView.setVisibility(View.VISIBLE);
        } else {
            holder.expiryDateTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> listener.onItemClick(ingredient));
        holder.deleteButton.setOnClickListener(view -> listener.onDeleteClick(ingredient));
    }

    //tells the recycler view how many rows exist
    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    //this will hold a reference to the rows widgets
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView quantityTextView;
        private final TextView expiryDateTextView;
        private final ImageButton deleteButton;

        PantryViewHolder (@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            expiryDateTextView = itemView.findViewById(R.id.expiryDateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }


}
