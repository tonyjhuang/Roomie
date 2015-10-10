package com.roomie.roomie.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final LatLngBounds BOUNDS_USA =
            new LatLngBounds(new LatLng(25.284438, -125.859375), new LatLng(48.545705, -66.093750));

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient googleApiClient;

    private PlaceAutocompleteAdapter autocompleteAdapter;

    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Main", "connected to api");
                        Log.d("Main", getLatLng("brooklyn, nyc").toString());
                        Log.d("Main", getLatLng("brooklyn").toString());
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        autoCompleteTextView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        autocompleteAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS_USA,
                null);

        // Register a listener that receives callbacks when a suggestion has been selected
        autoCompleteTextView.setOnItemClickListener(Autocomplete.getClickListener(
                googleApiClient, autocompleteAdapter, new Callback<Place>() {
                    @Override
                    public void onResult(Place place) {
                        Log.d(TAG, getLatLng(place.getAddress().toString()).toString());
                    }
                }));

        autoCompleteTextView.setAdapter(autocompleteAdapter);

        final SwipeFlingAdapterView cardsContainer = (SwipeFlingAdapterView) findViewById(R.id.cards);

        ArrayList<User> users = new ArrayList<User>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        users.add(new User());
        users.add(new User());
        users.add(new User());

        CardsAdapter adapter = new CardsAdapter(users);
        cardsContainer.setAdapter(adapter);
        cardsContainer.setFlingListener(getOnFlingListener(adapter));

        // Optionally add an OnItemClickListener
        cardsContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Log.d(TAG, "clock");
            }
        });

        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsContainer.getTopCardListener().selectRight();
            }
        });
        findViewById(R.id.reject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsContainer.getTopCardListener().selectLeft();
            }
        });
    }

    private LatLng getLatLng(String location) {
        Geocoder coder = new Geocoder(this);
        try {
            List<Address> addressList = coder.getFromLocationName(location, 1);
            if (addressList.size() != 0) {
                return new LatLng(
                        addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e("Main", e.getMessage());
            return null;
        }
    }

    private SwipeFlingAdapterView.onFlingListener getOnFlingListener(final CardsAdapter adapter) {
        return new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void onScroll(float v) {

            }

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                adapter.remove(0);
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onRightCardExit(Object dataObject) {
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.d(TAG, "almost empty!");
            }
        };
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar partial_potential_match clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_messages) {
            startActivity(new Intent(this, MessageListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
