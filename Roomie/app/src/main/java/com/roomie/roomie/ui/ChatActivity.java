package com.roomie.roomie.ui;

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
import android.widget.TextView;

import com.magnet.mmx.client.api.MMX;
import com.roomie.roomie.R;
import com.roomie.roomie.api.MagnetAPI;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private EditText recipientInput;
    private LinearLayout container;
    private String username;
    private MagnetAPI magnet = new MagnetAPI();
    MMX.EventListener receiveMessageListener =
            magnet.getEventListener(new MagnetAPI.Callback<String>() {
                @Override
                public void onResult(String result) {
                    addMessageToChat(result, false);
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

        EditText usernameInput = (EditText) findViewById(R.id.username);
        usernameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().equals("")) {
                    username = v.getText().toString();
                    magnet.login(username, null);
                }
                Log.d(TAG, "enter");
                return true;
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

        recipientInput = (EditText) findViewById(R.id.recipient);
        container = (LinearLayout) findViewById(R.id.container);
    }

    private void sendMessage(final String messageText) {
        magnet.sendMessage(username,
                recipientInput.getText().toString(),
                messageText,
                new MagnetAPI.Callback<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        addMessageToChat(messageText, true);
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

}
