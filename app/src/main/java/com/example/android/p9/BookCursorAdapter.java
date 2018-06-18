package com.example.android.p9;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.p9.data.BookCotracts.booksEntry;

class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameView = view.findViewById(R.id.book_title);
        TextView suppView = view.findViewById(R.id.book_supplier);
        TextView priceView = view.findViewById(R.id.book_price);
        final TextView quantityView = view.findViewById(R.id.book_quantity);
        Button saleBtn = view.findViewById(R.id.sell);

        int nameIndex = cursor.getColumnIndex(booksEntry.COLUMN_BOOKS_NAME);
        int supplierIndex = cursor.getColumnIndex(booksEntry.COLUMN_SUPPLIER);
        int quantityIndex = cursor.getColumnIndex(booksEntry.COLUMN_QUANTITY);
        int priceIndex = cursor.getColumnIndex(booksEntry.COLUMN_PRICE);

        String name = cursor.getString(nameIndex);
        String supplier = cursor.getString(supplierIndex);
        String quantity = cursor.getString(quantityIndex);
        String price = cursor.getString(priceIndex);

        nameView.setText(name);
        suppView.setText(supplier);
        priceView.setText(price);
        quantityView.setText(quantity);

        int index = cursor.getColumnIndex(booksEntry._ID);
        int id = cursor.getInt(index);
        final Uri currentBookUri = ContentUris.withAppendedId(booksEntry.CONTENT_URI, id);

        saleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityView.getText().toString());
                if (quantity > 0) {
                    quantityView.setText("" + (--quantity));
                    ContentValues values = new ContentValues();
                    values.put(booksEntry.COLUMN_QUANTITY, quantity);
                    context.getContentResolver().update(currentBookUri, values, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.no_more_books),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
