package com.willmcintosh.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.willmcintosh.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        // find views to modify
        TextView titleTextView = view.findViewById(R.id.title);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        // find columns of book attirbutes to display
        int titleColumnIndex = cursor.getColumnIndex(BookEntry
                .COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry
                .COLUMN_QUANTITY);

        // read book attributes from current book
        String bookTitle = cursor.getString(titleColumnIndex);
        int bookPrice = cursor.getInt(priceColumnIndex);
        String priceString = Integer.toString(bookPrice);
        int bookQuantity = cursor.getInt(quantityColumnIndex);
        String quantString = Integer.toString(bookQuantity);

        // update textview with attributes from current book
        titleTextView.setText(bookTitle);
        priceTextView.setText(priceString);
        quantityTextView.setText(quantString);
    }
}
