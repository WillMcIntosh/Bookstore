package com.willmcintosh.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.willmcintosh.bookstore.data.BookContract.BookEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id
                .fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity
                        .class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the store data
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows
        // when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // set up adapter and attach to list view
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        // set up on click listener
        bookListView.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

                // create new intent to go to EditorActivity
                Intent intent = new Intent(MainActivity.this, EditorActivity
                        .class);

                // Form the content URI that represents list item clicked on
                Uri currentPetUri = ContentUris.withAppendedId(BookEntry
                        .CONTENT_URI, id);

                // Set URI on the data field of intent
                intent.setData(currentPetUri);

                // Launch EditorActiviy
                startActivity(intent);
            }
        });

        // start loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

    }

    // update (decrement) stock value when user clicks sale button
    private void updateStock(int newStockValue, int rowId) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, newStockValue);
        String selection = BookEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(rowId)};
        Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, rowId);
        getContentResolver().update(uri, values, selection, selectionArgs);
    }

    /**
     * Insert dummy data to test functionality for part 1
     */
    private void insertBook() {
        // create ContentValues object
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Test Book");
        values.put(BookEntry.COLUMN_PRICE, 500);
        values.put(BookEntry.COLUMN_QUANTITY, 4);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Random House");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+1-555-555-1234");

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was
        // successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with
            // insertion.
            Toast.makeText(this, getString(R.string
                    .editor_update_book_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a
            // toast.
            Toast.makeText(this, getString(R.string
                    .editor_update_book_success), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Delete all entries from the books table
     */
    private void deleteBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI,
                null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from bookstore " +
                "database");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Define a projection that specifies the columns of interest
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE, BookEntry.COLUMN_QUANTITY};

        // execute ContentProvider's query method on a background thread
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // update BookCursorAdapter with new cursor
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // called when data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
