package com.example.localsqlitengoding8.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + DatabaseContract.StudentColumns.TABLE_NAME + " (" +
                    DatabaseContract.StudentColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseContract.StudentColumns.TITLE + " TEXT NOT NULL," +
                    DatabaseContract.StudentColumns.DESCRIPTION + " TEXT NOT NULL," +
                    DatabaseContract.StudentColumns.CREATED_AT + " TEXT NOT NULL" +
                    ")";

    public DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.StudentColumns.TABLE_NAME);
        onCreate(db);
    }
}
