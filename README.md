# Smart Pantry Manager

## Description
Smart Pantry Manager is an Android application written in Java that helps users reduce food waste. The user records the ingredients they currently have at home (their pantry), and the app suggests only the recipes they can cook right now using those ingredients, with no shopping trip required.

The core rule of the app is **strict matching**: a recipe is only suggested if every single ingredient it needs is in the pantry, in at least the required quantity. A recipe missing even one ingredient is never shown.

## Features
- **Pantry management:** add, edit and delete pantry items (name, quantity, unit and an optional expiry date)
- **Pantry list screen:** a RecyclerView with a custom adapter, bound to the database
- **Recipe collection:** 20 recipes pre-loaded into the database on first run
- **Suggested Recipes screen:** shows only the recipes that pass the strict-matching rule, with a friendly message when nothing matches
- **Recipe Detail screen:** the full ingredient list and method for a chosen recipe
- **Settings screen:** a toggle for expiring-soon alerts, saved between sessions
- **Expiry warnings:** a banner on the pantry screen, plus red (expired) and orange (expiring soon) dates on the list
- **Input validation:** on the Add/Edit form (name, quantity and unit are checked before saving)
- **Bottom navigation:** Pantry, Suggested and Settings tabs

## How the Strict Matching Works
The matching logic lives in `RecipeMatcher.java`. For each recipe, every required ingredient must pass two checks: it must be in the pantry, and the pantry must hold at least the required amount. If any ingredient fails either check, the whole recipe is excluded.

To cope with real-world messiness the matcher also:
- ignores upper/lower case and extra spaces
- treats singular and plural names as the same (for example "tomato" and "tomatoes")
- converts units before comparing (for example 1 kg in the pantry satisfies a recipe that needs 500 g)
- adds up duplicate pantry entries of the same ingredient

## Database Choice
This app uses **SQLite** (via `SQLiteOpenHelper`), stored locally on the device.

**Why SQLite:** it works offline, needs no accounts or backend server, and keeps the user's pantry data private on their own phone. It supports full CRUD (create, read, update, delete), and the data persists after the app is closed and reopened.

**Tables:**
- `pantry`: the ingredients the user currently has
- `recipes`: the pre-loaded recipes (name and method)
- `recipe_ingredients`: the ingredient lines each recipe requires, linked to `recipes` by `recipe_id`

## Project Structure
| File | Purpose |
|---|---|
| `MainActivity` | Pantry list screen |
| `AddEditIngredientActivity` | Add and edit form with validation |
| `SuggestedRecipesActivity` | Recipes the user can make right now |
| `RecipeDetailActivity` | Full recipe ingredients and method |
| `SettingsActivity` | Expiring-soon alert toggle |
| `DatabaseHelper` | Creates the database and handles all queries |
| `RecipeSeeder` | Loads the 20 starter recipes |
| `RecipeMatcher` | The strict-matching logic |
| `PantryAdapter`, `RecipeAdapter` | RecyclerView adapters |
| `NavigationHelper` | Shared bottom navigation setup |
| `AppSettings` | Saves settings with SharedPreferences |
| `Ingredient`, `Recipe`, `RecipeIngredient` | Model classes |

## Setup and Run Instructions
1. Install Android Studio.
2. Clone this repository: `git clone https://github.com/vikshay961203/smart-pantry-manager.git`
3. In Android Studio, choose **File > Open** and select the cloned folder.
4. Wait for the Gradle sync to finish.
5. Create an emulator in **Device Manager**, or connect an Android phone with USB debugging enabled.
6. Click the green **Run** button.

## Testing the Strict-Matching Rule
1. Open **Suggested** with an empty pantry. A message says no recipes match yet.
2. Add `Egg` (3 piece) and `Bread` (2 piece). Nothing is suggested yet, because "Scrambled Eggs on Toast" also needs butter.
3. Add `Butter` (10 g). "Scrambled Eggs on Toast" and "Fried Egg Sandwich" now appear.
4. Delete the butter. Both recipes disappear again.

## Technologies
- Java
- Android Studio
- SQLite
- RecyclerView, Intents, SharedPreferences, Material Components

## Limitations
- The app has no location, maps or GPS features (out of scope for this project).
- Recipes ~~~~are pre-loaded and cannot be added by the user in this version.

## Author
Vikshay Jagesur