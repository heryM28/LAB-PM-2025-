package com.example.localsqlitengoding8.db;

import android.provider.BaseColumns;

public final class DatabaseContract {
    private DatabaseContract() {}

    public static class StudentColumns implements BaseColumns {
        public static final String TABLE_NAME = "student";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CREATED_AT = "created_at";
    }
}
