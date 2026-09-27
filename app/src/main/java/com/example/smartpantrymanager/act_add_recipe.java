package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class act_add_recipe extends AppCompatActivity {

    EditText edit_RecipeName;
    EditText edit_ingredient_name;
    EditText edit_ingredient_quantity;
    EditText edit_ingredient_unit;
    EditText edit_recipe_method;

    TextView txt_added_ingredients;

    Button add_ingredient_button;
    Button save_button;
    Button cancel_button;

    DatabaseHelper databaseHelper;

    ArrayList<String> ingredientNames = new ArrayList<>();
    ArrayList<Double> ingredientQuantities = new ArrayList<>();
    ArrayList<String> ingredientUnits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_add_recipe);

        // Connecting Java code  to XML code
        edit_RecipeName = findViewById(R.id.editRecipeName);
        edit_ingredient_name = findViewById(R.id.editIngredientName);
        edit_ingredient_quantity = findViewById(R.id.editIngredientQuantity);
        edit_ingredient_unit = findViewById(R.id.editIngredientUnit);
        edit_recipe_method = findViewById(R.id.editRecipeMethod);

        txt_added_ingredients = findViewById(R.id.txtAddedIngredients);

        add_ingredient_button = findViewById(R.id.btnAddRecipeIngredient);
        save_button = findViewById(R.id.btnSaveRecipe);
        cancel_button = findViewById(R.id.btnCancelRecipe);

        // Connecting database
        databaseHelper = new DatabaseHelper(this);

        // Add ingredient button
        add_ingredient_button.setOnClickListener(v -> addIngredient());

        // Save recipe button
        save_button.setOnClickListener(v -> saveRecipe());

        // Cancel button and fucntion to return home
        cancel_button.setOnClickListener(v -> {

            Intent intent = new Intent(
                    act_add_recipe.this,
                    HomePgActivity.class
            );

            startActivity(intent);

            finish();
        });
    }

    private void addIngredient() {

        String ingredientName = edit_ingredient_name.getText().toString().trim();

        String quantityText = edit_ingredient_quantity.getText().toString().trim();

        String ingredientUnit = edit_ingredient_unit.getText().toString().trim();

        // Searching for ingredients
        if(ingredientName.isEmpty()) {

            edit_ingredient_name.setError("Please enter ingredient name");

            return;
        }

        if(quantityText.isEmpty()) {

            edit_ingredient_quantity.setError("Please enter quantity");

            return;
        }

        if(ingredientUnit.isEmpty()) {

            edit_ingredient_unit.setError("Please enter unit");

            return;
        }

        double ingredientQuantity;

        try {

            ingredientQuantity = Double.parseDouble(quantityText);

        } catch(NumberFormatException e) {

            edit_ingredient_quantity.setError("Please enter a valid quantity");

            return;
        }

        if(ingredientQuantity <= 0) {

            edit_ingredient_quantity.setError("Quantity must be more than 0");

            return;
        }

        ingredientNames.add(ingredientName);

        ingredientQuantities.add(ingredientQuantity);

        ingredientUnits.add(ingredientUnit);

        updateIngredientList();

        Toast.makeText(
                act_add_recipe.this,
                "Ingredient added",
                Toast.LENGTH_SHORT
        ).show();

        edit_ingredient_name.setText("");
        edit_ingredient_quantity.setText("");
        edit_ingredient_unit.setText("");

        edit_ingredient_name.requestFocus();
    }

    private void updateIngredientList() {

        if(ingredientNames.isEmpty()) {

            txt_added_ingredients.setText("No ingredients added yet");

            return;
        }

        StringBuilder ingredientList = new StringBuilder();

        for(int i = 0; i < ingredientNames.size(); i++) {

            ingredientList.append(i + 1)
                    .append(". ")
                    .append(ingredientNames.get(i))
                    .append(" - ")
                    .append(ingredientQuantities.get(i))
                    .append(" ")
                    .append(ingredientUnits.get(i));

            if(i < ingredientNames.size() - 1) {

                ingredientList.append("\n");
            }
        }

        txt_added_ingredients.setText(ingredientList.toString());
    }

    private void saveRecipe() {

        String recipeName = edit_RecipeName.getText().toString().trim();

        String method = edit_recipe_method.getText().toString().trim();

        // Checking for recipe
        if(recipeName.isEmpty()) {

            edit_RecipeName.setError("Please enter a recipe name");

            return;
        }

        // Searching for ingredients
        if(ingredientNames.isEmpty()) {

            Toast.makeText(
                    act_add_recipe.this,
                    "Please add at least one ingredient",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Showing cooking steps
        if(method.isEmpty()) {

            edit_recipe_method.setError("Please enter cooking steps");

            return;
        }

        long result = databaseHelper.addCustomRecipe(
                recipeName,
                method,
                ingredientNames,
                ingredientQuantities,
                ingredientUnits
        );

        if(result != -1) {

            Toast.makeText(
                    act_add_recipe.this,
                    "Recipe added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    act_add_recipe.this,
                    RecipesAct.class
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    act_add_recipe.this,
                    "Recipe could not be added",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}