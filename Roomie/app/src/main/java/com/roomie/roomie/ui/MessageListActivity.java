package com.roomie.roomie.ui;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roomie.roomie.R;
import com.roomie.roomie.api.models.User;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    static PrettyTime prettyTime = new PrettyTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");

        User user0 = new User("");
        user0.setName("Marina");
        User user1 = new User("");
        user1.setName("Tony");
        User user2 = new User("");
        user2.setName("Christian");
        User user3 = new User("");
        user3.setName("Ola");
        User user4 = new User("");
        user4.setName("Kanye West");

        Calendar calendar = Calendar.getInstance();

        List<MockChat> mockChats = new ArrayList<>();

        mockChats.add(new MockChat(user0, calendar.getTime(),
                "Well, the way they make shows is, they make one show. That show's called a pilot. "));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        mockChats.add(new MockChat(user1, calendar.getTime(),
                "Now that we know who you are, I know who I am. I'm not a mistake! It all makes sense! "));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        mockChats.add(new MockChat(user2, calendar.getTime(),
                "Now that there is the Tec-9, a crappy spray gun from South Miami. This gun is advertised as the most popular gun in American crime."));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        mockChats.add(new MockChat(user3, calendar.getTime(),
                "Normally, both your asses would be dead as fucking fried chicken, but you happen to pull this shit while I'm in a transitional period so I don't wanna kill you, I wanna help you."));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        mockChats.add(new MockChat(user4, calendar.getTime(),
                "Do you see any Teletubbies in here? "));

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        for (MockChat chat : mockChats) {
            View view = getLayoutInflater().inflate(R.layout.partial_message_item, container, false);
            ((ImageView) view.findViewById(R.id.image)).setImageResource(R.drawable.marina);
            ((TextView) view.findViewById(R.id.name)).setText(chat.user.getName());
            ((TextView) view.findViewById(R.id.date)).setText(prettyTime.format(chat.updatedAt));
            ((TextView) view.findViewById(R.id.last_message)).setText(chat.lastMessage);
            container.addView(view);
        }
    }

    private static class MockChat {
        User user;
        Date updatedAt;
        String lastMessage;

        public MockChat(User user, Date updatedAt, String lastMessage) {
            this.user = user;
            this.updatedAt = updatedAt;
            this.lastMessage = lastMessage;
        }
    }
}