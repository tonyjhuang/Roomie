package com.roomie.roomie.api;

import android.os.AsyncTask;

import com.roomie.roomie.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/11/15.
 */
public class FirebaseUtils {

    public static void retrieveUsers(List<String> ids, Callback<List<User>> callback) {
        new RetrieveUserTask(ids, callback).execute();
    }

    static class RetrieveUserTask extends AsyncTask<Void, Void, List<User>> {
        private List<String> ids;
        private Callback<List<User>> callback;
        private int latch;

        public RetrieveUserTask(List<String> ids, Callback<List<User>> callback) {
            this.ids = ids;
            this.callback = callback;
        }

        @Override
        protected List<User> doInBackground(Void... params) {
            ArrayList<User> users = new ArrayList<>();
            latch = ids.size();
            for(String id : ids) {
                User user = new User(id);
                users.add(user);
                user.retrieve(new Callback<User>() {
                    @Override
                    public void onResult(User result) {
                        latch--;
                    }
                });
            }

            while (latch > 0) {}

            return users;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            callback.onResult(users);
        }
    }

}
