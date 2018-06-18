package com.example.android.p9.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.p9.data.BookCotracts.booksEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + booksEntry.TABLE_NAME + " ("
                + booksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + booksEntry.COLUMN_BOOKS_NAME + " TEXT NOT NULL, "
                + booksEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + booksEntry.COLUMN_QUANTITY + " INTEGER, "
                + booksEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + booksEntry.COLUMN_SUPER_PHONE + " INTEGER, "
                + booksEntry.COLUMN_SUPP_EMAIL + " TEXT);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
