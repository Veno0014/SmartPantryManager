package com.example.smartpantrymanager;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class actsettings extends AppCompatActivity {

    // Settings components
    SwitchMaterial switchDarkMode;
    SwitchMaterial switchLowStock;
    Button btnSettingsBack;

    // Saves settings
    SharedPreferences settings;

    // Notification permission number
    private static final int NOTIFICATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.actsettings);

        // Connect Java to XML
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchLowStock = findViewById(R.id.switchLowStock);
        btnSettingsBack = findViewById(R.id.btnSettingsBack);

        // Connects settings
        settings = getSharedPreferences("app_settings", MODE_PRIVATE);

        // Gets saved settings
        boolean darkMode = settings.getBoolean("dark_mode", false);
        boolean lowStockNotifications = settings.getBoolean("low_stock_notifications", false);

        // Displays saved settings
        switchDarkMode.setChecked(darkMode);
        switchLowStock.setChecked(lowStockNotifications);

        // Changes Light or Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            settings.edit().putBoolean("dark_mode", isChecked).apply();

            if(isChecked) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            } else {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // activates low stock notifications
        switchLowStock.setOnCheckedChangeListener((buttonView, isChecked) -> {

            settings.edit().putBoolean("low_stock_notifications", isChecked).apply();

            if(isChecked) {

                // Requests permission from the user to activate notifications
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION);

                } else {

                    Toast.makeText(this, "Low stock notifications enabled", Toast.LENGTH_SHORT).show();
                }

            } else {

                Toast.makeText(this, "Low stock notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        btnSettingsBack.setOnClickListener(v -> finish());

        // Adjusting screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == NOTIFICATION_PERMISSION) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                settings.edit().putBoolean("low_stock_notifications", true).apply();

                switchLowStock.setChecked(true);

                Toast.makeText(this, "Notification permission allowed", Toast.LENGTH_SHORT).show();

            } else {

                settings.edit().putBoolean("low_stock_notifications", false).apply();

                switchLowStock.setChecked(false);

                Toast.makeText(this, "Notification permission is required for low stock alerts", Toast.LENGTH_LONG).show();
            }
        }
    }
}