package com.roomie.roomie.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.ObjectAnimator;
import com.roomie.roomie.R;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.models.User;
import com.roomie.roomie.ui.partials.PotentialMatchView;

/**
 * Created by tonyjhuang on 10/18/15.
 * The screen that pops up when the user has matched with another user after swiping
 */
public class MatchActivity extends RoomieActivity {

    private FirebaseApi firebase = FirebaseApiClient.getInstance();

    private View background;
    private TextView label;
    private CardView matchView;
    private Button messageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        background = findViewById(R.id.background);
        background.setAlpha(0);
        label = (TextView) findViewById(R.id.label);
        label.setAlpha(0);
        matchView = (CardView) findViewById(R.id.match);
        matchView.setAlpha(0);
        messageButton = (Button) findViewById(R.id.message);
        messageButton.setAlpha(0);

        YoYo.with(Techniques.FadeIn)
                .duration(250)
                .playOn(findViewById(R.id.background));

        new User(getIntent().getStringExtra("USER_ID")).retrieve(new Callback<User>() {
            @Override
            public void onResult(User result) {
                PotentialMatchView.ViewHolder holder =
                        PotentialMatchView.getViewHolder(findViewById(R.id.match));
                PotentialMatchView.populateViewHolder(MatchActivity.this, holder, result);
                animateIn();
            }
        });
    }

    private void animateIn() {
        YoYo.with(new FadeInUpAnimator())
                .duration(300)
                .playOn(matchView);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(new BounceInDownAnimator())
                        .duration(400)
                        .playOn(label);
            }
        }, 250);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(new BounceInUpAnimator())
                        .duration(400)
                        .playOn(messageButton);
            }
        }, 350);
    }

    public class BounceInDownAnimator extends BaseViewAnimator {
        @Override
        public void prepare(View target) {
            getAnimatorAgent().playTogether(
                    ObjectAnimator.ofFloat(target, "alpha", 0, 1, 1, 1),
                    ObjectAnimator.ofFloat(target, "translationY", -target.getHeight(), 30, 0));
        }
    }

    public class BounceInUpAnimator extends BaseViewAnimator {
        @Override
        public void prepare(View target) {
            getAnimatorAgent().playTogether(
                    ObjectAnimator.ofFloat(target, "alpha", 0, 1, 1),
                    ObjectAnimator.ofFloat(target, "translationY", target.getMeasuredHeight(), -30, 0));
        }
    }

    public class FadeInUpAnimator extends BaseViewAnimator {
        @Override
        public void prepare(View target) {
            getAnimatorAgent().playTogether(
                    ObjectAnimator.ofFloat(target, "alpha", 0, 1),
                    ObjectAnimator.ofFloat(target, "translationY", target.getHeight() / 8, 0)
            );
        }
    }
}
