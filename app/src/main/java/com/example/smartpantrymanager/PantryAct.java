package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class PantryAct extends AppCompatActivity {

    // Pantry components
    ListView listPantry;
    TextView txtEmptyPantry;
    Button btnAddIngredient;

    // Navigation components
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar pantryToolbar;
    ActionBarDrawerToggle drawerToggle;

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

        // Connect navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        pantryToolbar = findViewById(R.id.pantryToolbar);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Create pantry list
        pantryItems = new ArrayList<>();

        // Create pantry adapter
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

        // Connect adapter
        listPantry.setAdapter(pantryAdapter);

        // Empty pantry message
        listPantry.setEmptyView(txtEmptyPantry);

        // Create hamburger menu
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, pantryToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        // Show Pantry as current page
        navigationView.setCheckedItem(R.id.navPantry);

        // Hamburger menu
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            // Home
            if(id == R.id.navHome) {
                Intent intent = new Intent(PantryAct.this, HomePgActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }

            // Pantry
            if(id == R.id.navPantry) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            // Recipes
            if(id == R.id.navRecipes) {
                Intent intent = new Intent(PantryAct.this, RecipesAct.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }

            // Suggested Recipes
            if(id == R.id.navSuggested) {
                Intent intent = new Intent(PantryAct.this, SuggestedRecipesAct.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }

            return false;
        });

        // Add Ingredient
        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(PantryAct.this, IngredientsActivity.class);
            startActivity(intent);
        });

        // Back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {

                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
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

        // Get search from Home screen
        String searchQuery = getIntent().getStringExtra("search_query");

        loadPantry(searchQuery);
    }

    // Load pantry
    private void loadPantry(String searchQuery) {

        pantryItems.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID);
        int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME);
        int quantityIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY);
        int unitIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT);
        int expiryIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY_DATE);

        String searchText = "";

        if(searchQuery != null) {
            searchText = searchQuery.trim().toLowerCase();
        }

        while(cursor.moveToNext()) {

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            double quantity = cursor.getDouble(quantityIndex);
            String unit = cursor.getString(unitIndex);

            String expiryDate = "";

            if(!cursor.isNull(expiryIndex)) {
                expiryDate = cursor.getString(expiryIndex);
            }

            // Show all ingredients or matching ingredients
            if(searchText.isEmpty() || name.toLowerCase().contains(searchText)) {
                PantryItems item = new PantryItems(id, name, quantity, unit, expiryDate);
                pantryItems.add(item);
            }
        }

        cursor.close();

        // Change title when searching
        if(searchText.isEmpty()) {
            pantryToolbar.setTitle("My Pantry");
            txtEmptyPantry.setText("Your pantry is empty.");
        } else {
            pantryToolbar.setTitle("Search: " + searchQuery);
            txtEmptyPantry.setText("No ingredients found for \"" + searchQuery + "\".");
        }

        pantryAdapter.notifyDataSetChanged();
    }

    // Edit ingredient
    private void editIngredient(PantryItems item) {

        Intent intent = new Intent(PantryAct.this, IngredientsActivity.class);

        intent.putExtra("ingredient_id", item.getId());
        intent.putExtra("ingredient_name", item.getName());
        intent.putExtra("ingredient_quantity", item.getQuantity());
        intent.putExtra("ingredient_unit", item.getUnit());
        intent.putExtra("ingredient_expiry", item.getExpiryDate());

        startActivity(intent);
    }

    // Delete ingredient
    private void deleteIngredient(PantryItems item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PantryAct.this);

        builder.setTitle("Delete Ingredient");

        builder.setMessage("Are you sure you want to delete " + item.getName() + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {

            int result = databaseHelper.deleteIngredient(item.getId());

            if(result > 0) {

                Toast.makeText(PantryAct.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();

                String searchQuery = getIntent().getStringExtra("search_query");

                loadPantry(searchQuery);

            } else {

                Toast.makeText(PantryAct.this, "Unable to delete ingredient", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}