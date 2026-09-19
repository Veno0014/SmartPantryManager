package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class act_add_recipe extends AppCompatActivity {

    EditText editRecipeName;
    EditText editRecipeIngredients;
    EditText editRecipeMethod;

    Button btnSaveRecipe;
    Button btnCancelRecipe;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_add_recipe);

        // Connect Java to XML
        editRecipeName = findViewById(R.id.editRecipeName);
        editRecipeIngredients = findViewById(R.id.editRecipeIngredients);
        editRecipeMethod = findViewById(R.id.editRecipeMethod);

        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Save Recipe
        btnSaveRecipe.setOnClickListener(v -> saveRecipe());

        // Cancel and return to Home
        btnCancelRecipe.setOnClickListener(v -> {

            Intent intent = new Intent(
                    act_add_recipe.this,
                    HomePgActivity.class
            );

            startActivity(intent);

            finish();
        });
    }

    private void saveRecipe() {

        String recipeName = editRecipeName.getText().toString().trim();

        String ingredientsText = editRecipeIngredients.getText().toString().trim();

        String method = editRecipeMethod.getText().toString().trim();

        // Check Recipe Name
        if(recipeName.isEmpty()) {

            editRecipeName.setError("Please enter a recipe name");

            return;
        }

        // Check Ingredients
        if(ingredientsText.isEmpty()) {

            editRecipeIngredients.setError("Please enter ingredients");

            return;
        }

        // Check Cooking Steps
        if(method.isEmpty()) {

            editRecipeMethod.setError("Please enter cooking steps");

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

                    editRecipeIngredients.setError(
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

                    editRecipeIngredients.setError(
                            "Invalid quantity on line " + (i + 1)
                    );

                    return;
                }

                if(ingredientName.isEmpty()) {

                    editRecipeIngredients.setError(
                            "Ingredient name missing on line " + (i + 1)
                    );

                    return;
                }

                if(ingredientQuantity <= 0) {

                    editRecipeIngredients.setError(
                            "Quantity must be more than 0 on line " + (i + 1)
                    );

                    return;
                }

                if(ingredientUnit.isEmpty()) {

                    editRecipeIngredients.setError(
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