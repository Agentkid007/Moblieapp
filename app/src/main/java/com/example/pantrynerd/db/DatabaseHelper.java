package com.example.pantrynerd.db;

import static com.example.pantrynerd.util.IngredNormalization.normalizeUnit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pantrynerd.model.Ingred;
import com.example.pantrynerd.model.Recipe;
import com.example.pantrynerd.util.IngredNormalization;

import java.util.ArrayList;
import java.util.List;

import com.example.pantrynerd.model.RecipeIngred;
import java.util.HashMap;
import java.util.Map;

// I am using SQLite as the database
// there is 3 tables:
// pantry_items: what the user has in their pantry
// recipes: seeded recipes
// recipe_ingredients: the requirements of the recipes
// [Assignment 3.2 - Database requirement] I chose SQLite (SQLiteOpenHelper), stored locally on the device.
// it gives full CRUD (see the pantry crud section below) and the data is saved in a file (pantry.db)
// so it is still there after the app is closed and reopened.
// [Video concept: how the database works end-to-end]
// Activity -> DatabaseHelper method -> SQLite file -> Cursor -> model object (Ingred / Recipe) -> Adapter -> RecyclerView
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "pantry.db";
    private static final int DATABASE_VERSION = 1;

    // the pantry table the user owns
    private static final String TABLE_PANTRY_ITEMS = "pantry_items";
    private static final String COLUMN_PANTRY_ID = "_id";
    private static final String COLUMN_PANTRY_NAME = "name";
    private static final String COLUMN_PANTRY_QUANTITY = "quantity";
    private static final String COLUMN_PANTRY_UNIT = "unit";
    private static final String COLUMN_PANTRY_EXPIRY_DATE = "expiry_date";

    //the recipe table
    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_RECIPE_ID = "_id";
    private static final String COLUMN_RECIPE_NAME = "name";
    private static final String COLUMN_RECIPE_STEPS = "steps";

    //recipe ingredients requirements
    private static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    private static final String COLUMN_INGREDIENT_ID = "_id";
    private static final String COLUMN_INGREDIENT_RECIPE_ID = "recipe_id";
    private static final String COLUMN_INGREDIENT_NAME = "name";
    private static final String COLUMN_INGREDIENT_QUANTITY = "quantity";
    private static final String COLUMN_INGREDIENT_UNIT = "unit";

    //pantry table
    private static final String CREATE_TABLE_PANTRY_ITEMS =
            "CREATE TABLE " + TABLE_PANTRY_ITEMS + " (" +
                    COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                    COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                    COLUMN_PANTRY_EXPIRY_DATE + " TEXT)";

    //recipe table
    private static final String CREATE_TABLE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "+
                    COLUMN_RECIPE_NAME + " TEXT NOT NULL,  "+
                    COLUMN_RECIPE_STEPS + " TEXT NOT NULL);";

    //recipe ingredients table
    private static final String CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_INGREDIENT_RECIPE_ID + " INTEGER NOT NULL, " +
                    COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_INGREDIENT_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_INGREDIENT_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_INGREDIENT_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //runs only the first time the database file is created (not on every app start)
    //so the recipes get seeded once and then stay in the db
    // [Assignment 2.2 - recipe collection pre-loaded/seeded on first run] onCreate creates the 3 tables then calls seedRecipes
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY_ITEMS);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);
        seedRecipes(db);
    }

    //will recreate the database if the database version is updated
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }



    // [Assignment 3.2 - full CRUD on pantry items]
    // Create = insertPantryIngreds, Read = getAllPantryIngreds / getPantryIngred,
    // Update = updatePantryIngred, Delete = deletePantryIngred
    // SQLite writes to a file on the device so the data persists after closing and reopening the app
    // PANTRY CRUD METHODS


    //adds a new row to the pantry table
    public void insertPantryIngreds(Ingred ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = pantryToValues(ingredient);
        long id = db.insert(TABLE_PANTRY_ITEMS, null, values);
        db.close();
    }
    //updates a pantry row, using the id
    public void updatePantryIngred(Ingred ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = pantryToValues(ingredient);
        int rows = db.update(TABLE_PANTRY_ITEMS, values, COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())});
        db.close();
    }

    //removes a pantry ingredient
    public void deletePantryIngred(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY_ITEMS, COLUMN_PANTRY_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
    }

    //finds an ingredient in the pantry
    public Ingred getPantryIngred(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY_ITEMS, null, COLUMN_PANTRY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Ingred ingredient = null;
        if(cursor.moveToFirst()) {
            ingredient = cursorToIngred(cursor);
        }
        cursor.close();
        db.close();
        return ingredient;
    }

    //gets all the pantry ingredients in alphabetical order
    public List<Ingred> getAllPantryIngreds() {
        List<Ingred> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY_ITEMS, null, null, null,
                null, null, COLUMN_PANTRY_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursorToIngred(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    //converts an ingred object to a row of pantry_ingreds
    private ContentValues pantryToValues(Ingred ingredient) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, ingredient.getName());
        values.put(COLUMN_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, ingredient.getUnit());
        values.put(COLUMN_PANTRY_EXPIRY_DATE, ingredient.getExpiryDate());
        return values;
    }

    //converts row of pantry_ingreds cursor back to an object
    private Ingred cursorToIngred(Cursor cursor) {
        return new Ingred(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY_DATE)));
    }


    // these functions will read the recipes from the database

    //gets all the recipes in alphabetical order
    //used by getSuggestedRecipes and getCloseRecipes to load every recipe with its ingredients
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COLUMN_RECIPE_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String steps = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_STEPS));
                Recipe recipe = new Recipe(id, name, steps);
                recipe.setIngreds(getRecipeIngreds(db, id));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    //gets a recipe by its id
    public Recipe getRecipe(long recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COLUMN_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
            String steps = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_STEPS));
            recipe = new Recipe(recipeId, name, steps);
            recipe.setIngreds(getRecipeIngreds(db, recipeId));
        }
        cursor.close();
        db.close();
        return recipe;
    }

    //gets the ingredients for a recipe

    private List<RecipeIngred> getRecipeIngreds(SQLiteDatabase db, long recipeId) {
        List<RecipeIngred> ingredients = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COLUMN_INGREDIENT_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ingredients.add(new RecipeIngred(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_UNIT))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }



    // the recipe matching functions, will only return the recipes that can be made with only
    // the ingredients the user has

    // [Assignment 2.3 - STRICT-MATCHING RULE (core logic)]
    // a recipe is only suggested if EVERY ingredient it needs is in the pantry in at least the required quantity.
    // steps: 1) load all recipes  2) add up the pantry per ingredient (buildPantryTotals)
    // 3) keep a recipe only if canMakeRecipe() finds nothing missing
    // so a recipe that needs 5 ingredients when the user only has 4 is NOT returned.
    // [Video concept: how my strict-matching algorithm decides if a recipe is suggested]
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> recipes = getAllRecipes();
        Map<String, Double> pantryTotals = buildPantryTotals(getAllPantryIngreds());

        List<Recipe> suggestedRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMakeRecipe(recipe, pantryTotals)) {
                suggestedRecipes.add(recipe);
            }
        }
        return suggestedRecipes;
    }



    // this will get the recipes that the user is missing an ingred for
    // [Assignment 2.3 - optional "Almost There" stretch]
    // recipes missing exactly ONE ingredient. this is a separate method and is shown in a separate list
    // on screen so these never get mixed into the strict suggestions list.
    public List<Recipe> getCloseRecipes() {
        List<Recipe> recipes = getAllRecipes();
        Map<String, Double> pantryTotals = buildPantryTotals(getAllPantryIngreds());

        List<Recipe> closeRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            int missingCount = countMissingIngredients(recipe, pantryTotals);
            if (missingCount == 1) {
                closeRecipes.add(recipe);
            }
        }
        return closeRecipes;
    }

    //will count how many ingredients a recipe is missing
    private int countMissingIngredients(Recipe recipe, Map<String, Double> pantryTotals) {
        int missingCount = 0;
        for (RecipeIngred ingredient : recipe.getIngreds()) {
            if (isMissing(ingredient, pantryTotals)) {
                missingCount++;
            }
        }
        return missingCount;
    }

    //builds a map of the pantry ingredients
    // [Assignment 2.3 - robust matching] adds up the quantity of each ingredient (e.g. two rows of eggs)
    // after converting the units to a base unit (kg -> g, l -> ml) so different units can be compared
    private Map<String, Double> buildPantryTotals(List<Ingred> ingredients) {
        Map<String, Double> totals = new HashMap<>();
        for (Ingred ingredient : ingredients) {
            String key = pantryKey(ingredient.getName(), ingredient.getUnit());
            String unit = normalizeUnit(ingredient.getUnit());
            double baseQuantity = IngredNormalization.toBaseQuantity(ingredient.getQuantity(), unit);
            Double existing = totals.get(key);
            totals.put(key, existing == null ? baseQuantity : existing + baseQuantity);
        }
        return totals;
    }

    //builds a key for the pantry ingredients
    // key = normalised name + unit category, so "Tomatoes" and "tomato" give the same key
    // and an ingredient counted in pieces is never compared with one measured in grams/ml
    private String pantryKey(String name, String unitText) {
        String normalizedName = IngredNormalization.normalizeWords(name);
        String unit = normalizeUnit(unitText);
        String category = IngredNormalization.unitCategory(unit);
        return normalizedName + "::" + category;
    }

    //checks if a recipe can be made with the ingredients the user already has
    // [Assignment 2.3] the strict rule in code: as soon as ONE ingredient is missing it returns false,
    // only if the loop finishes with nothing missing is the recipe allowed to be suggested
    private boolean canMakeRecipe(Recipe recipe, Map<String, Double> pantryTotals) {
        for (RecipeIngred ingredient : recipe.getIngreds()) {
            if (isMissing(ingredient, pantryTotals)) {
                return false;
            }
        }
        return true;
    }


    //checks if there are ingredients missing
    // [Assignment 2.3] an ingredient counts as missing if it is not in the pantry at all
    // OR the pantry has less than the recipe needs ("at least the required quantity")
    private boolean isMissing(RecipeIngred ingredient, Map<String, Double> pantryTotals) {
        if (ingredient == null || pantryTotals == null) {
            return true;
        }
        String key = pantryKey(ingredient.getName(), ingredient.getUnit());
        String unit = normalizeUnit(ingredient.getUnit());
        double neededQuantity = IngredNormalization.toBaseQuantity(ingredient.getQuantity(), unit);
        Double available = pantryTotals.get(key);
        return available == null || available < neededQuantity;
    }

    // adding/seeding recipes

    // [Assignment 2.2 - recipe collection] 16 recipes are seeded here (the brief asks for at least 15-20)
    // each one has a name, simple preparation steps and a list of {ingredient, quantity, unit}
    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Omelette",
                "1. Beat the eggs in a bowl and season well with salt. Heat the oil and butter" +
                        " in a non stick frying pan over medium heat until the butter is melted\n" +
                        "2. Pour the eggs into the pan and spread it evenly over the pan. Let the" +
                        " mixture cook for about 20 seconds then scrape a line through the middle" +
                        " with a spatula.",
                new Object[][]{
                        {"egg", 2, "pcs"},
                        {"salt", 1, "tsp"},
                        {"butter", 1, "tsp"}
                });
        insertRecipe(db, "Homemade Peanut Butter",
                "1. Heat oven to 200C\n" +
                        "2. Put peanuts into large baking tray, place into oven and roast for 10mins" +
                        " Remove when golden brown and let to cool\n" +
                        "3. Add peanuts to food processor and add salt, blend for 4-5mins" +
                        " add oil to loosen consistency",
                new Object[][]{
                        {"peanuts", 400, "g"},
                        {"salt", 2, "tsp"},
                        {"oil", 15, "ml"}
                });
        insertRecipe(db, "Pancakes",
                "1. Add flour in a large bowl and make a well in the center. Crack the eggs" +
                        " in the center, add half of the milk, and whisk/beat until smooth. " +
                        " add the rest of the milk until the mixture is smooth. \n" +
                        "2. Heat a non stick frying pan over medium heat and add a drop of oil" +
                        " covering the whole pan. Add a thin layer of the batter onto the pan" +
                        " Leave the batter for 30 seconds until it starts to change color around " +
                        " the edges\n" +
                        "3. Gently lift up the edges of the pancake using a flat knife, gently " +
                        " moving to the center of the pancake. In one quick movement flip the " +
                        " pancake over and leave for around 30 seconds and remove once done",
                new Object[][]{
                        {"egg", 2, "pcs"},
                        {"flour", 100, "g"},
                        {"milk", 300, "ml"},
                        {"oil", 2, "tsp"}
                });
        insertRecipe(db, "Hash Browns",
                "1. Cook the potatoes in a pan of boiling water for 10 mins then drain and" +
                        " set aside to cool. \n" +
                        "2. Grate the potatoes  into a bowl and removing the skins. Season well" +
                        " with salt and pour over half the butter. Mix well and divide into 8" +
                        " and shape it into a patty. \n" +
                        "3. Heat the oil and the remaining butter in a frying pan until its sizzling" +
                        " and gently fry the potatoes. Fry for 4-5 mins on each side until its" +
                        " crispy and golden. ",
                new Object[][]{
                        {"potato", 3, "pcs"},
                        {"butter", 50, "g"},
                        {"oil", 10, "tsp"},
                        {"salt", 1, "tsp"}
                });
        insertRecipe(db, "Homemade Ice cream",
                "1. Add condensed milk, cream and vanilla into a large bowl. Beat with an " +
                        " electric whisk until thick and stiff.\n" +
                        "2. Scrape it off the bowl and put in the freezer and cover with cling" +
                        " wrap and freeze until solid. \n",
                new Object[][]{
                        {"condensed milk", 200, "g"},
                        {"cream", 600, "ml"},
                        {"vanilla extract", 1, "tsp"}
                });
        insertRecipe(db, "Fresh Chips",
                "1. Cut up the potatoes into chunky sized chips.\n" +
                        "2. Put the chips in a saucepan. Pour enough oil to cover them by at least" +
                        " 2cm. Put the pan on high heat and boil the oil. Stirring the chips" +
                        " occasionally. \n" +
                        "3. After 20 mins, the chips should look like they are frying, keep stirring" +
                        " until its golden brown. Remove the chips from the pan and let them cool",
                new Object[][]{
                        {"potato", 5, "pcs"},
                        {"salt", 5, "tsp"},
                        {"oil", 300, "ml"}
                });
        insertRecipe(db, "Crepes",
                "1. Add the flour to a large bowl, crack the eggs and add half of the milk" +
                        " and whisk until smooth and thick. Add remaining milk and whisk more. set" +
                        " aside for 30 mins. \n" +
                        "2. Heat a non stick frying pan over medium heat and add a drop of oil to " +
                        " the pan. When pan is hot, add batter to cover the surface of pan. \n" +
                        "3. Gently lift up the edges of the crepe using a flat knife, gently " +
                        " moving to the center of the crepe. In one quick movement flip the " +
                        " crepe over and leave for around 30 seconds and remove once done \n" +
                        "4. Whip some cream and add to the crepe. ",
                new Object[][]{
                        {"flour", 175, "g"},
                        {"egg", 3, "pcs"},
                        {"milk", 450, "ml"},
                        {"oil", 4, "tsp"},
                        {"cream", 150, "ml"}
                });
        insertRecipe(db, "Shortbread",
                "1. Heat the oven to 170C, add the flour, butter and egg into a mixing bowl, " +
                        " combine the ingredients until the mixture feels like crumbly, then " +
                        " squeeze until it comes together. " +
                        "2. Add some flour to a counter, and use a rolling pin to roll the dough" +
                        " to about half a cm thick. Cut the dough into thin rectangles and place " +
                        " onto a baking tray. Use a fork to create holes. Sprinkle with sugar. \n" +
                        "3. Chill the dough in the fridge for 20mins, and bake for 15-20mins. " +
                        " Remove the shortbread rectangles from the oven and leave to cool. \n",
                new Object[][]{
                        {"flour", 150, "g"},
                        {"butter", 100, "g"},
                        {"sugar", 50, "g"}
                });
        insertRecipe(db, "Peanut Butter Cookies",
                "1. Heat oven to 180C, and line 2 large baking trays with baking paper. " +
                        " Add the peanut butter and sugar into a bowl. Add the salt and mix well" +
                        " with a spoon. Add the egg and mix again until it turns into a dough. \n" +
                        "2. Break off small pieces of dough and place it on the baking tray with" +
                        " a decent spacing between them. Press them down with a fork and spread " +
                        " them. \n" +
                        "3. Bake for 12 mins until golden around the edges. Cool on the trays for 10" +
                        "mins and transfer to a wire rack and cool. ",
                new Object[][]{
                        {"peanut butter", 200, "g"},
                        {"sugar", 175, "g"},
                        {"salt", 1, "tsp"},
                        {"egg", 1, "pcs"}
                });
        insertRecipe(db, "Homemade Tortillas",
                "1. Mix together the flour and salt. Mix the oil and 150ml of water " +
                        " into the dry ingredients to form a soft dough. Knead for 1-2 mins\n" +
                        "2. Split the dough into 25g pieces and roll into thin circular shapes. " +
                        " Heat a dry pan until very hot and fry each tortilla for 20 secs on " +
                        " each side. \n" +
                        "3. Remove any excess flour if needed. ",
                new Object[][]{
                        {"self raising flour", 225, "g"},
                        {"oil", 1, "tsp"}
                });
        insertRecipe(db, "Basil Pesto",
                "1. Wash and remove stems from the basil leaves. \n" +
                        "2. Combine basil, salt and oil in a blender. \n" +
                        "3. Pulse the blender until the basil is finely chopped. \n" +
                        "4. either freeze it in an airtight container, or store in a jar and keep " +
                        " it in the fridge",
                new Object[][]{
                        {"basil", 60, "g"},
                        {"oil", 11, "tsp"},
                        {"salt", 1, "tsp"}
                });
        insertRecipe(db, "Scones",
                "1. Freeze butter for 15 minutes if not frozen already. Grate the butter in " +
                        " a bowl with the flour.  \n" +
                        "2. Rub the butter into the flour with your finger until its like " +
                        " breadcrumbs. Try to aerate the mixture \n" +
                        "3. Pour the milk into the flour and combine with a spatula until it is" +
                        " soft. Do not overmix. \n" +
                        "4. Turn the dough into a lightly floured surface. Gather the dough gently" +
                        " into a ball. \n" +
                        "5. Pat the dough out and make it 2cm thick. Cut with a cookie cutter. " +
                        " Gently form the dough back together and cut again until the dough is used" +
                        " up.  \n" +
                        "6. Place the cut dough onto a baking sheet with parchment paper. \n" +
                        "7. Bake for 12-15 mins until risen and golden brown in an oven on " +
                        " 200C heat. ",
                new Object[][]{
                        {"self raising flour", 375, "g"},
                        {"butter", 75, "g"},
                        {"milk", 80, "ml"}
                });
        insertRecipe(db, "Flatbread (Roti)",
                "1. Combine the self raising flour, yogurt and salt together. \n" +
                        "2. Mix to form a soft dough. Cut or break into small pieces. \n" +
                        "3. Dust a little flour on a board and roll out the dough into about " +
                        " 3 cm thick. \n" +
                        "4. Heat a non stick pan on low-medium heat and cook for 30 secs on each" +
                        " side. \n" +
                        "5. Keep the cooked roti covered with a kitchen towel.",
                new Object[][]{
                        {"self raising flour", 250, "g"},
                        {"yogurt", 250, "ml"},
                        {"salt", 1, "tsp"}
                });
        insertRecipe(db, "Banana Brownies",
                "1. Preheat oven to 175C. Grease a square baking pan and line with with " +
                        " parchment paper. \n" +
                        "2. Mash the bananas in a large bowl with a fork until smooth. \n" +
                        "3. Add the butter until well combined with the banana. \n" +
                        "4. Sift the cacao powder to avoid lumps and mix until batter is smooth " +
                        " and all ingredients are fully mixed. And add the vanilla extract. \n" +
                        "5. Pour the batter into the baking pan and spread it evenly." +
                        "6. Bake in the oven for around 20-25 mins. Poke with a toothpick in the " +
                        " center and check if it comes out clean with moist crumbs. \n" +
                        "7. Allow to cool for 10 mins and cut into squares. ",
                new Object[][]{
                        {"banana", 3, "pcs"},
                        {"butter", 125, "g"},
                        {"cacao", 200, "g"},
                        {"vanilla extract", 1, "tsp"}
                });
        insertRecipe(db, "Strawberry Mousse",
                "1. Wash the strawberries and add to a food processor and blend until its " +
                        " a smooth puree.\n" +
                        "2. In a small pan, over medium heat, combine the strawberry puree and " +
                        " sugar and simmer for 5-7 mins, stirring occasionally. Let it cool after \n" +
                        "3. In a large bowl, whip the cream and vanilla extract until  the stiff " +
                        " peaks form. Don't overheat. \n" +
                        "4. Gently fold the strawberry mixture into the whipped cream in small " +
                        "batches until combined well. \n" +
                        "5. Divide the mousse evenly into bowls. Refrigerate for 3-4 hours until set. ",
                new Object[][]{
                        {"strawberries", 15, "pcs"},
                        {"sugar", 100, "g"},
                        {"cream", 240, "ml"},
                        {"vanilla extract", 1,"tsp"}
                });
        insertRecipe(db, "Chocolate Fudge",
                "1. Line a square baking pan with parchment paper and grease with butter. \n" +
                        "2. Chop the chocolate, place in a medium pan, add the condensed milk " +
                        " and butter. \n" +
                        "3. Place the pan over low heat, and cook, and stir until completely " +
                        " melted and mixed, then cook for 2-3mins until thickens a bit more. \n" +
                        "4. Pour the mixture into the pan and spread evenly. \n" +
                        "5. Refrigerate for at least 3-4 hours. Release from the pan and cut into squares. ",
                new Object[][]{
                        {"chocolate", 300, "g"},
                        {"condensed milk", 400, "g"},
                        {"butter", 30, "g"}
                });


    }

    //inserts a recipe and its ingredient list in one call.

    private void insertRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredientRows) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COLUMN_RECIPE_NAME, name);
        recipeValues.put(COLUMN_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (Object[] row : ingredientRows) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(COLUMN_INGREDIENT_RECIPE_ID, recipeId);
            ingredientValues.put(COLUMN_INGREDIENT_NAME, (String) row[0]);
            ingredientValues.put(COLUMN_INGREDIENT_QUANTITY, ((Number) row[1]).doubleValue());
            ingredientValues.put(COLUMN_INGREDIENT_UNIT, (String) row[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
        }
    }


}
