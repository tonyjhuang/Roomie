package com.roomie.roomie.ui;

import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roomie.roomie.R;
import com.roomie.roomie.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class CardsAdapter extends BaseAdapter {

    private static final String TAG = "CARDS";

    private List<User> users = new ArrayList<>();

    public void addUsers(List<User> users) {
        for (User u: users){
            if (this.users.contains(u)){
                users.remove(u);
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

        if (convertView == null) {
            convertView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.partial_potential_match, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(user.getName() + ", " + user.getAge());
        Glide.with(parent.getContext()).load(user.getProfilePicture()).into(holder.imageView);

        // Create stack effect.
        convertView.setTranslationY(position * 5);
        convertView.setScaleX(1f + (position * .01f));
        convertView.setScaleY(1f + (position * .01f));
        holder.cardView.setCardElevation(getCount() - position + 1);
        return convertView;
    }

    public User remove(int position) {
        if(position < users.size()) {
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

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            imageView = (ImageView) view.findViewById(R.id.image);
            cardView = (CardView) view.findViewById(R.id.card_container);
        }
    }
}
