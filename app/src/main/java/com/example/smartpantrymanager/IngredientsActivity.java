package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class IngredientsActivity extends AppCompatActivity {

    // Ingredient components
    TextView txtIngredientTitle;
    AutoCompleteTextView editIngredientName;
    EditText editQuantity;
    EditText editUnit;
    EditText editExpiryDate;
    Button btnSaveIngredient;
    Button btnCancelIngredient;

    // Database
    DatabaseHelper databaseHelper;

    // using the ingredient id when updating/editing
    int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredients);

        // Connect java code to XML code
        txtIngredientTitle = findViewById(R.id.txtIngredientTitle);
        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiryDate = findViewById(R.id.editExpiryDate);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnCancelIngredient = findViewById(R.id.btnCancelIngredient);

        // Connects database
        databaseHelper = new DatabaseHelper(this);

        // Load ingredient suggestions from recipes
        loadIngredientSuggestions();

        // Checks if user is editing ingredients
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if(ingredientId != -1) {

            txtIngredientTitle.setText("Edit Ingredient");
            btnSaveIngredient.setText("Update Ingredient");

            editIngredientName.setText(getIntent().getStringExtra("ingredient_name"));

            double quantity = getIntent().getDoubleExtra("ingredient_quantity", 0);

            if(quantity == Math.floor(quantity)) {
                editQuantity.setText(String.valueOf((int) quantity));
            } else {
                editQuantity.setText(String.valueOf(quantity));
            }

            editUnit.setText(getIntent().getStringExtra("ingredient_unit"));

            String expiryDate = getIntent().getStringExtra("ingredient_expiry");

            if(expiryDate != null) {
                editExpiryDate.setText(expiryDate);
            }
        }

        // User sees ingredient suggestions
        editIngredientName.setOnClickListener(v -> editIngredientName.showDropDown());

        // Open date selector
        editExpiryDate.setOnClickListener(v -> selectDate());

        // Saves Ingredient
        btnSaveIngredient.setOnClickListener(v -> saveIngredient());

        btnCancelIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(IngredientsActivity.this, HomePgActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Loads ingredients from recipe database
    private void loadIngredientSuggestions() {

        ArrayList<String> ingredientNames = databaseHelper.getRecipeIngredientNames();

        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ingredientNames);

        editIngredientName.setAdapter(ingredientAdapter);

        editIngredientName.setThreshold(1);
    }

    // Saving to updating ingredients
    private void saveIngredient() {

        String name = editIngredientName.getText().toString().trim();
        String quantity = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiryDate = editExpiryDate.getText().toString().trim();

        if(name.isEmpty()) {
            editIngredientName.setError("Enter ingredient name");
            return;
        }

        if(quantity.isEmpty()) {
            editQuantity.setError("Enter quantity");
            return;
        }

        if(unit.isEmpty()) {
            editUnit.setError("Enter unit");
            return;
        }

        double quantityValue;

        try {
            quantityValue = Double.parseDouble(quantity);
        } catch(NumberFormatException e) {
            editQuantity.setError("Enter a valid quantity");
            return;
        }

        if(quantityValue <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            return;
        }

        if(ingredientId == -1) {

            long result = databaseHelper.addingredient(name, quantityValue, unit, expiryDate);

            if(result != -1) {
                Toast.makeText(this, "Ingredient saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Unable to save ingredient", Toast.LENGTH_SHORT).show();
            }

        } else {

            int result = databaseHelper.updateIngredient(ingredientId, name, quantityValue, unit, expiryDate);

            if(result > 0) {
                Toast.makeText(this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Unable to update ingredient", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Adding an expiry data
    private void selectDate() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                IngredientsActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

                    editExpiryDate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }
}