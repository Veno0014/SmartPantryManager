package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class act_add_recipe extends AppCompatActivity {

    EditText edit_RecipeName;
    EditText edit_recipe_ingred;
    EditText edit_recipe_method;

    Button save_button;
    Button cancel_button;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_add_recipe);

        // Connecting Java code  to XML code
        edit_RecipeName = findViewById(R.id.editRecipeName);
        edit_recipe_ingred = findViewById(R.id.editRecipeIngredients);
        edit_recipe_method = findViewById(R.id.editRecipeMethod);

        save_button = findViewById(R.id.btnSaveRecipe);
        cancel_button = findViewById(R.id.btnCancelRecipe);

        // Connecting database
        databaseHelper = new DatabaseHelper(this);

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

    private void saveRecipe() {

        String recipeName = edit_RecipeName.getText().toString().trim();

        String ingredientsText = edit_RecipeName.getText().toString().trim();

        String method = edit_recipe_method.getText().toString().trim();

        // Checking for recipe
        if(recipeName.isEmpty()) {

            edit_RecipeName.setError("Please enter a recipe name");

            return;
        }

        // Searching for ingredients
        if(ingredientsText.isEmpty()) {

            edit_recipe_ingred.setError("Please enter ingredients");

            return;
        }

        // Showing cooking steps
        if(method.isEmpty()) {

            edit_recipe_method.setError("Please enter cooking steps");

            return;
        }

        ArrayList<String> ingredientNames = new ArrayList<>();

        ArrayList<Double> ingredientQuantities = new ArrayList<>();

        ArrayList<String> ingredientUnits = new ArrayList<>();

        // Separate each ingredient line
        String[] ingredientLines = ingredientsText.split("\n");

        for(int i = 0; i < ingredientLines.length; i++) {

            String line = ingredientLines[i].trim();

            if(!line.isEmpty()) {

                String[] ingredientParts = line.split(",");

                if(ingredientParts.length != 3) {

                    edit_recipe_ingred.setError(
                            "Use: Name, Quantity, Unit on line " + (i + 1)
                    );

                    return;
                }

                String ingredientName = ingredientParts[0].trim();

                String quantityText = ingredientParts[1].trim();

                String ingredientUnit = ingredientParts[2].trim();

                double ingredientQuantity;

                try {

                    ingredientQuantity = Double.parseDouble(quantityText);

                } catch(NumberFormatException e) {

                    edit_recipe_ingred.setError(
                            "Invalid quantity on line " + (i + 1)
                    );

                    return;
                }

                if(ingredientName.isEmpty()) {

                    edit_recipe_ingred.setError(
                            "Ingredient name missing on line " + (i + 1)
                    );

                    return;
                }

                if(ingredientQuantity <= 0) {

                    edit_recipe_ingred.setError(
                            "Quantity must be more than 0 on line " + (i + 1)
                    );

                    return;
                }

                if(ingredientUnit.isEmpty()) {

                    edit_recipe_ingred.setError(
                            "Unit missing on line " + (i + 1)
                    );

                    return;
                }

                ingredientNames.add(ingredientName);

                ingredientQuantities.add(ingredientQuantity);

                ingredientUnits.add(ingredientUnit);
            }
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
                    HomePgActivity.class
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