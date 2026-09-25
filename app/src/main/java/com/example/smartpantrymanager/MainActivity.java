package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // GUI
    EditText editUsername;
    EditText editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Connects java function is XML display
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Adjusting screen around system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Login button
        btnLogin.setOnClickListener(v -> GUI());
    }

    // checking login credentials
    private void GUI(){

        String name = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Checks username
        if(name.isEmpty()){
            editUsername.setError("Enter username");
            return;
        }

        // Checks password
        if(password.isEmpty()){
            editPassword.setError("Enter password");
            return;
        }

        // Checks login details
        if(name.equals("admin") && password.equals("admin123")){

            Toast.makeText(MainActivity.this, "Welcome Sir", Toast.LENGTH_SHORT).show();

            // Open Home page
            Intent intent = new Intent(MainActivity.this, HomePgActivity.class);

            // Send username to Home page
            intent.putExtra("username", name);

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(MainActivity.this, "Please check your logins and try again", Toast.LENGTH_SHORT).show();
        }
    }
}