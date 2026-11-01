package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Sets up the bottom navigation bar for every main screen, so the navigation
 * code lives in one place instead of being copied into each Activity.
 */
public class NavigationHelper {

    /**
     * Highlights the current tab and makes the other tabs open their screens.
     * CLEAR_TOP + SINGLE_TOP reuse a screen that is already open instead of
     * stacking a new copy on top of it each time a tab is tapped.
     */
    public static void setup(Activity activity, int selectedItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Tapping the tab you are already on does nothing
            if (id == selectedItemId) {
                return true;
            }

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_suggested) {
                target = SuggestedRecipesActivity.class;
            } else if (id == R.id.nav_settings) {
                target = SettingsActivity.class;
            } else {
                return false;
            }

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
            return false; // keep the current tab highlighted
        });
    }
}