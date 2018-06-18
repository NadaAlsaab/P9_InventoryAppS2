package com.example.android.p9.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.p9.data.BookCotracts.booksEntry;

import java.security.Provider;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookCotracts.CONTENT_AUTHORITY, BookCotracts.PATH, BOOKS);
        sUriMatcher.addURI(BookCotracts.CONTENT_AUTHORITY, BookCotracts.PATH + "/#", BOOK_ID);
    }

    private BookDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int matcher = sUriMatcher.match(uri);

        if (matcher == BOOKS) {
            cursor = db.query(booksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        } else if (matcher == BOOK_ID) {
            selection = booksEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            cursor = db.query(booksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        } else {
            throw new IllegalArgumentException("Querying this URI has failed");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return booksEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return booksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case BOOKS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion failed");
        }
    }
    private Uri insertItem(Uri uri, ContentValues contentValues) {
        String itemName = contentValues.getAsString(booksEntry.COLUMN_BOOKS_NAME);
        String suppName = contentValues.getAsString(booksEntry.COLUMN_SUPPLIER);
        String price = contentValues.getAsString(booksEntry.COLUMN_PRICE);
        Long insertRow;


        if (itemName == null) {
            throw new IllegalArgumentException("You must provide a name for this item");
        } else if (suppName == null) {
            throw new IllegalArgumentException("You must provide a supplier name for this item");

        } else if (price == null) {
            throw new IllegalArgumentException("You must provide a price for this item");

        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        insertRow = db.insert(booksEntry.TABLE_NAME, null, contentValues);

        if (insertRow == -1) {
            Log.e(LOG_TAG, "Row insertion failed " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, insertRow);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int matcher = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (matcher) {
            case BOOKS:
                rowsDeleted = db.delete(booksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = booksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(booksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case BOOKS:
                return updateItem(uri, values, selection, selectionArgs);

            case BOOK_ID:
                selection = booksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Can't perform update " + uri);
        }
    }
    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] sArgs) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Boolean check = contentValues.containsKey(booksEntry.COLUMN_BOOKS_NAME);

        if (check) {
            String name = contentValues.getAsString(booksEntry.COLUMN_BOOKS_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a description");
            }
        }
        check = contentValues.containsKey(booksEntry.COLUMN_SUPPLIER);
        if (check) {
            String supp = contentValues.getAsString(booksEntry.COLUMN_SUPPLIER);
            if (supp == null) {
                throw new IllegalArgumentException("Item requires a supplier");
            }
        }

        check = contentValues.containsKey(booksEntry.COLUMN_PRICE);
        if (check) {
            Integer price = contentValues.getAsInteger(booksEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Item requires a price");
            }
        }

        check = contentValues.containsKey(booksEntry.COLUMN_QUANTITY);
        if (check) {
            Integer quantity = contentValues.getAsInteger(booksEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Item requires a quantity");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }


        int rowsUpdated = database.update(booksEntry.TABLE_NAME, contentValues, selection, sArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }

}
