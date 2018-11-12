package com.willmcintosh.bookstore.data;

import android.provider.BaseColumns;

/**
 * API Contract for Bookstore app
 */
public final class BookContract {

    private BookContract() {
    }


    public static final class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "books";

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


    }
}
