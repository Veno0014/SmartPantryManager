package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class RecipesAct extends AppCompatActivity {

    // Recipe components
    ListView listRecipes;
    TextView txtEmptyRecipes;

    // Database
    DatabaseHelper databaseHelper;

    // Recipe list
    ArrayList<Recipes> recipes;
    RecipesApt recipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_recipes);

        // Connect Java to XML
        listRecipes = findViewById(R.id.listRecipes);
        txtEmptyRecipes = findViewById(R.id.txtEmptyRecipes);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Create recipe list
        recipes = new ArrayList<>();

        // Connect RecipesApt
        recipesAdapter = new RecipesApt(this, recipes);

        // Connect Adapter to ListView
        listRecipes.setAdapter(recipesAdapter);

        // Empty recipe message
        listRecipes.setEmptyView(txtEmptyRecipes);

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load recipes
        loadRecipes();
    }

    // Load recipes from SQLite
    private void loadRecipes() {

        recipes.clear();

        Cursor cursor = databaseHelper.getAllRecipes();

        int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID);
        int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_NAME);
        int methodIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_METHOD);

        while(cursor.moveToNext()) {

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String method = cursor.getString(methodIndex);

            recipes.add(new Recipes(id, name, method));
        }

        cursor.close();

        recipesAdapter.notifyDataSetChanged();
    }
}