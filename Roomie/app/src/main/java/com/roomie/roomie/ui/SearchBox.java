package com.roomie.roomie.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class SearchBox extends FrameLayout {

    private static final LatLngBounds BOUNDS_USA =
            new LatLngBounds(new LatLng(25.284438, -125.859375), new LatLng(48.545705, -66.093750));

    private AutoCompleteTextView search;
    private ImageView clear;
    private PlaceAutocompleteAdapter autocompleteAdapter;
    private GoogleApiClient client;

    public SearchBox(Context context) {
        this(context, null);
    }

    public SearchBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.partial_search_box, this, true);
        clear = (ImageView) findViewById(R.id.icon_clear);
        search = (AutoCompleteTextView) findViewById(R.id.search);

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clear.setVisibility(s.length() == 0 ? GONE : VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setGoogleApiClient(GoogleApiClient client) {
        this.client = client;
        autocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), client, BOUNDS_USA, null);
        search.setAdapter(autocompleteAdapter);
    }

    public void setPlaceCallback(Callback<Place> callback) {
        search.setOnItemClickListener(Autocomplete.getClickListener(
                client, autocompleteAdapter, callback));
    }
}
