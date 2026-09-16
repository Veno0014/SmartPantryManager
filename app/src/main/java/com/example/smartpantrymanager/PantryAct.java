package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class PantryAct extends AppCompatActivity {

    // Pantry components
    ListView listPantry;
    TextView txtEmptyPantry;
    Button btnAddIngredient;

    // Database
    DatabaseHelper databaseHelper;

    // Pantry list
    ArrayList<PantryItems> pantryItems;
    PantryApt pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_pantry);

        // Connect Java to XML
        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Create pantry list
        pantryItems = new ArrayList<>();

        // Connect Adapter
        pantryAdapter = new PantryApt(this, pantryItems, new PantryApt.OnPantryActionListener() {

            @Override
            public void onEdit(PantryItems item) {
                editIngredient(item);
            }

            @Override
            public void onDelete(PantryItems item) {
                deleteIngredient(item);
            }
        });

        // Connect Adapter to ListView
        listPantry.setAdapter(pantryAdapter);

        // Empty pantry message
        listPantry.setEmptyView(txtEmptyPantry);

        // Add Ingredient button
        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(PantryAct.this, IngredientsActivity.class);
            startActivity(intent);
        });

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh pantry
        loadPantry();
    }

    // Load ingredients from SQLite
    private void loadPantry() {

        pantryItems.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID);
        int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME);
        int quantityIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY);
        int unitIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT);
        int expiryIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY_DATE);

        while(cursor.moveToNext()) {

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            double quantity = cursor.getDouble(quantityIndex);
            String unit = cursor.getString(unitIndex);

            String expiryDate = "";

            if(!cursor.isNull(expiryIndex)) {
                expiryDate = cursor.getString(expiryIndex);
            }

            pantryItems.add(new PantryItems(id, name, quantity, unit, expiryDate));
        }

        cursor.close();

        pantryAdapter.notifyDataSetChanged();
    }

    // Edit Ingredient
    private void editIngredient(PantryItems item) {

        Intent intent = new Intent(PantryAct.this, IngredientsActivity.class);

        intent.putExtra("ingredient_id", item.getId());
        intent.putExtra("ingredient_name", item.getName());
        intent.putExtra("ingredient_quantity", item.getQuantity());
        intent.putExtra("ingredient_unit", item.getUnit());
        intent.putExtra("ingredient_expiry", item.getExpiryDate());

        startActivity(intent);
    }

    // Delete Ingredient
    private void deleteIngredient(PantryItems item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PantryAct.this);

        builder.setTitle("Delete Ingredient");

        builder.setMessage("Are you sure you want to delete " + item.getName() + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {

            int result = databaseHelper.deleteIngredient(item.getId());

            if(result > 0) {
                Toast.makeText(PantryAct.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                loadPantry();
            } else {
                Toast.makeText(PantryAct.this, "Unable to delete ingredient", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}