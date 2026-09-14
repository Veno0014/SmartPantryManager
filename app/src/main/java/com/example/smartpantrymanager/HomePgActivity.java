package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePgActivity extends AppCompatActivity {

    // Home page components
    MaterialToolbar topAppBar;
    TextView txtHello;
    EditText editSearch;
    MaterialCardView cardPantry;
    MaterialCardView cardRecipes;
    MaterialCardView cardSuggested;
    Button btnViewSuggested;
    FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_pg);

        // Connect Java to XML
        topAppBar = findViewById(R.id.topAppBar);
        txtHello = findViewById(R.id.txtHello);
        editSearch = findViewById(R.id.editSearch);
        cardPantry = findViewById(R.id.cardPantry);
        cardRecipes = findViewById(R.id.cardRecipes);
        cardSuggested = findViewById(R.id.cardSuggested);
        btnViewSuggested = findViewById(R.id.btnViewSuggested);
        btnAdd = findViewById(R.id.btnAdd);

        // Get username from login page
        String username = getIntent().getStringExtra("username");

        // Show username on Home page
        if(username != null){
            topAppBar.setTitle(username + "'s Pantry");
            txtHello.setText("Hello, " + username);
        } else {
            topAppBar.setTitle("My Pantry");
            txtHello.setText("Hello, User");
        }

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // My Pantry card
        cardPantry.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "My Pantry selected", Toast.LENGTH_SHORT).show();
        });

        // Recipes card
        cardRecipes.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "Recipes selected", Toast.LENGTH_SHORT).show();
        });

        // Suggested Recipes card
        cardSuggested.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "Suggested Recipes selected", Toast.LENGTH_SHORT).show();
        });

        // View Suggested Recipes button
        btnViewSuggested.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "Opening Suggested Recipes", Toast.LENGTH_SHORT).show();
        });

        // Plus button
        btnAdd.setOnClickListener(v -> addMenu());

        // Three dot menu
        topAppBar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            // Open My Pantry
            if(id == R.id.menuPantry){
                Toast.makeText(HomePgActivity.this, "My Pantry selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            // Open Recipes
            if(id == R.id.menuRecipes){
                Toast.makeText(HomePgActivity.this, "Recipes selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            // Open Suggested Recipes
            if(id == R.id.menuSuggestedRecipes){
                Toast.makeText(HomePgActivity.this, "Suggested Recipes selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            // Open Settings
            if(id == R.id.menuSettings){
                Toast.makeText(HomePgActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            // Logout user
            if(id == R.id.menuLogout){
                Intent intent = new Intent(HomePgActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    // Shows Add Ingredient and Add Recipe options
    private void addMenu(){

        String[] options = {"Add Ingredient", "Add Recipe"};

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePgActivity.this);

        builder.setTitle("What would you like to add?");

        builder.setItems(options, (dialog, which) -> {

            // Open Ingredients page
            if(which == 0){
                Intent intent = new Intent(HomePgActivity.this, IngredientsActivity.class);
                startActivity(intent);
            }

            // Add Recipe option
            if(which == 1){
                Toast.makeText(HomePgActivity.this, "Add Recipe selected", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
}