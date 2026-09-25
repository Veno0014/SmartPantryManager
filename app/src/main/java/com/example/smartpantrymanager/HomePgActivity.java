package com.example.smartpantrymanager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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

    // Database
    DatabaseHelper databaseHelper;

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

        // Connects database
        databaseHelper = new DatabaseHelper(this);

        // Get username
        String username = getIntent().getStringExtra("username");

        if(username != null && !username.isEmpty()) {
            topAppBar.setTitle(username + "'s Pantry");
            txtHello.setText("Hello, " + username);
        } else {
            topAppBar.setTitle("My Pantry");
            txtHello.setText("Hello, User");
        }

        // Adjusting screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // creating search bar funciton
        editSearch.setOnEditorActionListener((v, actionId, event) -> {

            boolean searchPressed = actionId == EditorInfo.IME_ACTION_SEARCH;

            boolean enterPressed = event != null &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getAction() == KeyEvent.ACTION_DOWN;

            if(searchPressed || enterPressed) {

                String searchText = editSearch.getText().toString().trim();

                if(searchText.isEmpty()) {
                    editSearch.setError("Search for ingredient or recipe name");
                    return true;
                }

                showSearchOptions(searchText);

                return true;
            }

            return false;
        });

        // Waitings for the pantry tab to be selected
        cardPantry.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, PantryAct.class);
            startActivity(intent);
        });

        // Waitings for the recipe tab to be selected
        cardRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, RecipesAct.class);
            startActivity(intent);
        });

        // Waiting for suggested tab to be pressed
        btnViewSuggested.setOnClickListener(v -> {
            Intent intent = new Intent(HomePgActivity.this, SuggestedRecipesAct.class);
            startActivity(intent);
        });

        // Plus button to add new ingredients or recipes
        btnAdd.setOnClickListener(v -> showAddMenu());

        // Menu options on Toolbar
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
                Intent intent = new Intent(HomePgActivity.this, actsettings.class);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Checks for low stock
        checkLowStock();
    }

    // Checks pantry for low stock
    private void checkLowStock() {

        SharedPreferences settings = getSharedPreferences("app_settings", MODE_PRIVATE);

        boolean notificationsEnabled = settings.getBoolean("low_stock_notifications", false);

        if(!notificationsEnabled) {
            return;
        }

        // Checks notification permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Cursor cursor = databaseHelper.getAllIngredients();

        int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME);
        int quantityIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY);
        int unitIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT);

        StringBuilder lowStockItems = new StringBuilder();

        while(cursor.moveToNext()) {

            String name = cursor.getString(nameIndex);
            double quantity = cursor.getDouble(quantityIndex);
            String unit = cursor.getString(unitIndex);

            // Checks if pantry stock is low
            boolean lowStock = false;

            String unitValue = unit.toLowerCase().trim();

            // Pieces are low when 3 or less
            if(unitValue.equals("piece") || unitValue.equals("pieces") || unitValue.equals("pc") || unitValue.equals("pcs") || unitValue.equals("unit") || unitValue.equals("units")) {

                if(quantity <= 3) {
                    lowStock = true;
                }
            }

            // Grams are low when 300g or less
            if(unitValue.equals("g") || unitValue.equals("gram") || unitValue.equals("grams")) {

                if(quantity <= 300) {
                    lowStock = true;
                }
            }

            // Kilograms are low when 0.3kg or less
            if(unitValue.equals("kg") || unitValue.equals("kilogram") || unitValue.equals("kilograms")) {

                if(quantity <= 0.3) {
                    lowStock = true;
                }
            }

            // Millilitres are low when 300ml or less
            if(unitValue.equals("ml") || unitValue.equals("millilitre") || unitValue.equals("millilitres") || unitValue.equals("milliliter") || unitValue.equals("milliliters")) {

                if(quantity <= 300) {
                    lowStock = true;
                }
            }

            // Litres are low when 0.3L or less
            if(unitValue.equals("l") || unitValue.equals("litre") || unitValue.equals("litres") || unitValue.equals("liter") || unitValue.equals("liters")) {

                if(quantity <= 0.3) {
                    lowStock = true;
                }
            }

            // Other units are low when 3 or less
            if(!unitValue.equals("piece") &&
                    !unitValue.equals("pieces") &&
                    !unitValue.equals("pc") &&
                    !unitValue.equals("pcs") &&
                    !unitValue.equals("unit") &&
                    !unitValue.equals("units") &&
                    !unitValue.equals("g") &&
                    !unitValue.equals("gram") &&
                    !unitValue.equals("grams") &&
                    !unitValue.equals("kg") &&
                    !unitValue.equals("kilogram") &&
                    !unitValue.equals("kilograms") &&
                    !unitValue.equals("ml") &&
                    !unitValue.equals("millilitre") &&
                    !unitValue.equals("millilitres") &&
                    !unitValue.equals("milliliter") &&
                    !unitValue.equals("milliliters") &&
                    !unitValue.equals("l") &&
                    !unitValue.equals("litre") &&
                    !unitValue.equals("litres") &&
                    !unitValue.equals("liter") &&
                    !unitValue.equals("liters")) {

                if(quantity <= 3) {
                    lowStock = true;
                }
            }

            // Adds low stock ingredients to notification
            if(lowStock) {

                if(lowStockItems.length() > 0) {
                    lowStockItems.append(", ");
                }

                lowStockItems.append(name).append(" (").append(quantity).append(" ").append(unit).append(")");
            }
        }

        cursor.close();

        if(lowStockItems.length() > 0) {

            String message = "Low stock: " + lowStockItems + ". Please buy more stock.";

            String previousMessage = settings.getString("last_low_stock_message", "");

            // Stops the same notification from showing repeatedly
            if(!message.equals(previousMessage)) {

                sendLowStockNotification(message);

                settings.edit().putString("last_low_stock_message", message).apply();
            }

        } else {

            // Clears previous notification when stock is no longer low
            settings.edit().putString("last_low_stock_message", "").apply();
        }
    }

    // Sends low stock notification
    private void sendLowStockNotification(String message) {

        String channelId = "low_stock_channel";

        // Creates notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Low Stock Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Notifications when pantry stock is low");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }

        // Checks notification permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Creates notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Smart Pantry - Low Stock")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1001, builder.build());
    }

    // Searching through ingredients or recipes
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

    // Creating an Add button
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