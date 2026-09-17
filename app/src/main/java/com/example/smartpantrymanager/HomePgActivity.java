package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
        cardPantry = findViewById(R.id.cardPantry);
        cardRecipes = findViewById(R.id.cardRecipes);
        cardSuggested = findViewById(R.id.cardSuggested);
        btnViewSuggested = findViewById(R.id.btnViewSuggested);
        btnAdd = findViewById(R.id.btnAdd);

        // Get username from Login screen
        String username = getIntent().getStringExtra("username");

        if(username != null && !username.isEmpty()) {
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

        // Open My Pantry
        cardPantry.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, PantryAct.class);
            startActivity(intent);
        });

        // Recipes
        cardRecipes.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "Recipes selected", Toast.LENGTH_SHORT).show();
        });

        // Suggested Recipes
        cardSuggested.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "Suggested Recipes selected", Toast.LENGTH_SHORT).show();
        });

        // Suggested Recipes button
        btnViewSuggested.setOnClickListener(v -> {
            Toast.makeText(HomePgActivity.this, "Opening Suggested Recipes", Toast.LENGTH_SHORT).show();
        });

        // Add button
        btnAdd.setOnClickListener(v -> addMenu());

        // Toolbar menu
        topAppBar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if(id == R.id.menuPantry) {
                Intent intent = new Intent(HomePgActivity.this, PantryAct.class);
                startActivity(intent);
                return true;
            }

            if(id == R.id.menuRecipes) {
                Toast.makeText(HomePgActivity.this, "Recipes selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            if(id == R.id.menuSuggestedRecipes) {
                Toast.makeText(HomePgActivity.this, "Suggested Recipes selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            if(id == R.id.menuSettings) {
                Toast.makeText(HomePgActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            if(id == R.id.menuLogout) {
                Intent intent = new Intent(HomePgActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    // Add menu
    private void addMenu() {

        String[] options = {"Add Ingredient"};

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePgActivity.this);

        builder.setTitle("What would you like to add?");

        builder.setItems(options, (dialog, which) -> {

            if(which == 0) {
                Intent intent = new Intent(HomePgActivity.this, IngredientsActivity.class);
                startActivity(intent);
            }
        });

        builder.show();
    }
}