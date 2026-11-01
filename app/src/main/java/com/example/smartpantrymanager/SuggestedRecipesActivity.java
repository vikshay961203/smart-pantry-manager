package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Shows only the recipes the user can make right now.
 * It loads the pantry and all recipes from the database and passes them
 * through RecipeMatcher, which applies the strict-matching rule.
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerRecipes;
    private TextView tvEmpty;
    private TextView tvCount;
    private RecipeAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvCount = findViewById(R.id.tvCount);

        adapter = new RecipeAdapter(this);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecipes.setAdapter(adapter);

        // Bottom navigation: Suggested is the current tab
        NavigationHelper.setup(this, R.id.nav_suggested);
    }

    /** Recalculate every time the screen is shown, so it always matches the current pantry. */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Ingredient> pantry = dbHelper.getAllIngredients();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        // The strict-matching rule is applied here
        List<Recipe> suggested = RecipeMatcher.getSuggestedRecipes(allRecipes, pantry);
        adapter.setRecipes(suggested);

        if (suggested.isEmpty()) {
            // Friendly message instead of a blank screen
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);
            tvCount.setText("");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
            tvCount.setText(suggested.size()
                    + (suggested.size() == 1 ? " recipe" : " recipes")
                    + " you can make right now");
        }
    }

    /** A recipe row was tapped: open the detail screen and pass the recipe id. */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}