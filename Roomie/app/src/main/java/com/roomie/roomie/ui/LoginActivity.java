package com.roomie.roomie.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * This application demos the use of the Firebase Login feature. It currently supports logging in
 * with Google, Facebook, Twitter, Email/Password, and Anonymous providers.
 * <p/>
 * The methods in this class have been divided into sections based on providers (with a few
 * general methods).
 * <p/>
 * Facebook provides its own API via the {@link com.facebook.login.widget.LoginButton}.
 * Twitter requires us to use a Web View to authenticate, see
 * Email/Password is provided using {@link com.firebase.client.Firebase}
 * Anonymous is provided using {@link com.firebase.client.Firebase}
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    // If this
    private static final boolean SHOULD_ADVANCE_TO_MAIN = true;

    /* *************************************
     *              GENERAL                *
     ***************************************/
    private FirebaseApi firebaseApi = FirebaseApiClient.getInstance();
    /* TextView that is used to display information about the logged in user */
    private TextView mLoggedInStatusTextView;
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    /* *************************************
     *              FACEBOOK               *
     ***************************************/
    /* The login button for Facebook */
    private LoginButton mFacebookLoginButton;
    /* The callback manager for Facebook */
    private CallbackManager mFacebookCallbackManager;
    /* Used to track user logging in/out off Facebook */
    private AccessTokenTracker mFacebookAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Load the view and display it */
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);

        /* *************************************
         *              FACEBOOK               *
         ***************************************/
        /* Load the Facebook login button and set up the tracker to monitor access token changes */
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = (LoginButton) findViewById(R.id.login_with_facebook);
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
                LoginActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };


        /* *************************************
         *               GENERAL               *
         ***************************************/
        mLoggedInStatusTextView = (TextView) findViewById(R.id.login_status);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);

        Log.d(TAG, "firebase login status: " + firebaseApi.isLoggedIn());
        if(firebaseApi.isLoggedIn()) {
            Log.d(TAG, "logged in...");
            onLoggedIn();
        }

        logout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if user logged in with Facebook, stop tracking their token
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
        }
    }

    /**
     * This method fires when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Map<String, String> options = new HashMap<String, String>();
        /* Otherwise, it's probably the request by the Facebook login button, keep track of the session */
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* If a user is currently authenticated, display a logout menu */
        if (firebaseApi.isLoggedIn()) {
            getMenuInflater().inflate(R.menu.menu_login, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Logout of firebase and facebook, and reset visual state.
    private void logout() {
        firebaseApi.logout();
        LoginManager.getInstance().logOut();
        onLoggedOut();
    }

    private void onLoggedIn() {
        /* Hide all the login buttons */
        mFacebookLoginButton.setVisibility(View.GONE);
        mLoggedInStatusTextView.setVisibility(View.VISIBLE);

        firebaseApi.getCurrentUser(new Callback<User>() {
            @Override
            public void onResult(final User result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoggedInStatusTextView.setText("Logged in as " + result.getName());
                    }
                });
            }
        });
        Log.d(TAG, "logged in? " + firebaseApi.isLoggedIn());
        supportInvalidateOptionsMenu();
        mAuthProgressDialog.hide();
        if (SHOULD_ADVANCE_TO_MAIN) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void onLoggedOut() {
        mFacebookLoginButton.setVisibility(View.VISIBLE);
        mLoggedInStatusTextView.setVisibility(View.GONE);
        supportInvalidateOptionsMenu();
        mAuthProgressDialog.hide();
    }

    /* ************************************
     *             FACEBOOK               *
     **************************************
     */
    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            mAuthProgressDialog.show();
            firebaseApi.login(token.getToken(), new Callback<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        onLoggedIn();
                    } else {
                        Log.e(TAG, "Failed to login. Clearing and logging out.");
                        logout();
                    }
                }
            });
        } else {
            logout();
        }
    }
}