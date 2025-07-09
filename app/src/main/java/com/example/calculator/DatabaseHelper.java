package com.example.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 2; // Bumped to force onUpgrade

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // Module table
    public static final String MODULE_ID = "module_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COEF = "coef";
    public static final String COLUMN_CRED = "cred";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);

        String createModuleTable = "CREATE TABLE modules (" +
                MODULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_COEF + " INTEGER, " +
                COLUMN_CRED + " INTEGER)";
        db.execSQL(createModuleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS modules");
        onCreate(db);
    }

    // User methods
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean userExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void displayAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        if (cursor.getCount() == 0) {
            Log.d("DB_USERS", "No users found.");
        } else {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String email = cursor.getString(1);
                String password = cursor.getString(2);
                Log.d("DB_USERS", "ID: " + id + ", Email: " + email + ", Password: " + password);
            }
        }

        cursor.close();
    }

    // Module methods
    public void insertModule(Module module) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, module.getName());
        values.put(COLUMN_COEF, module.getCoef());
        values.put(COLUMN_CRED, module.getCred());
        long result = db.insert("modules", null, values);
        if (result == -1) {
            Log.e("DB_modules", "Insert failed");
        }
        db.close();
    }

    public List<Module> getAllModules() {
        List<Module> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM modules", null);
        if (cursor.getCount() == 0) {
            Log.d("DB_modules", "No modules found.");
        } else {
            while (cursor.moveToNext()) {
                Module m = new Module();
                m.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                m.setCoef(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COEF)));
                m.setCred(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CRED)));
                modules.add(m);
            }
        }
        cursor.close();
        db.close();
        return modules;
    }

    public void clearModules() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete("modules", null, null);
        } catch (Exception e) {
            Log.e("DB_modules", "Error clearing modules", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}
