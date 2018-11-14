package com.willmcintosh.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;



import com.willmcintosh.bookstore.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    /** EditText field to enter the book's name */
    private EditText mNameEditText;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;

    /** EditText field to enter the book's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the supplier's name */
    private EditText mSupplierEditText;

    /** EditText field to enter the supplier's phone */
    private EditText mPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // get intent
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // check if URI was passed in
        if (mCurrentBookUri == null) {
            setTitle(R.string.editor_activity_title_new_book);
        } else {
            setTitle(R.string.editor_activity_title_edit_book);

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null,
                    this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mPhoneEditText = findViewById(R.id.edit_supplier_phone);


    }

    /**
     * Get user input from editor and save book into database
     */
    private void saveBook() {
        // Read from input fields
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        int price = 0;
        if (!priceString.equals("")) {
            price = Integer.parseInt(priceString);
        }

        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = 0;
        if (!quantityString.equals("")) {
            quantity = Integer.parseInt(quantityString);
        }
        String supplierNameString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        // check if this is a new book with no data entered
        if (mCurrentBookUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty
                (quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(phoneString)) {

            return;
        }

        // check if user forgot to fill out a field
        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty
                (quantityString) || TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(phoneString)) {

            Toast.makeText(this, getString(R.string
                            .editor_fill_all_fields),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // create ContentValues object
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, phoneString);

        // Determine if this is a new or existing book
        if (mCurrentBookUri == null) {
            // This is a new book
            // Insert a new book into the provider, returning the content URI for
            // the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string
                                .editor_update_book_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // this is an existing book
            int rowsAffected = getContentResolver().update(mCurrentBookUri,
                    values, null, null);

            if (rowsAffected == 0) {
                // possible issue with update
                Toast.makeText(this, getString(R.string
                        .editor_update_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // the update was successful
                Toast.makeText(this, getString(R.string
                        .editor_update_book_success), Toast.LENGTH_SHORT).show
                        ();
            }
        }

        // Exit activity
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveBook();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // find columns of book attributes to display
            int titleColumnIndex = cursor.getColumnIndex(BookEntry
                    .COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry
                    .COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry
                    .COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry
                    .COLUMN_SUPPLIER_PHONE);

            // read book attributes from current book
            String bookTitle = cursor.getString(titleColumnIndex);
            int bookPrice = cursor.getInt(priceColumnIndex);
            String priceString = Integer.toString(bookPrice);
            int bookQuantity = cursor.getInt(quantityColumnIndex);
            String quantString = Integer.toString(bookQuantity);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(phoneColumnIndex);

            // update textview with attributes from current book
            mNameEditText.setText(bookTitle);
            mPriceEditText.setText(priceString);
            mQuantityEditText.setText(quantString);
            mSupplierEditText.setText(supplierName);
            mPhoneEditText.setText(supplierPhone);



            // Update the views on the screen with the values from the database



        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // clear all data from input fields
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");

    }
}
