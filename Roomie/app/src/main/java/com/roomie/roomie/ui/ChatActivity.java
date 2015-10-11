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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magnet.mmx.client.api.MMX;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.MagnetApi;
import com.roomie.roomie.api.models.Message;
import com.roomie.roomie.api.models.User;

import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "CHAT";
    private ScrollView scrollContainer;
    private LinearLayout container;
    private User currentUser;
    private User recipient;
    private FirebaseApi firebase = FirebaseApiClient.getInstance();
    private MagnetApi magnet = MagnetApi.getInstance();

    MMX.EventListener receiveMessageListener =
            magnet.getEventListener(new Callback<String>() {
                @Override
                public void onResult(final String message) {
                    firebase.onReceiveMessage(recipient.getId(), message, new Callback<Boolean>() {
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
        getSupportActionBar().setTitle("Chat");

        scrollContainer = (ScrollView) findViewById(R.id.scroll_container);
        container = (LinearLayout) findViewById(R.id.container);

        recipient = new User(getIntent().getStringExtra("USER_ID"));
        recipient.retrieve(new Callback<User>() {
            @Override
            public void onResult(User result) {
                setTitle(result.getName());
            }
        });

        firebase.getCurrentUser(new Callback<User>() {
            final String rec_id = new String(recipient.getId());
            @Override
            public void onResult(User result) {
                currentUser = result;
                Message message = new Message();
                message.getMessageHistory(currentUser.getId(), rec_id, new Callback<List<HashMap<String, String>>>() {
                    @Override
                    public void onResult(List<HashMap<String, String>> result) {
                        for (HashMap<String, String> r : result) {
                            addMessageToChat(r.get("message"), r.get("sentByAuthor").equals("true"));
                        }
                    }
                });

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
        firebase.sendMessage(recipient.getId(), messageText, new Callback<Boolean>() {
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

    private void addMessageToChat(final String message, final boolean isCurrentUser) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int layout = isCurrentUser ? R.layout.partial_chat_item_rtl : R.layout.partial_chat_item;
                String profilePicture = isCurrentUser ?
                        currentUser.getProfilePicture() : recipient.getProfilePicture();
                View view = getLayoutInflater().inflate(layout, container, false);
                Glide.with(ChatActivity.this).load(profilePicture)
                        .centerCrop()
                        .into((ImageView) view.findViewById(R.id.image));
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
