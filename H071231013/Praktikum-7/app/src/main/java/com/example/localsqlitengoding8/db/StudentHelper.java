package com.example.localsqlitengoding8.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StudentHelper {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public StudentHelper(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        if (database != null && database.isOpen()) database.close();
    }

    public Cursor getAll() {
        return database.query(
                DatabaseContract.StudentColumns.TABLE_NAME,
                null, null, null, null, null,
                DatabaseContract.StudentColumns._ID + " DESC"
        );
    }

    public long insert(ContentValues values) {
        return database.insert(DatabaseContract.StudentColumns.TABLE_NAME, null, values);
    }

    public int update(int id, ContentValues values) {
        return database.update(
                DatabaseContract.StudentColumns.TABLE_NAME,
                values,
                DatabaseContract.StudentColumns._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int delete(int id) {
        return database.delete(
                DatabaseContract.StudentColumns.TABLE_NAME,
                DatabaseContract.StudentColumns._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}
