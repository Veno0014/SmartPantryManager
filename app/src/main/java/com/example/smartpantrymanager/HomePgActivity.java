package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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

    MaterialToolbar topAppBar;
    TextView txtHello;
    EditText editSearch;
    MaterialCardView cardPantry;
    MaterialCardView cardRecipes;
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
        btnViewSuggested = findViewById(R.id.btnViewSuggested);
        btnAdd = findViewById(R.id.btnAdd);

        // Get username
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

        // Search bar
        editSearch.setOnEditorActionListener((v, actionId, event) -> {

            boolean searchPressed = actionId == EditorInfo.IME_ACTION_SEARCH;

            boolean enterPressed = event != null &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getAction() == KeyEvent.ACTION_DOWN;

            if(searchPressed || enterPressed) {

                String searchText = editSearch.getText().toString().trim();

                if(searchText.isEmpty()) {
                    editSearch.setError("Enter an ingredient or recipe name");
                    return true;
                }

                showSearchOptions(searchText);

                return true;
            }

            return false;
        });

        // Pantry
        cardPantry.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, PantryAct.class);
            startActivity(intent);
        });

        // Recipes
        cardRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, RecipesAct.class);
            startActivity(intent);
        });

        // Suggested Recipes
        btnViewSuggested.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, SuggestedRecipesAct.class);
            startActivity(intent);
        });

        // Plus button
        btnAdd.setOnClickListener(v -> showAddMenu());

        // Toolbar menu
        topAppBar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if(id == R.id.menuPantry) {
                Intent intent = new Intent(HomePgActivity.this, PantryAct.class);
                startActivity(intent);
                return true;
            }

            if(id == R.id.menuRecipes) {
                Intent intent = new Intent(HomePgActivity.this, RecipesAct.class);
                startActivity(intent);
                return true;
            }

            if(id == R.id.menuSuggestedRecipes) {
                Intent intent = new Intent(HomePgActivity.this, SuggestedRecipesAct.class);
                startActivity(intent);
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

    // Search Ingredients or Recipes
    private void showSearchOptions(String searchText) {

        String[] options = {
                "Search Ingredients",
                "Search Recipes"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePgActivity.this);

        builder.setTitle("Search for \"" + searchText + "\"");

        builder.setItems(options, (dialog, which) -> {

            if(which == 0) {
                Intent intent = new Intent(HomePgActivity.this, PantryAct.class);
                intent.putExtra("search_query", searchText);
                startActivity(intent);
            }

            if(which == 1) {
                Intent intent = new Intent(HomePgActivity.this, RecipesAct.class);
                intent.putExtra("search_query", searchText);
                startActivity(intent);
            }
        });

        builder.show();
    }

    // Plus Button Menu
    private void showAddMenu() {

        String[] options = {
                "Add Ingredient",
                "Add Recipe"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePgActivity.this);

        builder.setTitle("What would you like to add?");

        builder.setItems(options, (dialog, which) -> {

            if(which == 0) {
                Intent intent = new Intent(HomePgActivity.this, IngredientsActivity.class);
                startActivity(intent);
            }

            if(which == 1) {
                Intent intent = new Intent(HomePgActivity.this, act_add_recipe.class);
                startActivity(intent);
            }
        });

        builder.show();
    }
}