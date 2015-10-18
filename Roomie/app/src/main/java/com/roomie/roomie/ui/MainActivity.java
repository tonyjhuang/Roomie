package com.roomie.roomie.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.FirebaseUtils;
import com.roomie.roomie.api.models.Location;
import com.roomie.roomie.api.models.User;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RoomieActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient googleApiClient;
    ActionButton acceptButtonOverlay;
    ActionButton rejectButtonOverlay;
    private FirebaseApi firebaseApi = FirebaseApiClient.getInstance();
    private CardsAdapter cardsAdapter;
    private User currentUser;
    // Set default location to Boston for debugging
    // TODO: remove this
    private LatLng currentLatLng = new LatLng(42.3600825, -71.0588801);
    private SwipeFlingAdapterView cardsContainer;
    private SearchBox searchBox;

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
                .build();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        TextView appName = (TextView) findViewById(R.id.app_name);
        Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.ttf");
        appName.setTypeface(font);

        searchBox = (SearchBox) findViewById(R.id.searchbox);
        searchBox.setGoogleApiClient(googleApiClient);
        searchBox.setPlaceCallback(new Callback<Place>() {
            @Override
            public void onResult(final Place place) {
                /* User clicked on search result */
                currentLatLng = place.getLatLng();
                Log.d(TAG, "Current loc: " + currentLatLng.toString());
                final String address = place.getAddress().toString();
                searchBox.setText(address);
                searchBox.dismissDropDown();
                cardsAdapter.clear();
                closeKeyboard();
                firebaseApi.getCurrentUser(new Callback<User>() {
                    @Override
                    public void onResult(User result) {
                        new Location(result.getId(), currentLatLng, address);
                        firebaseApi.getPotentialMatches(currentLatLng, new Callback<List<String>>() {
                            @Override
                            public void onResult(List<String> result) {
                                RetrieveUsersToCreateCard(result);
                            }
                        });
                    }
                });
            }
        });

        cardsContainer = (SwipeFlingAdapterView) findViewById(R.id.cards);

        // Setup swipeable cards.
        cardsAdapter = new CardsAdapter();
        cardsContainer.setAdapter(cardsAdapter);
        cardsContainer.setFlingListener(getOnFlingListener(cardsAdapter));

        // Get click event when user taps on card.
        cardsContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Log.d(TAG, "clock");
                //TODO: open profile here
            }
        });

        acceptButtonOverlay = (ActionButton) findViewById(R.id.accept_overlay);
        acceptButtonOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardsAdapter.getCount() != 0) {
                    cardsContainer.getTopCardListener().selectRight();
                }
            }
        });
        rejectButtonOverlay = (ActionButton) findViewById(R.id.reject_overlay);
        rejectButtonOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardsAdapter.getCount() != 0) {
                    cardsContainer.getTopCardListener().selectLeft();
                }
            }
        });

        // Make sure we have a logged in user.
        firebaseApi.getCurrentUser(new Callback<User>() {
            @Override
            public void onResult(User result) {
                currentUser = result;
                // Retrieve potential matches from api, populate cardsAdapter.
                if (currentLatLng != null) {
                    getPotentialMatches(currentLatLng);
                }
            }
        });
    }

    private void getPotentialMatches(LatLng currentLatLng) {
        if (currentLatLng != null) {
            firebaseApi.getPotentialMatches(currentLatLng, new Callback<List<String>>() {
                @Override
                public void onResult(List<String> result) {
                    RetrieveUsersToCreateCard(result);
                }
            });
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cardsContainer.getWindowToken(), 0);
    }

    private SwipeFlingAdapterView.onFlingListener getOnFlingListener(final CardsAdapter adapter) {
        return new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void onScroll(float v) {
                Log.d(TAG, "scroll: " + v);
                if (v > 0) {
                    acceptButtonOverlay.setAlpha(v);
                    rejectButtonOverlay.setAlpha(0);
                } else if (v < 0) {
                    rejectButtonOverlay.setAlpha(v * -1);
                    acceptButtonOverlay.setAlpha(0);
                } else { // v == 0
                    acceptButtonOverlay.setAlpha(0);
                    rejectButtonOverlay.setAlpha(0);
                }
            }

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d(TAG, "removed object!");
                adapter.remove(0);
                cardsContainer.invalidate();
                acceptButtonOverlay.setAlpha(0);
                rejectButtonOverlay.setAlpha(0);
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                final User swipedUser = (User) dataObject;
                firebaseApi.getCurrentUser(new Callback<User>() {
                    @Override
                    public void onResult(User result) {
                        result.reject(swipedUser.getId());
                    }
                });
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                final User swipedUser = (User) dataObject;
                currentUser.accept(swipedUser.getId(), new Callback<User>() {
                    @Override
                    public void onResult(User result) {
                        currentUser = result;
                        if (currentUser.isMatch(swipedUser.getId())) {
                            onMatch(swipedUser.getId());
                        }
                    }
                });

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }
        };
    }

    private void onMatch(String matchedUserId) {
        Log.d(TAG, "LUCKY YOU! YOU GOT A MATCH!!!");
        Toast.makeText(this, "It's a match! Go to messages to get talking.", Toast.LENGTH_SHORT).show();
    }


    private void RetrieveUsersToCreateCard(List<String> userIds) {
        FirebaseUtils.retrieveUsers(userIds, new Callback<List<User>>() {
            @Override
            public void onResult(final List<User> potentialMatches) {
                firebaseApi.getCurrentUser(new Callback<User>() {
                    @Override
                    public void onResult(User currentUser) {
                        // Filter out users that we've already seen
                        List<User> potentialMatchesCopy = new ArrayList<User>(potentialMatches);
                        for (User p : potentialMatches) {
                            if (currentUser.isMatch(p.getId()) ||
                                    currentUser.getId().equals(p.getId()) ||
                                    currentUser.accepted(p.getId()) ||
                                    currentUser.rejected(p.getId())) {
                                potentialMatchesCopy.remove(p);
                            }
                        }
                        cardsAdapter.addUsers(potentialMatchesCopy);
                    }
                });

            }
        });
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

        if (id == R.id.action_profile) {
            firebaseApi.resetData();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_messages) {
            startActivity(new Intent(this, MessageListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
