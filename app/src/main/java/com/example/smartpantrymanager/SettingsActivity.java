package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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
        NavigationHelper.setup(this, R.id.nav_settings);
    }
}