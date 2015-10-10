package com.roomie.roomie.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roomie.roomie.R;
import com.roomie.roomie.api.models.User;

import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class CardsAdapter extends BaseAdapter {

    private List<User> users;

    public void setUsers(List<User> users) {
        this.users = users;
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

        holder.info.setText(user.getName());
        holder.imageView.setImageResource(R.drawable.marina);

        return convertView;
    }

    public User remove(int position) {
        User removed = users.remove(position);
        notifyDataSetChanged();
        return removed;
    }

    private static class ViewHolder {
        public TextView info;
        public ImageView imageView;

        public ViewHolder(View view) {
            info = (TextView) view.findViewById(R.id.info);
            imageView = (ImageView) view.findViewById(R.id.image);
        }
    }
}
