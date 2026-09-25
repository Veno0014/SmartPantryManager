package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class RecipesAct extends AppCompatActivity {

    // Recipe components
    ListView listRecipes;
    TextView txtEmptyRecipes;

    // Navigation components
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar recipesToolbar;
    ActionBarDrawerToggle drawerToggle;

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

        // Connects Java code to XML code
        listRecipes = findViewById(R.id.listRecipes);
        txtEmptyRecipes = findViewById(R.id.txtEmptyRecipes);

        // Connects navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        recipesToolbar = findViewById(R.id.recipesToolbar);

        // Connects to database
        databaseHelper = new DatabaseHelper(this);

        // Creates recipe list
        recipes = new ArrayList<>();

        // Connects adapter
        recipesAdapter = new RecipesApt(this, recipes);

        listRecipes.setAdapter(recipesAdapter);
        listRecipes.setEmptyView(txtEmptyRecipes);

        // Hamburger button
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                recipesToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setCheckedItem(R.id.navRecipes);

        // Hamburger navigation
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if(id == R.id.navHome) {
                Intent intent = new Intent(RecipesAct.this, HomePgActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }

            if(id == R.id.navPantry) {
                Intent intent = new Intent(RecipesAct.this, PantryAct.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }

            if(id == R.id.navRecipes) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            if(id == R.id.navSuggested) {
                Intent intent = new Intent(RecipesAct.this, SuggestedRecipesAct.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }

            return false;
        });

        // Back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {

                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Adjust screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        // Get search from Home
        String searchQuery = getIntent().getStringExtra("search_query");

        loadRecipes(searchQuery);
    }

    // Load Recipes
    private void loadRecipes(String searchQuery) {

        recipes.clear();

        Cursor cursor = databaseHelper.getAllRecipes();

        int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID);
        int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_NAME);
        int methodIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_METHOD);

        while(cursor.moveToNext()) {

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String method = cursor.getString(methodIndex);

            if(searchQuery == null || searchQuery.trim().isEmpty()) {

                recipes.add(new Recipes(id, name, method));

            } else {

                String recipeName = name.toLowerCase(Locale.ROOT);
                String searchText = searchQuery.toLowerCase(Locale.ROOT).trim();

                if(recipeName.contains(searchText)) {
                    recipes.add(new Recipes(id, name, method));
                }
            }
        }

        cursor.close();

        if(searchQuery != null && !searchQuery.trim().isEmpty()) {
            recipesToolbar.setTitle("Search: " + searchQuery);
            txtEmptyRecipes.setText("No recipes found for \"" + searchQuery + "\".");
        } else {
            recipesToolbar.setTitle("Recipes");
            txtEmptyRecipes.setText("No recipes found.");
        }

        recipesAdapter.notifyDataSetChanged();
    }
}