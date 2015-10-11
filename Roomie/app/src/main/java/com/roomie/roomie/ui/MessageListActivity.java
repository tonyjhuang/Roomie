package com.roomie.roomie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.FirebaseUtils;
import com.roomie.roomie.api.MockFirebaseApiClient;
import com.roomie.roomie.api.models.User;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    private static final String TAG = "MESSAGE";

    private static PrettyTime prettyTime = new PrettyTime();

    private FirebaseApi firebase = FirebaseApiClient.getInstance();

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");

        container = (LinearLayout) findViewById(R.id.container);

        firebase.getCurrentUser(new Callback<User>() {
            @Override
            public void onResult(User currentUser) {
                FirebaseUtils.retrieveUsers(currentUser.getMatches(), new Callback<List<User>>() {
                    @Override
                    public void onResult(List<User> matches) {
                        populateMessageList(matches);
                    }
                });
            }
        });
    }

    private void populateMessageList(final List<User> matches) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (final User match : matches) {
                    View view = getLayoutInflater().inflate(R.layout.partial_message_item, container, false);
                    ((ImageView) view.findViewById(R.id.image)).setImageResource(R.drawable.marina);
                    ((TextView) view.findViewById(R.id.name)).setText(match.getName());
                    //((TextView) view.findViewById(R.id.date)).setText(prettyTime.format(chat.updatedAt));
                    ((TextView) view.findViewById(R.id.last_message)).setText("uhhhh"); //chat.lastMessage);
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MessageListActivity.this, ChatActivity.class);
                            intent.putExtra("USER_ID", match.getId());
                            startActivity(intent);
                        }
                    });
                    container.addView(view);
                }
            }
        });
    }
}
