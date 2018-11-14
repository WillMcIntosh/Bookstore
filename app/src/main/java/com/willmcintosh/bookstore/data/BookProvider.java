package com.willmcintosh.bookstore.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.willmcintosh.bookstore.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * Database helper object
     */
    private BookDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher
            .NO_MATCH);

    // Static initializer. This is run the first time anything is called from
    // this class.
    static {

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract
                .PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract
                .PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Perform the query for the given URI. Use the given projection,
     * selection, selection
     * arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Instantiate cursor object
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris
                        .parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI"
                        + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not " +
                        "supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the
     * new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // check that name is not null
        String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name.");
        }

        // check price
        Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Book requires a valid price.");
        }

        // check quantity
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_PRICE);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Book requires a valid " +
                    "quantity.");
        }

        // check that supplier name is not null
        String supplierName = values.getAsString(BookEntry
                .COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a supplier " +
                    "name" + ".");
        }

        // check that phone number is valid
        String phone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
        if (phone == null || !BookEntry.validPhone(phone)) {
            throw new IllegalArgumentException("Book requires a valid " +
                    "supplier phone");
        }

        // get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert a new book into the database with the given ContentValues
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with
     * the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris
                        .parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update unknown URI" +
                        " " + uri);
        }
    }

    /**
     * Update books in the database with the given content values
     *
     * @return number of rows successfully updated
     */
    private int updateBook(Uri uri, ContentValues values, String selection,
                           String[] selectionArgs) {
        // validate data if present
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name.");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            // check price
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Book requires a valid price.");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            // check quantity
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Book requires a valid " +
                        "quantity.");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            // check that supplier name is not null
            String supplierName = values.getAsString(BookEntry
                    .COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a supplier " +
                        "name" + ".");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) {
            // check that phone number is valid
            String phone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
            if (phone == null || !BookEntry.validPhone(phone)) {
                throw new IllegalArgumentException("Book requires a valid " +
                        "supplier phone");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(BookEntry.TABLE_NAME, values, selection,
                selectionArgs);

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                return database.delete(BookEntry.TABLE_NAME, selection,
                        selectionArgs);
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BookEntry.TABLE_NAME, selection,
                        selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
