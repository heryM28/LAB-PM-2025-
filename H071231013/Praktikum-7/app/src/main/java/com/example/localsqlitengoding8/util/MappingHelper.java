package com.example.localsqlitengoding8.util;

import android.database.Cursor;
import com.example.localsqlitengoding8.db.DatabaseContract;
import com.example.localsqlitengoding8.model.Student;
import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<Student> mapCursorToList(Cursor cursor) {
        ArrayList<Student> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.StudentColumns._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.StudentColumns.TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.StudentColumns.DESCRIPTION));
            String created = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.StudentColumns.CREATED_AT));
            list.add(new Student(id, title, desc, created));
        }
        return list;
    }
}
