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
    private static final int DATABASE_VERSION = 3;

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
        String createModuleTable = "CREATE TABLE modules (" +
                MODULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_COEF + " INTEGER, " +
                COLUMN_CRED + " INTEGER)";
        db.execSQL(createModuleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS modules");
        onCreate(db);
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
