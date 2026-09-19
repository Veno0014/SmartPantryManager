package com.example.smartpantrymanager;

import android.content.Intent;
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

public class SuggestedRecipesAct extends AppCompatActivity {

    // Suggested Recipe components
    ListView listSuggestedRecipes;
    TextView txtNoSuggestedRecipes;

    // Navigation components
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar suggestedToolbar;
    ActionBarDrawerToggle drawerToggle;

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

        // Connect navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        suggestedToolbar = findViewById(R.id.suggestedToolbar);

        // Connect database
        databaseHelper = new DatabaseHelper(this);

        // Create suggested recipe list
        suggestedRecipes = new ArrayList<>();

        // Connect adapter
        recipesAdapter = new RecipesApt(this, suggestedRecipes);

        listSuggestedRecipes.setAdapter(recipesAdapter);

        // Empty message
        listSuggestedRecipes.setEmptyView(txtNoSuggestedRecipes);

        // Create hamburger button
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                suggestedToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        // Current page
        navigationView.setCheckedItem(R.id.navSuggested);

        // Navigation menu
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            // Home
            if(id == R.id.navHome) {

                Intent intent = new Intent(SuggestedRecipesAct.this, HomePgActivity.class);

                startActivity(intent);

                drawerLayout.closeDrawer(GravityCompat.START);

                finish();

                return true;
            }

            // Pantry
            if(id == R.id.navPantry) {

                Intent intent = new Intent(SuggestedRecipesAct.this, PantryAct.class);

                startActivity(intent);

                drawerLayout.closeDrawer(GravityCompat.START);

                finish();

                return true;
            }

            // Recipes
            if(id == R.id.navRecipes) {

                Intent intent = new Intent(SuggestedRecipesAct.this, RecipesAct.class);

                startActivity(intent);

                drawerLayout.closeDrawer(GravityCompat.START);

                finish();

                return true;
            }

            // Suggested Recipes
            if(id == R.id.navSuggested) {

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }

            return false;
        });

        // Android back button and gesture
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

        // Load suggested recipes
        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSuggestedRecipes();
    }

    // Load Suggested Recipes
    private void loadSuggestedRecipes() {

        suggestedRecipes.clear();

        suggestedRecipes.addAll(databaseHelper.getSuggestedRecipes());

        recipesAdapter.notifyDataSetChanged();
    }
}