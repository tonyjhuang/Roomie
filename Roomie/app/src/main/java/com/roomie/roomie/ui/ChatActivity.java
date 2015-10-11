package com.roomie.roomie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.magnet.mmx.client.api.MMX;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.MagnetApi;
import com.roomie.roomie.api.models.User;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "CHAT";
    private ScrollView scrollContainer;
    private LinearLayout container;
    private String username;
    private String recipient;
    private FirebaseApi firebase = FirebaseApiClient.getInstance();
    private MagnetApi magnet = MagnetApi.getInstance();
    MMX.EventListener receiveMessageListener =
            magnet.getEventListener(new Callback<String>() {
                @Override
                public void onResult(final String message) {
                    firebase.onReceiveMessage(recipient, message, new Callback<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            if (result) {
                                addMessageToChat(message, false);
                            } else {
                                Log.e(TAG, "Error saving message to chat. Dropping message");
                            }
                        }
                    });

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollContainer = (ScrollView) findViewById(R.id.scroll_container);
        container = (LinearLayout) findViewById(R.id.container);

        recipient = getIntent().getStringExtra("USER_ID");

        firebase.getCurrentUser(new Callback<User>() {
            @Override
            public void onResult(User result) {
                username = result.getId();
                TextView textView = new TextView(ChatActivity.this);
                textView.setText("username is " + username);
                container.addView(textView);
                setTitle(result.getName());
                // TODO(tony): Get message history.
            }
        });

        final EditText messageInput = (EditText) findViewById(R.id.input);
        messageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().equals("")) {
                    sendMessage(v.getText().toString());
                    v.setText("");
                }
                Log.d(TAG, "enter");
                return true;
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageInput.getText().toString().equals("")) {
                    sendMessage(messageInput.getText().toString());
                    messageInput.setText("");
                }
                Log.d(TAG, "send");
            }
        });


    }

    private void sendMessage(final String messageText) {
        firebase.sendMessage(recipient, messageText, new Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) {
                    addMessageToChat(messageText, true);
                } else {
                    Log.e(TAG, "Message failed to send!");
                }
            }
        });
    }

    private void addMessageToChat(final String message, final boolean currentUser) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int layout = currentUser ? R.layout.partial_chat_item_rtl : R.layout.partial_chat_item;
                View view = getLayoutInflater().inflate(layout, container, false);
                view.findViewById(R.id.image).setBackgroundResource(R.color.colorAccent);
                ((TextView) view.findViewById(R.id.message)).setText(message);
                container.addView(view);
                scrollContainer.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MMX.registerListener(receiveMessageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MMX.unregisterListener(receiveMessageListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebase.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
