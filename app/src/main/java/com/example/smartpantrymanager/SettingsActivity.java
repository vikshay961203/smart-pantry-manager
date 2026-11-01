package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The Settings screen. Lets the user turn expiring-soon alerts on or off.
 * The choice is stored with SharedPreferences (see AppSettings).
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show the saved choice, then save every change straight away
        SwitchCompat switchAlerts = findViewById(R.id.switchAlerts);
        switchAlerts.setChecked(AppSettings.isExpiryAlertsEnabled(this));
        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setExpiryAlertsEnabled(SettingsActivity.this, isChecked));

        // Bottom navigation: Settings is the current tab
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_pantry) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return false;
            } else if (item.getItemId() == R.id.nav_suggested) {
                startActivity(new Intent(SettingsActivity.this, SuggestedRecipesActivity.class));
                return false;
            }
            return true;
        });
    }
}