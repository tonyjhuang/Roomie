package com.roomie.roomie.ui.partials;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roomie.roomie.R;
import com.roomie.roomie.api.models.User;

/**
 * Created by tonyjhuang on 10/18/15.
 */
public class PotentialMatchView {

    public static void populateViewHolder(Context context, ViewHolder holder, User user) {
        holder.name.setText(user.getName() + ", " + user.getAge());
        holder.bio.setText(user.getBio());
        Glide.with(context).load(user.getProfilePicture()).into(holder.imageView);
    }

    public static ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    public static class ViewHolder {
        public TextView name;
        public ImageView imageView;
        public CardView cardView;
        public TextView bio;
        public LinearLayout preferencesContainer;
        public TextView preferencesLabel;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            bio = (TextView) view.findViewById(R.id.bio);
            imageView = (ImageView) view.findViewById(R.id.image);
            cardView = (CardView) view.findViewById(R.id.card_container);
            preferencesContainer = (LinearLayout) view.findViewById(R.id.preferences_container);
            preferencesLabel = (TextView) view.findViewById(R.id.preferences_label);
        }
    }
}
