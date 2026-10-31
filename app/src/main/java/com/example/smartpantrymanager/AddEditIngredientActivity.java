package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * One screen used for both adding a new pantry item and editing an existing one.
 * If an ingredient id is passed in through the Intent, the screen is in edit mode.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    /** Key used to pass the id of the ingredient being edited through an Intent. */
    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    /** Units the user can choose from. */
    public static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "piece"};

    private EditText etName;
    private EditText etQuantity;
    private EditText etExpiry;
    private Spinner spinnerUnit;
    private DatabaseHelper dbHelper;

    private int ingredientId = -1; // -1 means "adding a new item"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        TextView tvScreenTitle = findViewById(R.id.tvScreenTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnClearDate = findViewById(R.id.btnClearDate);

        setupUnitSpinner();

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> saveIngredient());
        btnCancel.setOnClickListener(v -> finish());

        // Was an ingredient id sent with the Intent? If so, we are editing.
        ingredientId = getIntent().getIntExtra(EXTRA_INGREDIENT_ID, -1);
        if (ingredientId != -1) {
            tvScreenTitle.setText("Edit Ingredient");
            loadIngredient();
        } else {
            tvScreenTitle.setText("Add Ingredient");
        }
    }

    /** Fills the spinner with a "Select unit" prompt followed by the real units. */
    private void setupUnitSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Select unit");
        for (String unit : UNITS) {
            items.add(unit);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);
    }

    /** Edit mode: loads the saved ingredient from the database into the form. */
    private void loadIngredient() {
        Ingredient ingredient = dbHelper.getIngredient(ingredientId);
        if (ingredient == null) {
            Toast.makeText(this, "Ingredient not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName.setText(ingredient.getName());

        double quantity = ingredient.getQuantity();
        if (quantity == Math.floor(quantity)) {
            etQuantity.setText(String.valueOf((int) quantity));
        } else {
            etQuantity.setText(String.valueOf(quantity));
        }

        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equalsIgnoreCase(ingredient.getUnit())) {
                spinnerUnit.setSelection(i + 1); // +1 because of the "Select unit" prompt
                break;
            }
        }

        if (ingredient.getExpiryDate() != null) {
            etExpiry.setText(ingredient.getExpiryDate());
        }
    }

    /** Opens a date picker and writes the chosen date as yyyy-MM-dd. */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> etExpiry.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /** Validates the form. If everything is valid, saves to the database. */
    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        etName.setError(null);
        etQuantity.setError(null);
        boolean valid = true;

        // --- Validate name ---
        if (name.isEmpty()) {
            etName.setError("Please enter an ingredient name");
            valid = false;
        } else if (name.length() > 50) {
            etName.setError("Name must be 50 characters or fewer");
            valid = false;
        } else if (!name.matches("[\\p{L}][\\p{L} '\\-]*")) {
            etName.setError("Use letters only (spaces and hyphens are allowed)");
            valid = false;
        }

        // --- Validate quantity ---
        double quantity = 0;
        if (quantityText.isEmpty()) {
            etQuantity.setError("Please enter a quantity");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                } else if (quantity > 100000) {
                    etQuantity.setError("Quantity is too large");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        // --- Validate unit ---
        if (spinnerUnit.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a unit", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (!valid) {
            return; // stop here and let the user fix the errors
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        if (ingredientId == -1) {
            // CREATE
            Ingredient newIngredient = new Ingredient(0, name, quantity, unit, expiry);
            long result = dbHelper.addIngredient(newIngredient);
            if (result == -1) {
                Toast.makeText(this, "Could not save ingredient", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE
            Ingredient updated = new Ingredient(ingredientId, name, quantity, unit, expiry);
            dbHelper.updateIngredient(updated);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}