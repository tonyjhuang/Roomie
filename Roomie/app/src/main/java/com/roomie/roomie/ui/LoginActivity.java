package com.roomie.roomie.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;

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
    final String fonts[] = {"MavenPro-Bold.ttf",
            "Montserrat-Bold.ttf", "Raleway-SemiBold.ttf", "Righteous-Regular.ttf"};
    int i = 0;
    /* *************************************
     *              GENERAL                *
     ***************************************/
    private FirebaseApi firebaseApi = FirebaseApiClient.getInstance();
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
        if (firebaseApi.isLoggedIn()) {
            onLoggedIn();
        }
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

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Logging in...");
        mAuthProgressDialog.setCancelable(false);

        final TextView appName = (TextView) findViewById(R.id.app_name);
        Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.ttf");
        appName.setTypeface(font);


        // Make the ImageView draw behind the statusbar.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        Glide.with(this).load(R.drawable.bg).centerCrop().into((ImageView) findViewById(R.id.bg));
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

    // Logout of firebase and facebook, and reset visual state.
    private void logout() {
        firebaseApi.logout();
        LoginManager.getInstance().logOut();
        onLoggedOut();
    }

    private void onLoggedIn() {
        if (mAuthProgressDialog != null) {
            mAuthProgressDialog.hide();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void onLoggedOut() {
        mFacebookLoginButton.setVisibility(View.VISIBLE);
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