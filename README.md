# PantryNerd
Is an Android app written in Java that helps reduce food waste. You tell it what
ingredients you have at home, and it suggests only the recipes you can cook
right now, with no shopping trip needed.

Built for Mobile App Development 700 (Practical Assignment) by KIDAREN RAMSAMY,
student number 402309901.

## What it does
- Pantry management: add, edit and delete ingredients (name, quantity, unit,
  optional expiry date).
- Pantry list screen using a RecyclerView with a custom adapter.
- 16 pre-loaded recipes, seeded into the database on first run.
- Suggested Recipes screen using a **strict-matching rule**: a recipe is shown
  only if every ingredient it needs is in the pantry in at least the required
  quantity. A recipe missing even one ingredient is never in the main list.
- Matching copes with simple real-world messiness: singular/plural names
  ("tomato" vs "tomatoes"), capitalisation, and unit differences (g vs kg,
  ml vs l, tsp vs tbsp).
- An optional "Almost There" list of recipes missing exactly one ingredient,
  kept clearly separate from the strict suggestions.
- Recipe detail screen with the full ingredient list and method.
- Settings screen (expiry-alert toggle and units preference, saved with
  SharedPreferences).
- Messages for empty states, and input validation on the add/edit form.

## Database: SQLite
I used SQLite through `SQLiteOpenHelper` (`DatabaseHelper.java`), with three
tables: `pantry_items`, `recipes` and `recipe_ingredients`.

Why SQLite:
“I chose SQLite because my data is small and structured, with recipes linked to their ingredients,
and it runs on the device, so the app works offline with no server.It’s built into Android and covered in our module, 
so I could explain it end to end. Firebase and PostgreSQL would have added accounts,a network or a backend I didn’t need. 
The downside is no sync between devices.

Data is stored in a local file (`pantry.db`), so it persists after the app is
closed and reopened.

## Setup and run
1. Install a recent version of Android Studio (the project uses Android
   Gradle Plugin 9.3.3, Java 11 and compileSdk 37).
2. Clone the repository:
   `git clone https://github.com/Agentkid007/Moblieapp.git`
3. Open the project folder in Android Studio and let Gradle sync finish.
4. Create or choose an emulator running Android 9 (API 28) or higher, or
   connect a physical device with USB debugging on.
5. Press Run. No API keys or other configuration are needed.

## Project structure
- `db/DatabaseHelper.java`: SQLite setup, CRUD, recipe seeding, strict matching
- `util/IngredNormalization.java`: name and unit normalisation
- `model/`: Ingred, Recipe, RecipeIngred
- `adapter/`: PantryAdapter, RecipeAdapter
- Activities: MainActivity (pantry list), AddEditIngredActivity,
  SuggestedRecipesActivity, RecipeDetailActivity, SettingsActivity

## Out of scope
As the brief requires, the app uses no Google Maps, mapping SDK or
location/GPS features.
