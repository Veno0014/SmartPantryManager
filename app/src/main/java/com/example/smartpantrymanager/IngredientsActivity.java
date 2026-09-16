package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class IngredientsActivity extends AppCompatActivity {

    // Ingredient components
    TextView txtIngredientTitle;
    EditText editIngredientName;
    EditText editQuantity;
    EditText editUnit;
    EditText editExpiryDate;
    Button btnSaveIngredient;

    // Database
    DatabaseHelper databaseHelper;

    // Ingredient ID
    int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredients);

        // Connect Java to XML
        txtIngredientTitle = findViewById(R.id.txtIngredientTitle);
        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiryDate = findViewById(R.id.editExpiryDate);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Check if ingredient is being edited
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

        // Expiry Date
        editExpiryDate.setOnClickListener(v -> selectDate());

        // Save Ingredient
        btnSaveIngredient.setOnClickListener(v -> saveIngredient());

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Save or Update Ingredient
    private void saveIngredient() {

        String name = editIngredientName.getText().toString().trim();
        String quantity = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiryDate = editExpiryDate.getText().toString().trim();

        // Validate name
        if(name.isEmpty()) {
            editIngredientName.setError("Enter ingredient name");
            return;
        }

        // Validate quantity
        if(quantity.isEmpty()) {
            editQuantity.setError("Enter quantity");
            return;
        }

        // Validate unit
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

        // Add new ingredient
        if(ingredientId == -1) {

            long result = databaseHelper.addIngredient(name, quantityValue, unit, expiryDate);

            if(result != -1) {

                Toast.makeText(
                        IngredientsActivity.this,
                        "Ingredient saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        IngredientsActivity.this,
                        "Unable to save ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            // Update existing ingredient
            int result = databaseHelper.updateIngredient(
                    ingredientId,
                    name,
                    quantityValue,
                    unit,
                    expiryDate
            );

            if(result > 0) {

                Toast.makeText(
                        IngredientsActivity.this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        IngredientsActivity.this,
                        "Unable to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // Select Expiry Date
    private void selectDate() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                IngredientsActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );

                    editExpiryDate.setText(date);

                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }
}