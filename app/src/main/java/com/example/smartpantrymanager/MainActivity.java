package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The Pantry List screen. Shows every ingredient stored in the database,
 * lets the user add, edit and delete items.
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    private RecyclerView recyclerPantry;
    private TextView tvEmpty;
    private TextView tvExpiryAlert;
    private PantryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvExpiryAlert = findViewById(R.id.tvExpiryAlert);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        // Connect the RecyclerView to our custom adapter
        adapter = new PantryAdapter(this);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        // The + button opens the Add/Edit screen in "add" mode (no id sent)
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        // Bottom navigation: Pantry is the current tab
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_suggested) {
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
                return false; // keep the Pantry tab highlighted on this screen
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return false;
            }
            return true;
        });
    }

    /**
     * onResume runs every time this screen comes back into view, for example after
     * the user saves an item on the Add/Edit screen, so the list is always up to date.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    /** Reads all ingredients from the database and shows them (or the empty message). */
    private void loadPantry() {
        List<Ingredient> ingredients = dbHelper.getAllIngredients();
        adapter.setIngredients(ingredients);

        if (ingredients.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerPantry.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerPantry.setVisibility(View.VISIBLE);
        }

        updateExpiryAlert(ingredients);
    }

    /** Shows the warning banner if alerts are on and some items expire soon. */
    private void updateExpiryAlert(List<Ingredient> ingredients) {
        if (!AppSettings.isExpiryAlertsEnabled(this)) {
            tvExpiryAlert.setVisibility(View.GONE);
            return;
        }

        int count = countExpiringSoon(ingredients);
        if (count == 0) {
            tvExpiryAlert.setVisibility(View.GONE);
        } else {
            tvExpiryAlert.setText("Warning: " + count
                    + (count == 1 ? " item expires" : " items expire")
                    + " within " + AppSettings.EXPIRY_WARNING_DAYS + " days (or already expired)");
            tvExpiryAlert.setVisibility(View.VISIBLE);
        }
    }

    /** Counts items whose expiry date is within the warning period, including expired ones. */
    private int countExpiringSoon(List<Ingredient> ingredients) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);

        // The last moment of the final warning day
        Calendar limit = Calendar.getInstance();
        limit.add(Calendar.DAY_OF_YEAR, AppSettings.EXPIRY_WARNING_DAYS);
        limit.set(Calendar.HOUR_OF_DAY, 23);
        limit.set(Calendar.MINUTE, 59);
        limit.set(Calendar.SECOND, 59);
        Date limitDate = limit.getTime();

        int count = 0;
        for (Ingredient item : ingredients) {
            String expiry = item.getExpiryDate();
            if (expiry == null || expiry.trim().isEmpty()) {
                continue; // no expiry date set
            }
            try {
                Date expiryDate = format.parse(expiry.trim());
                if (expiryDate != null && !expiryDate.after(limitDate)) {
                    count++;
                }
            } catch (ParseException e) {
                // ignore items with an unreadable date
            }
        }
        return count;
    }

    /** A row was tapped: open the Add/Edit screen in "edit" mode, passing the item id. */
    @Override
    public void onItemClick(Ingredient ingredient) {
        Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
        startActivity(intent);
    }

    /** The bin icon was tapped: ask for confirmation, then delete. */
    @Override
    public void onDeleteClick(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + ingredient.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteIngredient(ingredient.getId());
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}