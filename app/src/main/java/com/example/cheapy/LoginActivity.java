package com.example.cheapy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.cheapy.API.UserAPI;
import com.example.cheapy.Home_page.HomePageActivity;
import com.example.cheapy.Sign_up.CityActivity;
import com.example.cheapy.Sign_up.SignUpActivity;
import com.example.cheapy.databinding.ActivityLoginBinding;
import com.example.cheapy.isReturn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private ActivityLoginBinding binding;

    public static Context context;
    private UserAPI userAPI;
    private String userToken;
    private String activeUserName;
    private String newToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
             newToken = instanceIdResult.getToken();
             Log.d("msg - login", newToken);
        });

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Retrieve the initial value of the preference and set the theme
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNightMode = sharedPreferences.getBoolean("dark_mode", false);
        changeTheme(isNightMode);
        // Register the shared preference change listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        userAPI = new UserAPI();
        // Initialize the ViewModel
        // Observe changes to the token value
        userAPI.getTokenLiveData().observe(this, token -> {
            if (token != null) {
                userToken = token;
            }
        });

        userAPI.getActiveUserName().observe(this, username -> {
            if (username != null) {
                activeUserName = username;
            }
        });
        binding.signup.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        binding.dnthave.setOnClickListener(view -> {
            // Handle the click event here
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        binding.loginButton.setOnClickListener(view -> {
            // Get username and password from the UI
            String username = binding.editTextUsername.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            if (username.isEmpty()) {
                // Show error message
                binding.editTextUsername.setError("Username is empty");
            } else if (password.isEmpty()) {
                // Show error message
                binding.editTextPassword.setError("Password is empty");
            } else {
                try {
                    Log.d("Login:", userAPI.toString());
                    Log.d("LoginToken:", newToken);
                    userAPI.signIn(username, password,newToken, callback -> {
                        Log.d("Login:", String.valueOf(callback));
                        if (callback == 200) {
                            Log.d("Login:", String.valueOf(callback));
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(Cheapy.context);
                            String savedToken = sharedPreferences2.getString("auth_token", null); // Default is null if not found
                            intent.putExtra("token", savedToken);
                            intent.putExtra("activeUserName", activeUserName);
                            isReturn.getInstance().setIsReturn(false);
                            startActivity(intent);
                        } else if (callback == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "details not correct", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "error ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Log.d("Login:", e.toString());
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("dark_mode")) {
            boolean isNightMode = sharedPreferences.getBoolean(key, false);
            changeTheme(isNightMode);
        }
    }
    private void changeTheme(boolean isNightMode) {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the shared preference change listener
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        userAPI.setUrl();
    }

}