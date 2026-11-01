package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and saves the user's settings using SharedPreferences,
 * so choices are remembered after the app is closed.
 */
public class AppSettings {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";

    /** Items expiring within this many days trigger the warning banner. */
    public static final int EXPIRY_WARNING_DAYS = 3;

    /** Alerts are ON by default. */
    public static boolean isExpiryAlertsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }
}