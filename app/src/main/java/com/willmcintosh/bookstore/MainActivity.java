package com.willmcintosh.bookstore;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.willmcintosh.bookstore.data.BookContract.BookEntry;
import com.willmcintosh.bookstore.data.BookDbHelper;


public class MainActivity extends AppCompatActivity {

    /**
     * database helper
     */
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // For accessing database
        mDbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Method to display number of books in a textview
     * Also show column headers and contents via a cursor object
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE };

        // query the books table
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.text_view_book);

        try {
            // Create a header row in the text view
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(BookEntry._ID + " | " +
                    BookEntry.COLUMN_PRODUCT_NAME + " | " +
                    BookEntry.COLUMN_PRICE + " | " +
                    BookEntry.COLUMN_QUANTITY + " | " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " | " +
                    BookEntry.COLUMN_SUPPLIER_PHONE + "\n");

            // get index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // iterate through returned rows and add information to display
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);
                // Display current values in TextView
                displayView.append(("\n" + currentID + " | " +
                        currentProductName + " | " +
                        currentPrice + " | " +
                        currentQuantity + " | " +
                        currentSupplier + " | " +
                        currentPhone));
            }

        } finally {
            // close the cursor object always
            cursor.close();
        }

    }

    /**
     * Insert dummy data to test functionality for part 1
     */
    private void insertBook() {
        // get db in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // create ContentValues object
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Test Book");
        values.put(BookEntry.COLUMN_PRICE, 500);
        values.put(BookEntry.COLUMN_QUANTITY, 4);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Random House");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+1-888-555-1234");

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with importing dummy data", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Delete all entries from the books table
     */
    private void deleteBooks() {
        //  get db in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // delete all rows
        long rowsDeleted = db.delete(BookEntry.TABLE_NAME, "1", null);

        // Show a toast message for number of rows deleted
        Toast.makeText(this, "Successfully deleted " + rowsDeleted + " rows.", Toast.LENGTH_SHORT)
                .show();
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteBooks();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
