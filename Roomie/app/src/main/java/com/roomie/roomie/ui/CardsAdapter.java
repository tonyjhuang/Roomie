package com.roomie.roomie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.roomie.roomie.R;
import com.roomie.roomie.api.models.User;
import com.roomie.roomie.ui.partials.PotentialMatchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class CardsAdapter extends BaseAdapter {

    private static final String TAG = "CARDS";

    private List<User> users = new ArrayList<>();
    private List<String> listids = new ArrayList<>();

    public void addUsers(List<User> users) {
        List<User> userCopy = new ArrayList<>(users);
        for (User u : userCopy) {
            // Don't add duplicate users.
            if (listids.contains(u.getId())) {
                users.remove(u);
            } else {
                listids.add(u.getId());
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
        return getItem(position).getId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        PotentialMatchView.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.partial_potential_match, parent, false);
            holder = PotentialMatchView.getViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PotentialMatchView.ViewHolder) convertView.getTag();
        }

        PotentialMatchView.populateViewHolder(parent.getContext(), holder, user);

        // Create stack effect.
        convertView.setTranslationY(position * 5);
        convertView.setScaleX(1f + (position * .01f));
        convertView.setScaleY(1f + (position * .01f));
        holder.cardView.setCardElevation(getCount() - position + 1);;
        return convertView;
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
}
