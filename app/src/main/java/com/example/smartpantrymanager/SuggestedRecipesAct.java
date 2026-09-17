package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SuggestedRecipesAct extends AppCompatActivity {

    // Suggested Recipe components
    ListView listSuggestedRecipes;
    TextView txtNoSuggestedRecipes;

    // Database
    DatabaseHelper databaseHelper;

    // Suggested Recipe list
    ArrayList<Recipes> suggestedRecipes;
    RecipesApt recipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_suggested_recipes);

        // Connect Java to XML
        listSuggestedRecipes = findViewById(R.id.listSuggestedRecipes);
        txtNoSuggestedRecipes = findViewById(R.id.txtNoSuggestedRecipes);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Create recipe list
        suggestedRecipes = new ArrayList<>();

        // Connect adapter
        recipesAdapter = new RecipesApt(this, suggestedRecipes);

        listSuggestedRecipes.setAdapter(recipesAdapter);

        // Show message if no recipes match
        listSuggestedRecipes.setEmptyView(txtNoSuggestedRecipes);

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load suggested recipes
        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    // Load recipes that match the pantry
    private void loadSuggestedRecipes() {

        suggestedRecipes.clear();

        suggestedRecipes.addAll(databaseHelper.getSuggestedRecipes());

        recipesAdapter.notifyDataSetChanged();
    }
}