package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database connections
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

        samplerecipes(db);

        samplepantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Adds recipes to tables
        if(oldVersion < 2) {
            createRecipeTables(db);
        }

        // Refreshes recipes
        if(oldVersion < 4) {

            db.delete(TABLE_RECIPE_INGREDIENTS, null, null);

            db.delete(TABLE_RECIPES, null, null);

            samplerecipes(db);
        }

        // Add pantry stock
        if(oldVersion < 5) {

            samplepantry(db);
        }
    }

    // Creating pantry table
    private void createPantryTable(SQLiteDatabase db) {

        String createPantryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY_DATE + " TEXT)";

        db.execSQL(createPantryTable);
    }

    // Creating recipe tables
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

    // Adding ingredient
    public long addingredient(String name, double quantity, String unit, String expiryDate) {

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

    // Reads through ingredients
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

    // Updating ingredients
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

    // Deleting ingredients
    public int deleteIngredient(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Adding pantry stock into tab
    private void samplepantry(SQLiteDatabase db) {

        add_missing_stock(db, "leg mutton", 1.5, "kg", "30/08/27");
        add_missing_stock(db, "tomato", 12, "pieces", "30/08/27");
        add_missing_stock(db, "pizza base", 10, "pieces", "30/08/27");
        add_missing_stock(db, "onion", 8, "pieces", "30/08/27");
        add_missing_stock(db, "mushroom", 500, "g", "30/08/27");
        add_missing_stock(db, "bell pepper", 6, "pieces", "30/08/27");
        add_missing_stock(db, "cheese", 1, "kg", "30/08/27");
        add_missing_stock(db, "potato", 10, "pieces", "30/08/27");
        add_missing_stock(db, "rice", 2, "kg", "");
        add_missing_stock(db, "breyani spice", 250, "g", "30/08/27");
        add_missing_stock(db, "curry powder", 250, "g", "30/08/27");
        add_missing_stock(db, "egg", 12, "pieces", "30/08/27");
        add_missing_stock(db, "butter", 500, "g", "30/08/27");
    }

    // Adding Pantry stock if missing
    private void add_missing_stock(SQLiteDatabase db, String name, double quantity, String unit, String expiryDate) {

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
            values.put(COL_EXPIRY_DATE, expiryDate);

            db.insert(TABLE_PANTRY, null, values);
        }
    }

    // Reades through Recipes
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

    // Reades through Ingredients
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

    // Gets the Recipe & Ingredient Names
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

    // Adding Recipes
    private long addRecipe(SQLiteDatabase db, String name, String method) {

        ContentValues values = new ContentValues();

        values.put(COL_RECIPE_NAME, name);
        values.put(COL_METHOD, method);

        return db.insert(TABLE_RECIPES, null, values);
    }

    // Adding Recipe Ingredients
    private void adding_ingredient(SQLiteDatabase db, long recipeId, String name, double quantity, String unit) {

        ContentValues values = new ContentValues();

        values.put(COL_RECIPE_LINK_ID, recipeId);
        values.put(COL_INGREDIENT_NAME, name);
        values.put(COL_REQUIRED_QUANTITY, quantity);
        values.put(COL_REQUIRED_UNIT, unit);

        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }

    // Loading recipes
    private void samplerecipes(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECIPES,
                null
        );

        if(cursor.moveToFirst() && cursor.getInt(0) > 0) {

            cursor.close();

            return;
        }

        cursor.close();

        // Runny Eggs
        long recipe1 = addRecipe(
                db,
                "Runny Eggs",
                "Heat the butter in a pan. Crack the eggs into the pan and cook until the egg whites are firm while the yolks remain soft and runny."
        );

        adding_ingredient(db, recipe1, "egg", 2, "pieces");
        adding_ingredient(db, recipe1, "butter", 10, "g");

        // Bunny Chow
        long recipe2 = addRecipe(
                db,
                "Bunny Chow",
                "Cook the mutton with onion, tomato, potato and curry powder until tender. Hollow out the bread loaf and fill it with the prepared curry."
        );

        adding_ingredient(db, recipe2, "bread loaf", 1, "pieces");
        adding_ingredient(db, recipe2, "mutton", 500, "g");
        adding_ingredient(db, recipe2, "potato", 2, "pieces");
        adding_ingredient(db, recipe2, "onion", 1, "pieces");
        adding_ingredient(db, recipe2, "tomato", 2, "pieces");
        adding_ingredient(db, recipe2, "curry powder", 20, "g");

        // Tikka Chicken
        long recipe3 = addRecipe(
                db,
                "Tikka Chicken",
                "Mix the chicken with yoghurt and tikka spice. Allow the chicken to marinate and then cook until fully cooked and golden."
        );

        adding_ingredient(db, recipe3, "chicken", 500, "g");
        adding_ingredient(db, recipe3, "yoghurt", 150, "ml");
        adding_ingredient(db, recipe3, "tikka spice", 20, "g");

        // Phutu and Mutton Curry
        long recipe4 = addRecipe(
                db,
                "Phutu and Mutton Curry",
                "Prepare the phutu using maize meal and water. Cook the mutton with onion, tomato and curry powder until tender. Serve the curry with the phutu."
        );

        adding_ingredient(db, recipe4, "maize meal", 250, "g");
        adding_ingredient(db, recipe4, "mutton", 500, "g");
        adding_ingredient(db, recipe4, "onion", 1, "pieces");
        adding_ingredient(db, recipe4, "tomato", 2, "pieces");
        adding_ingredient(db, recipe4, "curry powder", 20, "g");

        // Phutu and Chicken Curry
        long recipe5 = addRecipe(
                db,
                "Phutu and Chicken Curry",
                "Prepare the phutu using maize meal and water. Cook the chicken with onion, tomato and curry powder until tender. Serve the chicken curry with the phutu."
        );

        adding_ingredient(db, recipe5, "maize meal", 250, "g");
        adding_ingredient(db, recipe5, "chicken", 500, "g");
        adding_ingredient(db, recipe5, "onion", 1, "pieces");
        adding_ingredient(db, recipe5, "tomato", 2, "pieces");
        adding_ingredient(db, recipe5, "curry powder", 20, "g");

        // French Toast
        long recipe6 = addRecipe(
                db,
                "French Toast",
                "Beat the egg and milk together. Dip the bread into the mixture and fry in butter until golden brown on both sides."
        );

        adding_ingredient(db, recipe6, "bread", 2, "slices");
        adding_ingredient(db, recipe6, "egg", 1, "pieces");
        adding_ingredient(db, recipe6, "milk", 100, "ml");
        adding_ingredient(db, recipe6, "butter", 10, "g");

        //Roasted Chicken
        long recipe7 = addRecipe(
                db,
                "Roasted Chicken",
                "Season the chicken with salt and oil. Place it in the oven and roast until golden brown and completely cooked."
        );

        adding_ingredient(db, recipe7, "chicken", 1, "pieces");
        adding_ingredient(db, recipe7, "oil", 30, "ml");
        adding_ingredient(db, recipe7, "salt", 5, "g");

        // Chicken Burger and Chips
        long recipe8 = addRecipe(
                db,
                "Chicken Burger and Chips",
                "Cook the chicken patty. Place it inside the burger bun with lettuce and tomato. Cut the potatoes into chips and fry until golden."
        );

        adding_ingredient(db, recipe8, "chicken patty", 1, "pieces");
        adding_ingredient(db, recipe8, "burger bun", 1, "pieces");
        adding_ingredient(db, recipe8, "potato", 2, "pieces");
        adding_ingredient(db, recipe8, "lettuce", 2, "leaves");
        adding_ingredient(db, recipe8, "tomato", 1, "pieces");
        adding_ingredient(db, recipe8, "oil", 250, "ml");

        // Mutton Burger and Chips
        long recipe9 = addRecipe(
                db,
                "Mutton Burger and Chips",
                "Cook the mutton patty. Place it inside the burger bun with lettuce and tomato. Cut the potatoes into chips and fry until golden."
        );

        adding_ingredient(db, recipe9, "mutton patty", 1, "pieces");
        adding_ingredient(db, recipe9, "burger bun", 1, "pieces");
        adding_ingredient(db, recipe9, "potato", 2, "pieces");
        adding_ingredient(db, recipe9, "lettuce", 2, "leaves");
        adding_ingredient(db, recipe9, "tomato", 1, "pieces");
        adding_ingredient(db, recipe9, "oil", 250, "ml");

        // Mushroom and Veg Sausage Omelette
        long recipe10 = addRecipe(
                db,
                "Mushroom and Veg Sausage Omelette",
                "Slice the mushrooms and vegetarian sausage and lightly fry them. Beat the eggs, pour them into the pan, add the filling and fold the omelette."
        );

        adding_ingredient(db, recipe10, "egg", 3, "pieces");
        adding_ingredient(db, recipe10, "mushroom", 100, "g");
        adding_ingredient(db, recipe10, "veg sausage", 2, "pieces");
        adding_ingredient(db, recipe10, "butter", 10, "g");

        // Egg Roll
        long recipe11 = addRecipe(
                db,
                "Egg Roll",
                "Beat and cook the eggs in a pan. Place the cooked eggs inside the wrap and roll it tightly before serving."
        );

        adding_ingredient(db, recipe11, "egg", 2, "pieces");
        adding_ingredient(db, recipe11, "wrap", 1, "pieces");
        adding_ingredient(db, recipe11, "butter", 10, "g");

        // Chicken Breyani
        long recipe12 = addRecipe(
                db,
                "Chicken Breyani",
                "Cook the chicken with onion and breyani spice. Add the rice and potatoes and cook until the rice is tender and the chicken is fully cooked."
        );

        adding_ingredient(db, recipe12, "chicken", 500, "g");
        adding_ingredient(db, recipe12, "rice", 300, "g");
        adding_ingredient(db, recipe12, "potato", 2, "pieces");
        adding_ingredient(db, recipe12, "onion", 1, "pieces");
        adding_ingredient(db, recipe12, "breyani spice", 20, "g");

        // Mutton Breyani
        long recipe13 = addRecipe(
                db,
                "Mutton Breyani",
                "Cook the mutton with onion and breyani spice until tender. Add the rice and potatoes and cook until the rice is ready."
        );

        adding_ingredient(db, recipe13, "mutton", 500, "g");
        adding_ingredient(db, recipe13, "rice", 300, "g");
        adding_ingredient(db, recipe13, "potato", 2, "pieces");
        adding_ingredient(db, recipe13, "onion", 1, "pieces");
        adding_ingredient(db, recipe13, "breyani spice", 20, "g");

        // Wagyu and Chips
        long recipe14 = addRecipe(
                db,
                "Wagyu and Chips",
                "Season and cook the Wagyu steak to the desired level. Cut the potatoes into chips and fry until golden and crispy."
        );

        adding_ingredient(db, recipe14, "wagyu steak", 250, "g");
        adding_ingredient(db, recipe14, "potato", 2, "pieces");
        adding_ingredient(db, recipe14, "oil", 250, "ml");
        adding_ingredient(db, recipe14, "salt", 5, "g");

        // Veg Todays Pizza
        long recipe15 = addRecipe(
                db,
                "Veg Todays Pizza",
                "Place the pizza base on a baking tray. Add the tomato, onion, mushroom and bell pepper. Sprinkle the cheese over the vegetables and bake until the cheese has melted and the pizza is golden."
        );

        adding_ingredient(db, recipe15, "pizza base", 1, "pieces");
        adding_ingredient(db, recipe15, "tomato", 1, "pieces");
        adding_ingredient(db, recipe15, "onion", 1, "pieces");
        adding_ingredient(db, recipe15, "mushroom", 100, "g");
        adding_ingredient(db, recipe15, "bell pepper", 1, "pieces");
        adding_ingredient(db, recipe15, "cheese", 100, "g");
    }

    // Creates suggested recipes
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

                if(!full_ingredient(db, requiredName, requiredQuantity, requiredUnit)) {

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

    // Inspects ingredients in pantry
    private boolean full_ingredient(SQLiteDatabase db, String requiredName, double requiredQuantity, String requiredUnit) {

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

        String requiredIngredient = ingredient_naming(requiredName);

        String requiredBaseUnit = getBaseUnit(requiredUnit);

        while(pantryCursor.moveToNext()) {

            String pantryName = pantryCursor.getString(pantryNameIndex);
            double pantryQuantity = pantryCursor.getDouble(pantryQuantityIndex);
            String pantryUnit = pantryCursor.getString(pantryUnitIndex);

            String pantryIngredient = ingredient_naming(pantryName);

            String pantryBaseUnit = getBaseUnit(pantryUnit);

            if(requiredIngredient.equals(pantryIngredient) && requiredBaseUnit.equals(pantryBaseUnit)) {

                totalQuantity += Converting_quantity(
                        pantryQuantity,
                        pantryUnit
                );
            }
        }

        pantryCursor.close();

        double requiredBaseQuantity = Converting_quantity(
                requiredQuantity,
                requiredUnit
        );

        return totalQuantity >= requiredBaseQuantity;
    }

    // standard Ingredient Name
    private String ingredient_naming(String name) {

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

    // Using different measurements
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

    // Converting units
    private double Converting_quantity(double quantity, String unit) {

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

    // Adding Custom Recipe
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

                adding_ingredient(
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