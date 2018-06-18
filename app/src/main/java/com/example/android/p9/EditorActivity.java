package com.example.android.p9;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.p9.data.BookCotracts.booksEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * URI
     **/
    private Uri currentBookUri;

    /**
     * Edit Text Attributes
     **/
    private EditText bookEditText;
    private EditText suppEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private EditText suppPhoneEditText;
    private EditText suppEmailEditText;

    /**
     * BUTTONS
     **/
    private Button quantityIncrement;
    private Button quantityDecrement;
    private Button priceIncrement;
    private Button priceDecrement;

    /**
     * Boolean flag
     **/
    private boolean isChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            isChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentBookUri = intent.getData();
        if (currentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        bookEditText = findViewById(R.id.edit_book_title);
        suppEditText = findViewById(R.id.edit_supplier);
        priceEditText = findViewById(R.id.edit_price);
        quantityEditText = findViewById(R.id.edit_quantity);
        quantityIncrement = findViewById(R.id.increment_quantity);
        quantityDecrement = findViewById(R.id.decrement_quantity);
        priceIncrement = findViewById(R.id.increment_price);
        priceDecrement = findViewById(R.id.decrement_price);
        suppPhoneEditText = findViewById(R.id.edit_phone);
        suppEmailEditText = findViewById(R.id.edit_mail);

        bookEditText.setOnTouchListener(touchListener);
        suppEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);

        quantityIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incQuantity();
                isChanged = true;
            }
        });

        quantityDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decQuantity();
                isChanged = true;
            }
        });

        priceIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incPrice();
                isChanged = true;
            }
        });

        priceDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decPrice();
                isChanged = true;
            }
        });

    }

    private void saveBook() {

        int price = 0;
        int quantity = 0;
        int changedRows;
        int phone = 0;

        String bookString = bookEditText.getText().toString().trim();
        String suppString = suppEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String suppPhoneString = suppPhoneEditText.getText().toString().trim();
        String suppEmailString = suppEmailEditText.getText().toString().trim();

        if (currentBookUri == null && TextUtils.isEmpty(bookString) && TextUtils.isEmpty(suppString)) {
            Toast.makeText(this, getString(R.string.info_correction_check), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(bookString)) {
            Toast.makeText(this, getString(R.string.info_correction_check), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(suppEmailString)) {
            Toast.makeText(this, getString(R.string.info_correction_check), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        } else {
            Toast.makeText(this, getString(R.string.info_correction_check), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        } else {
            Toast.makeText(this, getString(R.string.info_correction_check), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(suppPhoneString)) {
            phone = Integer.parseInt(suppPhoneString);
        } else {
            Toast.makeText(this, getString(R.string.info_correction_check),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(booksEntry.COLUMN_BOOKS_NAME, bookString);
        contentValues.put(booksEntry.COLUMN_SUPPLIER, suppString);
        contentValues.put(booksEntry.COLUMN_PRICE, priceString);
        contentValues.put(booksEntry.COLUMN_QUANTITY, quantityString);
        contentValues.put(booksEntry.COLUMN_SUPP_EMAIL, suppEmailString);

        contentValues.put(booksEntry.COLUMN_PRICE, price);
        contentValues.put(booksEntry.COLUMN_QUANTITY, quantity);
        contentValues.put(booksEntry.COLUMN_SUPER_PHONE, phone);


        if (currentBookUri == null) {

            Uri newUri = getContentResolver().insert(booksEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.failed_insertion),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_insertion),
                        Toast.LENGTH_SHORT).show();
            }

        } else {

            changedRows = getContentResolver().update(currentBookUri, contentValues, null, null);
            if (changedRows == 0) {
                Toast.makeText(this, getString(R.string.failed_insertion),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_insertion),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save: {
                saveBook();
                finish();
                return true;
            }

            case R.id.action_delete: {
                showDeleteConfirmationDialog();
                return true;
            }

            case android.R.id.home: {
                if (!isChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                booksEntry._ID,
                booksEntry.COLUMN_BOOKS_NAME,
                booksEntry.COLUMN_SUPPLIER,
                booksEntry.COLUMN_QUANTITY,
                booksEntry.COLUMN_PRICE,
                booksEntry.COLUMN_SUPER_PHONE,
                booksEntry.COLUMN_SUPP_EMAIL,
        };

        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(booksEntry.COLUMN_BOOKS_NAME);
            int supplierIndex = cursor.getColumnIndex(booksEntry.COLUMN_SUPPLIER);
            int quantityIndex = cursor.getColumnIndex(booksEntry.COLUMN_QUANTITY);
            int priceIndex = cursor.getColumnIndex(booksEntry.COLUMN_PRICE);
            int suppPhoneNumberIndex = cursor.getColumnIndex(booksEntry.COLUMN_SUPER_PHONE);
            int suppEmailIndex = cursor.getColumnIndex(booksEntry.COLUMN_SUPP_EMAIL);

            String name = cursor.getString(nameIndex);
            String supplier = cursor.getString(supplierIndex);
            int quantity = cursor.getInt(quantityIndex);
            int price = cursor.getInt(priceIndex);
            int phoneNumber = cursor.getInt(suppPhoneNumberIndex);
            String email = cursor.getString(suppEmailIndex);

            bookEditText.setText(name);
            suppEditText.setText(supplier);
            quantityEditText.setText(Integer.toString(quantity));
            priceEditText.setText(Integer.toString(price));
            suppPhoneEditText.setText(Integer.toString(phoneNumber));
            suppEmailEditText.setText(email);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookEditText.setText("");
        suppEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        suppPhoneEditText.setText("");
        suppEmailEditText.setText("");
    }


    /************* HELPFUL METHODS ****************/

    public void decQuantity() {
        String prevValueString = quantityEditText.getText().toString();

        if (prevValueString.isEmpty()) {
            return;
        } else if (prevValueString.equals("0")) {
            return;
        } else {
            int previousValue = Integer.parseInt(prevValueString);
            quantityEditText.setText(String.valueOf(previousValue - 1));
        }
    }

    private void incQuantity() {
        String prevValueString = quantityEditText.getText().toString();
        int previousValue = 0;

        if (prevValueString.isEmpty()) {
            quantityEditText.setText(String.valueOf(previousValue + 1));

        } else {
            previousValue = Integer.parseInt(prevValueString);
            quantityEditText.setText(String.valueOf(previousValue + 1));
        }
    }

    private void decPrice() {
        String preValueString = priceEditText.getText().toString();

        if (preValueString.isEmpty()) {
            return;
        } else if (preValueString.equals("0") || preValueString.contains("-")) {
            return;
        } else {
            int previousValue = Integer.parseInt(preValueString);
            priceEditText.setText(String.valueOf(previousValue - 1));
        }
    }

    private void incPrice() {
        String preValueString = priceEditText.getText().toString();
        int previousValue = 0;

        if (preValueString.isEmpty()) {
            priceEditText.setText(String.valueOf(previousValue + 1));

        } else {
            previousValue = Integer.parseInt(preValueString);
            priceEditText.setText(String.valueOf(previousValue + 1));
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.not_saved_assurance);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_book_assurance);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        int deletedRows;
        if (currentBookUri != null) {
            deletedRows = getContentResolver().delete(currentBookUri, null, null);

            if (deletedRows == 0) {
                Toast.makeText(this, getString(R.string.failed_deletion),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_deletion),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}
