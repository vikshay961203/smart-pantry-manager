package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Pre-loads the recipe collection into the database the first time it is created.
 * Ingredient names are stored in lower case and singular form to make matching easier.
 */
public class RecipeSeeder {

    /** Inserts all starter recipes. Called from DatabaseHelper.onCreate(). */
    public static void seed(SQLiteDatabase db) {

        addRecipe(db, "Scrambled Eggs on Toast",
                "1. Beat the eggs in a bowl.\n2. Melt the butter in a pan on low heat.\n"
                        + "3. Pour in the eggs and stir gently until just set.\n4. Serve on toasted bread.",
                ing("egg", 3, "piece"), ing("bread", 2, "piece"), ing("butter", 10, "g"));

        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt the butter in a pan.\n3. Pour in the eggs and cook until almost set.\n"
                        + "4. Add the cheese, fold the omelette in half and serve.",
                ing("egg", 3, "piece"), ing("cheese", 50, "g"), ing("butter", 10, "g"));

        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta until tender, then drain.\n2. Fry the chopped onion and garlic in olive oil.\n"
                        + "3. Add the chopped tomatoes and simmer for 15 minutes.\n4. Mix the sauce through the pasta.",
                ing("pasta", 200, "g"), ing("tomato", 4, "piece"), ing("onion", 1, "piece"),
                ing("garlic", 2, "piece"), ing("olive oil", 2, "tbsp"));

        addRecipe(db, "Egg Fried Rice",
                "1. Fry the chopped onion in a hot pan.\n2. Push it aside and scramble the eggs.\n"
                        + "3. Add the cooked rice and soy sauce.\n4. Stir-fry for 5 minutes and serve hot.",
                ing("rice", 200, "g"), ing("egg", 2, "piece"), ing("onion", 1, "piece"),
                ing("soy sauce", 2, "tbsp"));

        addRecipe(db, "Cheese Toastie",
                "1. Butter the outside of both slices of bread.\n2. Put the cheese between the slices.\n"
                        + "3. Toast in a pan for 3 minutes on each side until golden.",
                ing("bread", 2, "piece"), ing("cheese", 40, "g"), ing("butter", 10, "g"));

        addRecipe(db, "Banana Pancakes",
                "1. Mash the bananas in a bowl.\n2. Mix in the eggs, flour and milk until smooth.\n"
                        + "3. Pour spoonfuls into a hot pan.\n4. Cook until golden on both sides.",
                ing("banana", 2, "piece"), ing("egg", 2, "piece"), ing("flour", 100, "g"),
                ing("milk", 150, "ml"));

        addRecipe(db, "Classic Pancakes",
                "1. Whisk the flour, milk, egg and sugar into a smooth batter.\n2. Melt a little butter in a pan.\n"
                        + "3. Pour in some batter and cook until bubbles appear.\n4. Flip and cook the other side.",
                ing("flour", 150, "g"), ing("milk", 250, "ml"), ing("egg", 1, "piece"),
                ing("sugar", 1, "tbsp"), ing("butter", 20, "g"));

        addRecipe(db, "Vegetable Soup",
                "1. Peel and chop the potatoes, carrots and onion.\n2. Add them to a pot with the vegetable stock.\n"
                        + "3. Simmer for 25 minutes until soft.\n4. Blend or serve chunky.",
                ing("potato", 2, "piece"), ing("carrot", 2, "piece"), ing("onion", 1, "piece"),
                ing("vegetable stock", 500, "ml"));

        addRecipe(db, "Mashed Potatoes",
                "1. Peel and cut the potatoes.\n2. Boil for 20 minutes until soft, then drain.\n"
                        + "3. Add the butter and milk.\n4. Mash until smooth.",
                ing("potato", 4, "piece"), ing("butter", 30, "g"), ing("milk", 50, "ml"));

        addRecipe(db, "Garlic Butter Rice",
                "1. Cook the rice and keep it warm.\n2. Melt the butter in a pan and fry the crushed garlic.\n"
                        + "3. Stir the rice through the garlic butter and serve.",
                ing("rice", 200, "g"), ing("butter", 20, "g"), ing("garlic", 2, "piece"));

        addRecipe(db, "Tuna Pasta",
                "1. Boil the pasta and drain.\n2. Fry the chopped onion in olive oil.\n"
                        + "3. Add the tuna and warm through.\n4. Toss with the pasta.",
                ing("pasta", 200, "g"), ing("tuna", 1, "piece"), ing("onion", 1, "piece"),
                ing("olive oil", 1, "tbsp"));

        addRecipe(db, "Chicken Stir-Fry",
                "1. Slice the chicken, pepper and onion.\n2. Stir-fry the chicken until cooked through.\n"
                        + "3. Add the vegetables and soy sauce and cook for 5 minutes.\n4. Serve over cooked rice.",
                ing("chicken", 300, "g"), ing("bell pepper", 1, "piece"), ing("onion", 1, "piece"),
                ing("soy sauce", 2, "tbsp"), ing("rice", 150, "g"));

        addRecipe(db, "Baked Potato with Cheese",
                "1. Heat the oven to 200 C.\n2. Prick the potatoes and bake for 1 hour.\n"
                        + "3. Cut them open and add the butter.\n4. Top with grated cheese and bake 5 more minutes.",
                ing("potato", 2, "piece"), ing("cheese", 60, "g"), ing("butter", 15, "g"));

        addRecipe(db, "Fried Egg Sandwich",
                "1. Melt the butter in a pan and fry the eggs.\n2. Toast the bread.\n"
                        + "3. Place the eggs between the slices and serve.",
                ing("egg", 2, "piece"), ing("bread", 2, "piece"), ing("butter", 10, "g"));

        addRecipe(db, "Tomato Soup",
                "1. Fry the chopped onion and garlic.\n2. Add the chopped tomatoes and stock.\n"
                        + "3. Simmer for 20 minutes.\n4. Blend until smooth.",
                ing("tomato", 6, "piece"), ing("onion", 1, "piece"), ing("garlic", 2, "piece"),
                ing("vegetable stock", 500, "ml"));

        addRecipe(db, "Creamy Porridge",
                "1. Put the oats and milk in a pot.\n2. Cook on medium heat, stirring, for 5 minutes.\n"
                        + "3. Stir in the sugar and serve warm.",
                ing("oats", 80, "g"), ing("milk", 300, "ml"), ing("sugar", 1, "tbsp"));

        addRecipe(db, "Banana Smoothie",
                "1. Peel the bananas and break them into pieces.\n2. Blend with the milk and honey until smooth.\n"
                        + "3. Pour into a glass and serve cold.",
                ing("banana", 2, "piece"), ing("milk", 250, "ml"), ing("honey", 1, "tbsp"));

        addRecipe(db, "French Toast",
                "1. Whisk the eggs, milk and sugar in a shallow dish.\n2. Dip each slice of bread in the mixture.\n"
                        + "3. Fry in butter until golden on both sides.",
                ing("bread", 4, "piece"), ing("egg", 2, "piece"), ing("milk", 100, "ml"),
                ing("butter", 15, "g"), ing("sugar", 1, "tsp"));

        addRecipe(db, "Chicken and Rice",
                "1. Fry the chopped onion and garlic.\n2. Add the chicken pieces and brown them.\n"
                        + "3. Add the rice and enough water to cover.\n4. Cover and simmer for 20 minutes.",
                ing("chicken", 300, "g"), ing("rice", 200, "g"), ing("onion", 1, "piece"),
                ing("garlic", 2, "piece"));

        addRecipe(db, "Vegetable Omelette",
                "1. Chop the tomato and onion.\n2. Beat the eggs and pour into a hot pan.\n"
                        + "3. Add the vegetables and cheese.\n4. Fold the omelette and serve.",
                ing("egg", 3, "piece"), ing("tomato", 1, "piece"), ing("onion", 1, "piece"),
                ing("cheese", 30, "g"));
    }

    /** Shorthand for describing one ingredient line: name, quantity, unit. */
    private static Object[] ing(String name, double quantity, String unit) {
        return new Object[]{name, quantity, unit};
    }

    /** Inserts one recipe and then all of its ingredient lines. */
    private static void addRecipe(SQLiteDatabase db, String name, String steps, Object[]... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_RECIPE_NAME, name);
        recipeValues.put(DatabaseHelper.COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

        for (Object[] item : ingredients) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            values.put(DatabaseHelper.COL_RI_NAME, (String) item[0]);
            values.put(DatabaseHelper.COL_RI_QUANTITY, (Double) item[1]);
            values.put(DatabaseHelper.COL_RI_UNIT, (String) item[2]);
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
        }
    }
}