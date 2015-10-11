package com.roomie.roomie.ui;

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

        holder.name.setText(user.getName());
        Glide.with(parent.getContext()).load(user.getProfilePicture()).into(holder.imageView);
        convertView.setTranslationY(position * 10);
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

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            imageView = (ImageView) view.findViewById(R.id.image);
        }
    }
}
