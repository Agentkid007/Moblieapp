package com.example.pantrynerd;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pantrynerd.db.DatabaseHelper;
import com.example.pantrynerd.model.Ingred;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;
//this will add or edit an ingredient for a single ingredient
// Assignment 2.2 - Add/Edit Ingredient screen one screen used for Create, Update and Delete of a pantry item
// Assignment 3.1 - input validation] the form is checked before anything is saved (see isFormValid)
public class AddEditIngredActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "extra_ingredient_id";

    private static final String[] UNITS = {
            "g", "kg", "ml", "l", "pcs", "slice", "cup", "tsp", "tbsp"
    };

    private DatabaseHelper databaseHelper;

    //this will either be a new ingredient or one from the db
    private Ingred ingredient;

    private TextInputLayout nameInputLayout;
    private TextInputLayout quantityInputLayout;
    private TextInputEditText nameEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText expiryDateEditText;
    private Spinner unitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingred);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        quantityInputLayout = findViewById(R.id.quantityInputLayout);
        nameEditText = findViewById(R.id.nameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        unitSpinner = findViewById(R.id.unitSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, UNITS);
        unitSpinner.setAdapter(adapter);

        //will show the date picker when the user clicks on the expiry date
        expiryDateEditText.setOnClickListener(view -> showDatePicker());

        //-1 means that it is a new id since no id would have been passed
        // [Assignment 3.1 - Intents] reads the extra that MainActivity sent: no extra = add mode, an id = edit mode
        long ingredientId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);
        if (ingredientId != -1) {
            ingredient = databaseHelper.getPantryIngred(ingredientId);
            if (ingredient != null) {
                populateForEdit();
            }
        } else {
            ingredient = new Ingred();
        }

        assert ingredient != null;
        toolbar.setTitle(ingredient.isNew()
                ? R.string.title_add_ingredient : R.string.title_edit_ingredient);

        findViewById(R.id.saveButton).setOnClickListener(view -> saveIngredient());
        findViewById(R.id.deleteButton).setOnClickListener(view -> deleteIngredient());

        // will show the delete button if the ingredient is not new
        if (!ingredient.isNew()) {
            findViewById(R.id.deleteButton).setVisibility(android.view.View.VISIBLE);
        }
    }

    //when a user click an ingredient to edit, it will fill all the editable fields in the textbox
    private void populateForEdit() {
        nameEditText.setText(ingredient.getName());
        quantityEditText.setText(formatQuantity(ingredient.getQuantity()));
        expiryDateEditText.setText(ingredient.getExpiryDate());

        int position = indexOf(ingredient.getUnit());
        if (position >= 0) {
            unitSpinner.setSelection(position);
        }
    }

    //this opens the date picker when the user clicks on the expiry date
    //auto sets to the current date
    //formatted to yyyy-mm-dd
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year, month + 1, dayOfMonth);
            expiryDateEditText.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //this will check the form before its saved, for example: name is required, and quantity needs
    // to be greater than 0. if there is any errors in the data the field will show where the error is
    // [Assignment 3.1 - input validation] name must not be empty and quantity must be a number greater than 0
    private boolean isFormValid(String name, String quantityText) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError(getString(R.string.error_name_required));
            isValid = false;
        } else {
            nameInputLayout.setError(null);
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            //anything that can't be parsed is invalid (eg Letters)
            quantity = -1;
        }

        if (quantityText == null || quantityText.trim().isEmpty() || quantity <= 0) {
            quantityInputLayout.setError(getString(R.string.error_invalid_quantity));
            isValid = false;
        } else {
            quantityInputLayout.setError(null);
        }

        return isValid;
    }

    // reads all the fields and inserts if new ingredient or will update
    // [Assignment 3.2 - CRUD] Create (insert) when the ingredient is new, Update when it was loaded from the db
    private void saveIngredient() {
        String name = nameEditText.getText() == null ? "" : nameEditText.getText().toString().trim();
        String quantityText = quantityEditText.getText() == null ? "" : quantityEditText.getText().toString().trim();

        if (!isFormValid(name, quantityText)) {
            return;
        }

        ingredient.setName(name);
        ingredient.setQuantity(Double.parseDouble(quantityText));
        ingredient.setUnit((String) unitSpinner.getSelectedItem());

        String expiryDate = expiryDateEditText.getText() == null ? "" : expiryDateEditText.getText().toString().trim();
        ingredient.setExpiryDate(expiryDate.isEmpty() ? null : expiryDate);

        if (ingredient.isNew()) {
            databaseHelper.insertPantryIngreds(ingredient);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.updatePantryIngred(ingredient);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    //will only delete if it exists in the database
    // [Assignment 3.2 - CRUD] Delete
    private void deleteIngredient() {
        if (!ingredient.isNew()) {
            databaseHelper.deletePantryIngred(ingredient.getId());
            Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    //if its a whole number it wont display a .0
    private String formatQuantity(double quantity) {
        return quantity == Math.floor(quantity)
                ? String.valueOf((int) quantity)
                : String.valueOf(quantity);
    }

    //finds the position in the units array
    private int indexOf(String unit) {
        if (unit == null) return -1;
        for (int i = 0; i < AddEditIngredActivity.UNITS.length; i++) {
            if (AddEditIngredActivity.UNITS[i].equalsIgnoreCase(unit)) return i;
        }
        return -1;
    }
}
