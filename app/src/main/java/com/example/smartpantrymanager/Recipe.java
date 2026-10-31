package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe with its required ingredients and preparation steps.
 */
public class Recipe {

    private int id;
    private String name;
    private String steps;
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(int id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }
}