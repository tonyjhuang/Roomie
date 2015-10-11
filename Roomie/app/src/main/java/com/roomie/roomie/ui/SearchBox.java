package com.roomie.roomie.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class SearchBox extends FrameLayout {

    private static final String TAG = "SEARCHBOX";

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

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if(actionId )
                return false;
            }
        });
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private static ResultCallback<PlaceBuffer> getUpdatePlaceDetailsCallback(final Callback<Place> callback) {
        return new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    // Request did not complete successfully
                    Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                    places.release();
                    return;
                }
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                callback.onResult(place);

                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            }
        };
    }

    public void setGoogleApiClient(GoogleApiClient client) {
        this.client = client;
        autocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), client, BOUNDS_USA, null);
        search.setAdapter(autocompleteAdapter);
    }

    public void setPlaceCallback(Callback<Place> callback) {
        search.setOnItemClickListener(getClickListener(
                client, autocompleteAdapter, callback));
    }

    public AdapterView.OnItemClickListener getClickListener(
            final GoogleApiClient googleApiClient,
            final PlaceAutocompleteAdapter adapter,
            final Callback callback) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected potential_match from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
                final AutocompletePrediction item = adapter.getItem(position);
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

                Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleApiClient, placeId);
                placeResult.setResultCallback(getUpdatePlaceDetailsCallback(callback));

                Toast.makeText(parent.getContext(), "Clicked: " + primaryText,
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
            }
        };
    }

    /**
     * Adapter that handles Autocomplete requests from the Places Geo Data API.
     * {@link AutocompletePrediction} results from the API are frozen and stored directly in this
     * adapter. (See {@link AutocompletePrediction#freeze()}.)
     * <p/>
     * Note that this adapter requires a valid {@link com.google.android.gms.common.api.GoogleApiClient}.
     * The API client must be maintained in the encapsulating Activity, including all lifecycle and
     * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API.
     */
    private static class PlaceAutocompleteAdapter
            extends ArrayAdapter<AutocompletePrediction> implements Filterable {

        private static final String TAG = "Autocomplete";
        private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        /**
         * Current results returned by this adapter.
         */
        private ArrayList<AutocompletePrediction> mResultList;

        /**
         * Handles autocomplete requests.
         */
        private GoogleApiClient mGoogleApiClient;

        /**
         * The bounds used for Places Geo Data autocomplete API requests.
         */
        private LatLngBounds mBounds;

        /**
         * The autocomplete filter used to restrict queries to a specific set of place types.
         */
        private AutocompleteFilter mPlaceFilter;

        /**
         * Initializes with a resource for text rows and autocomplete query bounds.
         *
         * @see ArrayAdapter#ArrayAdapter(Context, int)
         */
        public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                        LatLngBounds bounds, AutocompleteFilter filter) {
            super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
            mGoogleApiClient = googleApiClient;
            mBounds = bounds;
            mPlaceFilter = filter;
        }

        /**
         * Sets the bounds for all subsequent queries.
         */
        public void setBounds(LatLngBounds bounds) {
            mBounds = bounds;
        }

        /**
         * Returns the number of results received in the last autocomplete query.
         */
        @Override
        public int getCount() {
            return mResultList.size();
        }

        /**
         * Returns an partial_potential_match from the last autocomplete query.
         */
        @Override
        public AutocompletePrediction getItem(int position) {
            return mResultList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            // Sets the primary and secondary text for a row.
            // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
            // styling based on the given CharacterStyle.

            AutocompletePrediction item = getItem(position);

            TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
            TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
            textView1.setText(item.getPrimaryText(STYLE_BOLD));
            textView2.setText(item.getSecondaryText(STYLE_BOLD));
            textView1.setTextColor(getContext().getResources().getColor(R.color.black));
            textView2.setTextColor(getContext().getResources().getColor(R.color.gray));

            return row;
        }

        /**
         * Returns the filter for the current set of autocomplete results.
         */
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    // Skip the autocomplete query if no constraints are given.
                    if (constraint != null) {
                        // Query the autocomplete API for the (constraint) search string.
                        mResultList = getAutocomplete(constraint);
                        if (mResultList != null) {
                            // The API successfully returned results.
                            results.values = mResultList;
                            results.count = mResultList.size();
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        // The API returned at least one result, update the data.
                        notifyDataSetChanged();
                    } else {
                        // The API did not return any results, invalidate the data set.
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    // Override this method to display a readable result in the AutocompleteTextView
                    // when clicked.
                    if (resultValue instanceof AutocompletePrediction) {
                        return ((AutocompletePrediction) resultValue).getFullText(null);
                    } else {
                        return super.convertResultToString(resultValue);
                    }
                }
            };
        }

        /**
         * Submits an autocomplete query to the Places Geo Data Autocomplete API.
         * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
         * objects to store the Place ID and description that the API returns.
         * Returns an empty list if no results were found.
         * Returns null if the API client is not available or the query did not complete
         * successfully.
         * This method MUST be called off the main UI thread, as it will block until data is returned
         * from the API, which may include a network request.
         *
         * @param constraint Autocomplete query string
         * @return Results from the autocomplete API or null if the query was not successful.
         * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
         * @see AutocompletePrediction#freeze()
         */
        private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
            if (mGoogleApiClient.isConnected()) {
                Log.i(TAG, "Starting autocomplete query for: " + constraint);

                // Submit the query to the autocomplete API and retrieve a PendingResult that will
                // contain the results when the query completes.
                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi
                                .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                        mBounds, mPlaceFilter);

                // This method should have been called off the main UI thread. Block and wait for at most 60s
                // for a result from the API.
                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);

                // Confirm that the query completed successfully, otherwise return null
                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                    autocompletePredictions.release();
                    return null;
                }

                Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                        + " predictions.");

                // Freeze the results immutable representation that can be stored safely.
                return DataBufferUtils.freezeAndClose(autocompletePredictions);
            }
            Log.e(TAG, "Google API client is not connected for autocomplete query.");
            return null;
        }


    }
}
