package com.willmcintosh.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for Bookstore app
 */
public final class BookContract {

    private BookContract() {
    }

    /**
     * Create Content URI
     */
    public static final String CONTENT_AUTHORITY = "com.willmcintosh.bookstore";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +
            CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";


    public static final class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "books";

        /**
         * Content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath
                (BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Unique ID for the book
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Product name
         */
        public static final String COLUMN_PRODUCT_NAME = "product_name";

        /**
         * Price
         */
        public static final String COLUMN_PRICE = "price";

        /**
         * Quantity
         */
        public static final String COLUMN_QUANTITY = "quantity";

        /**
         * Supplier Name
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Supplier Phone Number
         */
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        /**
         * Returns whether a phone matches a valid phone pattern
         */
        public static boolean validPhone(String number) {
            return android.util.Patterns.PHONE.matcher(number).matches();
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver
                .CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                PATH_BOOKS;


    }
}
