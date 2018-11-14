package com.willmcintosh.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "bookstore.db";

    /**
     * Database version. If you change the database schema, you must
     * increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the
        // books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookContract
                .BookEntry.TABLE_NAME + " " + "(" + BookContract.BookEntry
                ._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BookContract
                .BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                BookContract.BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, " +
                BookContract.BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL "
                + "DEFAULT 0, " + BookContract.BookEntry.COLUMN_SUPPLIER_NAME
                + " TEXT NOT NULL, " + BookContract.BookEntry
                .COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be
        // done here.
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookContract
                .BookEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
