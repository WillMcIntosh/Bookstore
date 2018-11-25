package com.willmcintosh.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.willmcintosh.bookstore.data.BookContract.BookEntry;

import java.text.NumberFormat;
import java.util.Locale;

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor)
     * to the given list item layout.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // find views to modify
        TextView titleTextView = view.findViewById(R.id.title);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        // find columns of book attributes to display
        int titleColumnIndex = cursor.getColumnIndex(BookEntry
                .COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry
                .COLUMN_QUANTITY);

        // read book attributes from current book
        String bookTitle = cursor.getString(titleColumnIndex);
        int bookPrice = cursor.getInt(priceColumnIndex);
        // convert book price to currency
        NumberFormat num = NumberFormat.getCurrencyInstance(Locale.US);
        String priceString = num.format(bookPrice/ 100.00);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);

        // update textview with attributes from current book
        titleTextView.setText(bookTitle);
        priceTextView.setText(priceString);
        quantityTextView.setText(String.valueOf(bookQuantity));

        // apply listener to sale button
        Button saleButton = view.findViewById(R.id.button_minus);
        final String id = cursor.getString(cursor.getColumnIndex(BookEntry._ID));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity > 0) {
                    Log.e(LOG_TAG, String.valueOf(bookQuantity));
                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, Long
                            .parseLong(id));
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, bookQuantity -1);
                    context.getContentResolver().update(currentBookUri, values, null, null);
                    swapCursor(cursor);
                }
            }
        });


    }

}
