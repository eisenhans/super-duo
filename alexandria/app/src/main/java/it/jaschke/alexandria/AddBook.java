package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";

    private static final int RC_SCAN_BARCODE = 3;
    private final int LOADER_ID = 1;
    private final String EAN_CONTENT="eanContent";

    private ViewHolder viewHolder;

    private static class ViewHolder {
        EditText barcodeEditText;
        TextView titleView;
        TextView subTitleView;
        TextView authorsView;
        TextView categoriesView;
        ImageView coverView;
        View scanButton;
        View saveButton;
        View deleteButton;

        ViewHolder(View rootView) {
            barcodeEditText = (EditText) rootView.findViewById(R.id.ean);
            titleView = ((TextView) rootView.findViewById(R.id.bookTitle));
            subTitleView = ((TextView) rootView.findViewById(R.id.bookSubTitle));
            authorsView = ((TextView) rootView.findViewById(R.id.authors));
            categoriesView =  ((TextView) rootView.findViewById(R.id.categories));
            coverView = (ImageView) rootView.findViewById(R.id.bookCover);
            scanButton = rootView.findViewById(R.id.scan_button);
            saveButton = rootView.findViewById(R.id.save_button);
            deleteButton = rootView.findViewById(R.id.delete_button);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewHolder != null && viewHolder.barcodeEditText != null) {
            outState.putString(EAN_CONTENT, viewHolder.barcodeEditText.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        viewHolder = new ViewHolder(rootView);

        viewHolder.barcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean =s.toString();
                //catch isbn10 numbers
                if(ean.length()==10 && !ean.startsWith("978")){
                    ean="978"+ean;
                }
                if(ean.length()<13){
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        viewHolder.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.

                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_SCAN_BARCODE);
            }
        });

        viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.barcodeEditText.setText("");
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, viewHolder.barcodeEditText.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                viewHolder.barcodeEditText.setText("");
            }
        });

        if(savedInstanceState!=null){
            viewHolder.barcodeEditText.setText(savedInstanceState.getString(EAN_CONTENT));
            viewHolder.barcodeEditText.setHint("");
        }

        return rootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(viewHolder.barcodeEditText.getText().length()==0){
            return null;
        }
        String eanStr= viewHolder.barcodeEditText.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        viewHolder.titleView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        viewHolder.subTitleView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        viewHolder.authorsView.setLines(authorsArr.length);
        viewHolder.authorsView.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            Glide.with(this)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .into(viewHolder.coverView);
            viewHolder.coverView.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        viewHolder.categoriesView.setText(categories);

        viewHolder.saveButton.setVisibility(View.VISIBLE);
        viewHolder.deleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        viewHolder.titleView.setText("");
        viewHolder.subTitleView.setText("");
        viewHolder.authorsView.setText("");
        viewHolder.categoriesView.setText("");
        viewHolder.coverView.setVisibility(View.INVISIBLE);
        viewHolder.saveButton.setVisibility(View.INVISIBLE);
        viewHolder.deleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SCAN_BARCODE) {
            if (data != null && data.hasExtra(BarcodeCaptureActivity.BARCODE)) {
                String barcode = data.getStringExtra(BarcodeCaptureActivity.BARCODE);
                updateBarcode(barcode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateBarcode(String barcode) {
        viewHolder.barcodeEditText.setText(barcode);
    }
}
