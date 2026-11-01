package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows one recipe in full: its name, ingredient list and preparation steps.
 * The recipe id arrives through the Intent that started this screen.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    /** Key used to pass the recipe id through an Intent. */
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Read the recipe id that was sent with the Intent
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(recipe.getName());
        tvSteps.setText(recipe.getSteps());

        // Build the ingredient list, one bullet per line
        StringBuilder builder = new StringBuilder();
        for (RecipeIngredient item : recipe.getIngredients()) {
            builder.append("\u2022 ")
                    .append(formatQuantity(item.getQuantity()))
                    .append(" ")
                    .append(item.getUnit())
                    .append(" ")
                    .append(item.getName())
                    .append("\n");
        }
        tvIngredients.setText(builder.toString().trim());
    }

    /** Shows 200.0 as "200" and 1.5 as "1.5". */
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((int) quantity);
        }
        return String.valueOf(quantity);
    }
}