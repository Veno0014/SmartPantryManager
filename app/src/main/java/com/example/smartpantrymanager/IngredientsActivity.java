package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    EditText editIngredientName;
    EditText editQuantity;
    EditText editUnit;
    EditText editExpiryDate;
    Button btnSaveIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredients);

        // Connect Java to XML
        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiryDate = findViewById(R.id.editExpiryDate);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Select expiry date
        editExpiryDate.setOnClickListener(v -> selectDate());

        // Save Ingredient
        btnSaveIngredient.setOnClickListener(v -> saveIngredient());
    }

    // Ingredient validation
    private void saveIngredient(){

        String name = editIngredientName.getText().toString().trim();
        String quantity = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();

        // Check ingredient name
        if(name.isEmpty()){
            editIngredientName.setError("Enter ingredient name");
            return;
        }

        // Check quantity
        if(quantity.isEmpty()){
            editQuantity.setError("Enter quantity");
            return;
        }

        // Check unit
        if(unit.isEmpty()){
            editUnit.setError("Enter unit");
            return;
        }

        // Check quantity is a number
        double quantityValue;

        try {

            quantityValue = Double.parseDouble(quantity);

        } catch(NumberFormatException e){

            editQuantity.setError("Enter a valid quantity");
            return;
        }

        // Quantity cannot be zero
        if(quantityValue <= 0){

            editQuantity.setError("Quantity must be greater than 0");
            return;
        }

        // Database will be added next
        Toast.makeText(IngredientsActivity.this, "Ingredient details accepted", Toast.LENGTH_SHORT).show();
    }

    // Expiry Date picker
    private void selectDate(){

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(IngredientsActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {

            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);

            editExpiryDate.setText(date);

        }, year, month, day);

        datePickerDialog.show();
    }
}