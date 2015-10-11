package com.roomie.roomie.ui;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roomie.roomie.R;
import com.roomie.roomie.api.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class CardsAdapter extends BaseAdapter {

    private static final String TAG = "CARDS";

    private List<User> users = new ArrayList<>();
    private List<String> listids = new ArrayList<>();
    private HashMap<String, List<Integer>> preferences = new HashMap<>();

    public void addUsers(List<User> users) {
        List<User> userCopy = new ArrayList<>(users);
        for (User u : userCopy) {
            if (listids.contains(u.getId())) {
                users.remove(u);
            } else {
                listids.add(u.getId());
                preferences.put(u.getId(), getPreferences());
            }
        }
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void clear() {
        users = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Return user.id()
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        ViewHolder holder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.partial_potential_match, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(user.getName() + ", " + user.getAge());
        holder.bio.setText(user.getBio());
        Glide.with(parent.getContext()).load(user.getProfilePicture()).into(holder.imageView);

        holder.preferencesContainer.removeAllViews();
        for(Integer preference : preferences.get(user.getId())) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setImageResource(preference);
            imageView.setAlpha(0.5f);
            holder.preferencesContainer.addView(imageView);
        }

        // Create stack effect.
        convertView.setTranslationY(position * 5);
        convertView.setScaleX(1f + (position * .01f));
        convertView.setScaleY(1f + (position * .01f));
        holder.cardView.setCardElevation(getCount() - position + 1);
        return convertView;
    }

    private List<Integer> getPreferences() {
        List<Integer> preferences = new ArrayList<>();
        Random random = new Random();
        int smokingChance = random.nextInt(15);
        if (smokingChance == 0) {
            preferences.add(R.drawable.trait_smoking);
        } else if (smokingChance == 1 || smokingChance == 2) {
            preferences.add(R.drawable.trait_smoking_no);
        }
        int petsChance = random.nextInt(12);
        if (petsChance == 0 || petsChance == 1) {
            preferences.add(R.drawable.trait_pets);
        } else if (petsChance == 2) {
            preferences.add(R.drawable.trait_pets_no);
        }
        int musicChance = random.nextInt(15);
        if (musicChance == 0) {
            preferences.add(R.drawable.trait_music);
        }
        int parkingChance = random.nextInt(8);
        if (parkingChance == 0) {
            preferences.add(R.drawable.trait_parking);
        }
        return preferences;
    }

    public User remove(int position) {
        if (position < users.size()) {
            User removed = users.remove(position);
            notifyDataSetChanged();
            return removed;
        } else {
            return null;
        }
    }

    private static class ViewHolder {
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
