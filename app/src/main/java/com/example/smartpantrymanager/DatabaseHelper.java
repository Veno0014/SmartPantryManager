package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database details
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 5;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_METHOD = "method";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RECIPE_INGREDIENT_ID = "id";
    public static final String COL_RECIPE_LINK_ID = "recipe_id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_REQUIRED_QUANTITY = "required_quantity";
    public static final String COL_REQUIRED_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createPantryTable(db);

        createRecipeTables(db);

        seedRecipes(db);

        seedPantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Add recipe tables
        if(oldVersion < 2) {
            createRecipeTables(db);
        }

        // Refresh recipe collection
        if(oldVersion < 4) {

            db.delete(TABLE_RECIPE_INGREDIENTS, null, null);

            db.delete(TABLE_RECIPES, null, null);

            seedRecipes(db);
        }

        // Add starting pantry stock
        if(oldVersion < 5) {

            seedPantry(db);
        }
    }

    // Create Pantry Table
    private void createPantryTable(SQLiteDatabase db) {

        String createPantryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY_DATE + " TEXT)";

        db.execSQL(createPantryTable);
    }

    // Create Recipe Tables
    private void createRecipeTables(SQLiteDatabase db) {

        String createRecipeTable = "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_METHOD + " TEXT NOT NULL)";

        db.execSQL(createRecipeTable);

        String createRecipeIngredientsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RECIPE_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_LINK_ID + " INTEGER NOT NULL, " +
                COL_INGREDIENT_NAME + " TEXT NOT NULL, " +
                COL_REQUIRED_QUANTITY + " REAL NOT NULL, " +
                COL_REQUIRED_UNIT + " TEXT NOT NULL)";

        db.execSQL(createRecipeIngredientsTable);
    }

    // Add Ingredient
    public long addIngredient(String name, double quantity, String unit, String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);

        if(expiryDate.isEmpty()) {

            values.putNull(COL_EXPIRY_DATE);

        } else {

            values.put(COL_EXPIRY_DATE, expiryDate);
        }

        return db.insert(TABLE_PANTRY, null, values);
    }

    // Read Ingredients
    public Cursor getAllIngredients() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " ASC"
        );
    }

    // Update Ingredient
    public int updateIngredient(int id, String name, double quantity, String unit, String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);

        if(expiryDate.isEmpty()) {

            values.putNull(COL_EXPIRY_DATE);

        } else {

            values.put(COL_EXPIRY_DATE, expiryDate);
        }

        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Delete Ingredient
    public int deleteIngredient(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Preload Pantry Stock
    private void seedPantry(SQLiteDatabase db) {

        addPantryStockIfMissing(db, "leg mutton", 1.5, "kg");
        addPantryStockIfMissing(db, "tomato", 12, "pieces");
        addPantryStockIfMissing(db, "pizza base", 10, "pieces");
        addPantryStockIfMissing(db, "onion", 8, "pieces");
        addPantryStockIfMissing(db, "mushroom", 500, "g");
        addPantryStockIfMissing(db, "bell pepper", 6, "pieces");
        addPantryStockIfMissing(db, "cheese", 1, "kg");
        addPantryStockIfMissing(db, "potato", 10, "pieces");
        addPantryStockIfMissing(db, "rice", 2, "kg");
        addPantryStockIfMissing(db, "breyani spice", 250, "g");
        addPantryStockIfMissing(db, "curry powder", 250, "g");
        addPantryStockIfMissing(db, "egg", 12, "pieces");
        addPantryStockIfMissing(db, "butter", 500, "g");
    }

    // Add Pantry Stock Only If Missing
    private void addPantryStockIfMissing(SQLiteDatabase db, String name, double quantity, String unit) {

        Cursor cursor = db.query(
                TABLE_PANTRY,
                new String[]{COL_ID},
                "LOWER(" + COL_NAME + ") = LOWER(?)",
                new String[]{name},
                null,
                null,
                null
        );

        boolean alreadyExists = cursor.moveToFirst();

        cursor.close();

        if(!alreadyExists) {

            ContentValues values = new ContentValues();

            values.put(COL_NAME, name);
            values.put(COL_QUANTITY, quantity);
            values.put(COL_UNIT, unit);
            values.putNull(COL_EXPIRY_DATE);

            db.insert(TABLE_PANTRY, null, values);
        }
    }

    // Read Recipes
    public Cursor getAllRecipes() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COL_RECIPE_NAME + " ASC"
        );
    }

    // Read Recipe Ingredients
    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COL_RECIPE_LINK_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        );
    }

    // Get Recipe Ingredient Names
    public ArrayList<String> getRecipeIngredientNames() {

        ArrayList<String> ingredientNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + COL_INGREDIENT_NAME +
                        " FROM " + TABLE_RECIPE_INGREDIENTS +
                        " ORDER BY " + COL_INGREDIENT_NAME + " ASC",
                null
        );

        int nameIndex = cursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME);

        while(cursor.moveToNext()) {

            ingredientNames.add(cursor.getString(nameIndex));
        }

        cursor.close();

        return ingredientNames;
    }

    // Add Recipe
    private long addRecipe(SQLiteDatabase db, String name, String method) {

        ContentValues values = new ContentValues();

        values.put(COL_RECIPE_NAME, name);
        values.put(COL_METHOD, method);

        return db.insert(TABLE_RECIPES, null, values);
    }

    // Add Recipe Ingredient
    private void addRecipeIngredient(SQLiteDatabase db, long recipeId, String name, double quantity, String unit) {

        ContentValues values = new ContentValues();

        values.put(COL_RECIPE_LINK_ID, recipeId);
        values.put(COL_INGREDIENT_NAME, name);
        values.put(COL_REQUIRED_QUANTITY, quantity);
        values.put(COL_REQUIRED_UNIT, unit);

        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }

    // Preload Recipes
    private void seedRecipes(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECIPES,
                null
        );

        if(cursor.moveToFirst() && cursor.getInt(0) > 0) {

            cursor.close();

            return;
        }

        cursor.close();

        // 1. Runny Eggs
        long recipe1 = addRecipe(
                db,
                "Runny Eggs",
                "Heat the butter in a pan. Crack the eggs into the pan and cook until the egg whites are firm while the yolks remain soft and runny."
        );

        addRecipeIngredient(db, recipe1, "egg", 2, "pieces");
        addRecipeIngredient(db, recipe1, "butter", 10, "g");

        // 2. Bunny Chow
        long recipe2 = addRecipe(
                db,
                "Bunny Chow",
                "Cook the mutton with onion, tomato, potato and curry powder until tender. Hollow out the bread loaf and fill it with the prepared curry."
        );

        addRecipeIngredient(db, recipe2, "bread loaf", 1, "pieces");
        addRecipeIngredient(db, recipe2, "mutton", 500, "g");
        addRecipeIngredient(db, recipe2, "potato", 2, "pieces");
        addRecipeIngredient(db, recipe2, "onion", 1, "pieces");
        addRecipeIngredient(db, recipe2, "tomato", 2, "pieces");
        addRecipeIngredient(db, recipe2, "curry powder", 20, "g");

        // 3. Tikka Chicken
        long recipe3 = addRecipe(
                db,
                "Tikka Chicken",
                "Mix the chicken with yoghurt and tikka spice. Allow the chicken to marinate and then cook until fully cooked and golden."
        );

        addRecipeIngredient(db, recipe3, "chicken", 500, "g");
        addRecipeIngredient(db, recipe3, "yoghurt", 150, "ml");
        addRecipeIngredient(db, recipe3, "tikka spice", 20, "g");

        // 4. Phutu and Mutton Curry
        long recipe4 = addRecipe(
                db,
                "Phutu and Mutton Curry",
                "Prepare the phutu using maize meal and water. Cook the mutton with onion, tomato and curry powder until tender. Serve the curry with the phutu."
        );

        addRecipeIngredient(db, recipe4, "maize meal", 250, "g");
        addRecipeIngredient(db, recipe4, "mutton", 500, "g");
        addRecipeIngredient(db, recipe4, "onion", 1, "pieces");
        addRecipeIngredient(db, recipe4, "tomato", 2, "pieces");
        addRecipeIngredient(db, recipe4, "curry powder", 20, "g");

        // 5. Phutu and Chicken Curry
        long recipe5 = addRecipe(
                db,
                "Phutu and Chicken Curry",
                "Prepare the phutu using maize meal and water. Cook the chicken with onion, tomato and curry powder until tender. Serve the chicken curry with the phutu."
        );

        addRecipeIngredient(db, recipe5, "maize meal", 250, "g");
        addRecipeIngredient(db, recipe5, "chicken", 500, "g");
        addRecipeIngredient(db, recipe5, "onion", 1, "pieces");
        addRecipeIngredient(db, recipe5, "tomato", 2, "pieces");
        addRecipeIngredient(db, recipe5, "curry powder", 20, "g");

        // 6. French Toast
        long recipe6 = addRecipe(
                db,
                "French Toast",
                "Beat the egg and milk together. Dip the bread into the mixture and fry in butter until golden brown on both sides."
        );

        addRecipeIngredient(db, recipe6, "bread", 2, "slices");
        addRecipeIngredient(db, recipe6, "egg", 1, "pieces");
        addRecipeIngredient(db, recipe6, "milk", 100, "ml");
        addRecipeIngredient(db, recipe6, "butter", 10, "g");

        // 7. Roasted Chicken
        long recipe7 = addRecipe(
                db,
                "Roasted Chicken",
                "Season the chicken with salt and oil. Place it in the oven and roast until golden brown and completely cooked."
        );

        addRecipeIngredient(db, recipe7, "chicken", 1, "pieces");
        addRecipeIngredient(db, recipe7, "oil", 30, "ml");
        addRecipeIngredient(db, recipe7, "salt", 5, "g");

        // 8. Chicken Burger and Chips
        long recipe8 = addRecipe(
                db,
                "Chicken Burger and Chips",
                "Cook the chicken patty. Place it inside the burger bun with lettuce and tomato. Cut the potatoes into chips and fry until golden."
        );

        addRecipeIngredient(db, recipe8, "chicken patty", 1, "pieces");
        addRecipeIngredient(db, recipe8, "burger bun", 1, "pieces");
        addRecipeIngredient(db, recipe8, "potato", 2, "pieces");
        addRecipeIngredient(db, recipe8, "lettuce", 2, "leaves");
        addRecipeIngredient(db, recipe8, "tomato", 1, "pieces");
        addRecipeIngredient(db, recipe8, "oil", 250, "ml");

        // 9. Mutton Burger and Chips
        long recipe9 = addRecipe(
                db,
                "Mutton Burger and Chips",
                "Cook the mutton patty. Place it inside the burger bun with lettuce and tomato. Cut the potatoes into chips and fry until golden."
        );

        addRecipeIngredient(db, recipe9, "mutton patty", 1, "pieces");
        addRecipeIngredient(db, recipe9, "burger bun", 1, "pieces");
        addRecipeIngredient(db, recipe9, "potato", 2, "pieces");
        addRecipeIngredient(db, recipe9, "lettuce", 2, "leaves");
        addRecipeIngredient(db, recipe9, "tomato", 1, "pieces");
        addRecipeIngredient(db, recipe9, "oil", 250, "ml");

        // 10. Mushroom and Veg Sausage Omelette
        long recipe10 = addRecipe(
                db,
                "Mushroom and Veg Sausage Omelette",
                "Slice the mushrooms and vegetarian sausage and lightly fry them. Beat the eggs, pour them into the pan, add the filling and fold the omelette."
        );

        addRecipeIngredient(db, recipe10, "egg", 3, "pieces");
        addRecipeIngredient(db, recipe10, "mushroom", 100, "g");
        addRecipeIngredient(db, recipe10, "veg sausage", 2, "pieces");
        addRecipeIngredient(db, recipe10, "butter", 10, "g");

        // 11. Egg Roll
        long recipe11 = addRecipe(
                db,
                "Egg Roll",
                "Beat and cook the eggs in a pan. Place the cooked eggs inside the wrap and roll it tightly before serving."
        );

        addRecipeIngredient(db, recipe11, "egg", 2, "pieces");
        addRecipeIngredient(db, recipe11, "wrap", 1, "pieces");
        addRecipeIngredient(db, recipe11, "butter", 10, "g");

        // 12. Chicken Breyani
        long recipe12 = addRecipe(
                db,
                "Chicken Breyani",
                "Cook the chicken with onion and breyani spice. Add the rice and potatoes and cook until the rice is tender and the chicken is fully cooked."
        );

        addRecipeIngredient(db, recipe12, "chicken", 500, "g");
        addRecipeIngredient(db, recipe12, "rice", 300, "g");
        addRecipeIngredient(db, recipe12, "potato", 2, "pieces");
        addRecipeIngredient(db, recipe12, "onion", 1, "pieces");
        addRecipeIngredient(db, recipe12, "breyani spice", 20, "g");

        // 13. Mutton Breyani
        long recipe13 = addRecipe(
                db,
                "Mutton Breyani",
                "Cook the mutton with onion and breyani spice until tender. Add the rice and potatoes and cook until the rice is ready."
        );

        addRecipeIngredient(db, recipe13, "mutton", 500, "g");
        addRecipeIngredient(db, recipe13, "rice", 300, "g");
        addRecipeIngredient(db, recipe13, "potato", 2, "pieces");
        addRecipeIngredient(db, recipe13, "onion", 1, "pieces");
        addRecipeIngredient(db, recipe13, "breyani spice", 20, "g");

        // 14. Wagyu and Chips
        long recipe14 = addRecipe(
                db,
                "Wagyu and Chips",
                "Season and cook the Wagyu steak to the desired level. Cut the potatoes into chips and fry until golden and crispy."
        );

        addRecipeIngredient(db, recipe14, "wagyu steak", 250, "g");
        addRecipeIngredient(db, recipe14, "potato", 2, "pieces");
        addRecipeIngredient(db, recipe14, "oil", 250, "ml");
        addRecipeIngredient(db, recipe14, "salt", 5, "g");

        // 15. Veg Todays Pizza
        long recipe15 = addRecipe(
                db,
                "Veg Todays Pizza",
                "Place the pizza base on a baking tray. Add the tomato, onion, mushroom and bell pepper. Sprinkle the cheese over the vegetables and bake until the cheese has melted and the pizza is golden."
        );

        addRecipeIngredient(db, recipe15, "pizza base", 1, "pieces");
        addRecipeIngredient(db, recipe15, "tomato", 1, "pieces");
        addRecipeIngredient(db, recipe15, "onion", 1, "pieces");
        addRecipeIngredient(db, recipe15, "mushroom", 100, "g");
        addRecipeIngredient(db, recipe15, "bell pepper", 1, "pieces");
        addRecipeIngredient(db, recipe15, "cheese", 100, "g");
    }

    // Get Suggested Recipes
    public ArrayList<Recipes> getSuggestedRecipes() {

        ArrayList<Recipes> suggestedRecipes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor recipeCursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COL_RECIPE_NAME + " ASC"
        );

        int recipeIdIndex = recipeCursor.getColumnIndexOrThrow(COL_RECIPE_ID);
        int recipeNameIndex = recipeCursor.getColumnIndexOrThrow(COL_RECIPE_NAME);
        int methodIndex = recipeCursor.getColumnIndexOrThrow(COL_METHOD);

        while(recipeCursor.moveToNext()) {

            int recipeId = recipeCursor.getInt(recipeIdIndex);
            String recipeName = recipeCursor.getString(recipeNameIndex);
            String method = recipeCursor.getString(methodIndex);

            boolean canMakeRecipe = true;

            Cursor ingredientCursor = db.query(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    COL_RECIPE_LINK_ID + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    null,
                    null,
                    null
            );

            int ingredientNameIndex = ingredientCursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME);
            int quantityIndex = ingredientCursor.getColumnIndexOrThrow(COL_REQUIRED_QUANTITY);
            int unitIndex = ingredientCursor.getColumnIndexOrThrow(COL_REQUIRED_UNIT);

            while(ingredientCursor.moveToNext()) {

                String requiredName = ingredientCursor.getString(ingredientNameIndex);
                double requiredQuantity = ingredientCursor.getDouble(quantityIndex);
                String requiredUnit = ingredientCursor.getString(unitIndex);

                if(!hasEnoughIngredient(db, requiredName, requiredQuantity, requiredUnit)) {

                    canMakeRecipe = false;

                    break;
                }
            }

            ingredientCursor.close();

            if(canMakeRecipe) {

                suggestedRecipes.add(
                        new Recipes(
                                recipeId,
                                recipeName,
                                method
                        )
                );
            }
        }

        recipeCursor.close();

        return suggestedRecipes;
    }

    // Check Pantry Ingredient
    private boolean hasEnoughIngredient(SQLiteDatabase db, String requiredName, double requiredQuantity, String requiredUnit) {

        Cursor pantryCursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                null
        );

        int pantryNameIndex = pantryCursor.getColumnIndexOrThrow(COL_NAME);
        int pantryQuantityIndex = pantryCursor.getColumnIndexOrThrow(COL_QUANTITY);
        int pantryUnitIndex = pantryCursor.getColumnIndexOrThrow(COL_UNIT);

        double totalQuantity = 0;

        String requiredIngredient = normalizeIngredientName(requiredName);

        String requiredBaseUnit = getBaseUnit(requiredUnit);

        while(pantryCursor.moveToNext()) {

            String pantryName = pantryCursor.getString(pantryNameIndex);
            double pantryQuantity = pantryCursor.getDouble(pantryQuantityIndex);
            String pantryUnit = pantryCursor.getString(pantryUnitIndex);

            String pantryIngredient = normalizeIngredientName(pantryName);

            String pantryBaseUnit = getBaseUnit(pantryUnit);

            if(requiredIngredient.equals(pantryIngredient) && requiredBaseUnit.equals(pantryBaseUnit)) {

                totalQuantity += convertToBaseQuantity(
                        pantryQuantity,
                        pantryUnit
                );
            }
        }

        pantryCursor.close();

        double requiredBaseQuantity = convertToBaseQuantity(
                requiredQuantity,
                requiredUnit
        );

        return totalQuantity >= requiredBaseQuantity;
    }

    // Normalize Ingredient Name
    private String normalizeIngredientName(String name) {

        String value = name.toLowerCase(Locale.ROOT).trim();

        // Leg mutton is still treated as mutton
        if(value.equals("leg mutton")) {
            return "mutton";
        }

        if(value.equals("tomatoes")) {
            return "tomato";
        }

        if(value.equals("potatoes")) {
            return "potato";
        }

        if(value.equals("loaves")) {
            return "loaf";
        }

        if(value.equals("leaves")) {
            return "leaf";
        }

        if(value.endsWith("ies") && value.length() > 3) {

            value = value.substring(
                    0,
                    value.length() - 3
            ) + "y";

        } else if(value.endsWith("s") && !value.endsWith("ss") && value.length() > 3) {

            value = value.substring(
                    0,
                    value.length() - 1
            );
        }

        return value;
    }

    // Normalize Units
    private String getBaseUnit(String unit) {

        String value = unit.toLowerCase(Locale.ROOT).trim();

        if(value.equals("g") ||
                value.equals("gram") ||
                value.equals("grams") ||
                value.equals("kg") ||
                value.equals("kilogram") ||
                value.equals("kilograms")) {

            return "g";
        }

        if(value.equals("ml") ||
                value.equals("millilitre") ||
                value.equals("millilitres") ||
                value.equals("milliliter") ||
                value.equals("milliliters") ||
                value.equals("l") ||
                value.equals("litre") ||
                value.equals("litres") ||
                value.equals("liter") ||
                value.equals("liters")) {

            return "ml";
        }

        if(value.equals("piece") ||
                value.equals("pieces") ||
                value.equals("pc") ||
                value.equals("pcs") ||
                value.equals("unit") ||
                value.equals("units")) {

            return "piece";
        }

        if(value.equals("slice") ||
                value.equals("slices")) {

            return "slice";
        }

        if(value.equals("leaf") ||
                value.equals("leaves")) {

            return "leaf";
        }

        return value;
    }

    // Convert Units
    private double convertToBaseQuantity(double quantity, String unit) {

        String value = unit.toLowerCase(Locale.ROOT).trim();

        // Kilograms to grams
        if(value.equals("kg") ||
                value.equals("kilogram") ||
                value.equals("kilograms")) {

            return quantity * 1000;
        }

        // Litres to millilitres
        if(value.equals("l") ||
                value.equals("litre") ||
                value.equals("litres") ||
                value.equals("liter") ||
                value.equals("liters")) {

            return quantity * 1000;
        }

        return quantity;
    }

    // Add Custom Recipe
    public long addCustomRecipe(String name, String method, ArrayList<String> ingredientNames, ArrayList<Double> quantities, ArrayList<String> units) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        long recipeId = -1;

        try {

            recipeId = addRecipe(db, name, method);

            if(recipeId == -1) {
                return -1;
            }

            for(int i = 0; i < ingredientNames.size(); i++) {

                addRecipeIngredient(
                        db,
                        recipeId,
                        ingredientNames.get(i),
                        quantities.get(i),
                        units.get(i)
                );
            }

            db.setTransactionSuccessful();

        } finally {

            db.endTransaction();
        }

        return recipeId;
    }
}