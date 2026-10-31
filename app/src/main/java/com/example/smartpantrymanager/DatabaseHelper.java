package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;





import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates and manages the local SQLite database for Smart Pantry Manager.
 *
 * Tables:
 *  - pantry:             the ingredients the user currently has
 *  - recipes:            the pre-loaded recipes (name and method)
 *  - recipe_ingredients: the ingredients each recipe requires
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Make SQLite enforce foreign keys
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PANTRY_NAME + " TEXT NOT NULL, "
                + COL_PANTRY_QUANTITY + " REAL NOT NULL, "
                + COL_PANTRY_UNIT + " TEXT NOT NULL, "
                + COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_NAME + " TEXT NOT NULL, "
                + COL_RECIPE_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_RI_NAME + " TEXT NOT NULL, "
                + COL_RI_QUANTITY + " REAL NOT NULL, "
                + COL_RI_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }


    // ---------------------------------------------------------------
    // PANTRY CRUD
    // ---------------------------------------------------------------

    /** CREATE: adds a new pantry item and returns its new row id (-1 on failure). */
    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, ingredient.getName());
        values.put(COL_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COL_PANTRY_UNIT, ingredient.getUnit());
        values.put(COL_PANTRY_EXPIRY, ingredient.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    /** READ: returns every pantry item, sorted by name. */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_PANTRY_NAME + " COLLATE NOCASE ASC");
        while (cursor.moveToNext()) {
            list.add(cursorToIngredient(cursor));
        }
        cursor.close();
        return list;
    }

    /** READ: returns one pantry item by id, or null if it does not exist. */
    public Ingredient getIngredient(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Ingredient ingredient = null;
        if (cursor.moveToFirst()) {
            ingredient = cursorToIngredient(cursor);
        }
        cursor.close();
        return ingredient;
    }

    /** UPDATE: saves changes to an existing pantry item. Returns rows changed. */
    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, ingredient.getName());
        values.put(COL_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COL_PANTRY_UNIT, ingredient.getUnit());
        values.put(COL_PANTRY_EXPIRY, ingredient.getExpiryDate());
        return db.update(TABLE_PANTRY, values, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())});
    }

    /** DELETE: removes a pantry item. Returns rows deleted. */
    public int deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /** Helper: converts the current cursor row into an Ingredient object. */
    private Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
        ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)));
        ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY)));
        ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)));
        ingredient.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)));
        return ingredient;
    }
}