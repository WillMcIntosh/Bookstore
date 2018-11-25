package com.willmcintosh.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.willmcintosh.bookstore.data.BookContract;
import com.willmcintosh.bookstore.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the supplier's phone
     */
    private EditText mPhoneEditText;

    /**
     * Button to contact supplier
     */
    private Button mContactButton;

    /**
     * Boolean flag to track if entry has been edited
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener to check for user changes
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

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

            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_book);

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mPhoneEditText = findViewById(R.id.edit_supplier_phone);
        // contact button
        mContactButton = findViewById(R.id.order_button);

        // set up OnTouch Listener
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

        // set up Button OnClickListener

        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = PhoneNumberUtils.normalizeNumber(mPhoneEditText.getText().toString().trim());
                if (BookContract.BookEntry.validPhone(phoneNumber)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.editor_valid_phone),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


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
        String supplierNameString = mSupplierEditText.getText().toString()
                .trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        // check if this is a new book with no data entered
        if (mCurrentBookUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty
                (quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(phoneString)) {

            return;
        }

        // check if user forgot to fill out a field
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty
                (supplierNameString)) {

            Toast.makeText(this, getString(R.string.editor_fill_all_fields),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean invalidPhone = false;
        // check for invalid phone number which would throw an exception in
        // the Provider
        if (!BookEntry.validPhone(phoneString) || TextUtils.isEmpty
                (phoneString)) {
            phoneString = "";
            invalidPhone = true;
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
            // Insert a new book into the provider, returning the content URI
            // for
            // the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI,
                    values);
            // Show a toast message depending on whether or not the insertion
            // was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error
                // with insertion.
                Toast.makeText(this, getString(R.string
                        .editor_update_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display
                // a toast.
                if (invalidPhone) {
                    Toast.makeText(this, getString(R.string
                            .editor_null_phone), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string
                            .editor_update_book_success), Toast.LENGTH_SHORT)
                            .show();
                }
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
                if (invalidPhone) {
                    Toast.makeText(this, getString(R.string
                            .editor_null_phone), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string
                            .editor_update_book_success), Toast.LENGTH_SHORT)
                            .show();
                }
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

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
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
                // show delete confirmation
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mBookHasChanged) {
                    // Navigate back to parent activity (MainActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int
                            i) {
                        // User clicked "Discard" button, navigate to parent
                        // activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show a dialog that notifies the user they have unsaved
                // changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the
        // user.
        // Create a click listener to handle the user confirming that changes
        // should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new
                DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE, BookEntry.COLUMN_QUANTITY, BookEntry
                .COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a
        // background thread
        return new CursorLoader(this, mCurrentBookUri, projection, null,
                null, null);
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
            int priceColumnIndex = cursor.getColumnIndex(BookEntry
                    .COLUMN_PRICE);
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

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click
        // listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteBook() {
        int rowsDeleted = 0;
        if (mCurrentBookUri != null) {
            rowsDeleted = getContentResolver().delete(mCurrentBookUri, null,
                    null);
        }

        // Show a toast message depending on whether or not the delete was
        // successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_success),
                    Toast.LENGTH_SHORT).show();
        }
        // close activity
        finish();
    }
}
