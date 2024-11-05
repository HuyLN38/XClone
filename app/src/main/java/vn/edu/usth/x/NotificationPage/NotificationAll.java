package vn.edu.usth.x.NotificationPage;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationModel;
import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationRecycleAdapter;
import vn.edu.usth.x.R;

public class NotificationAll extends Fragment {

    private RecyclerView recyclerView;
    private NotificationRecycleAdapter adapter;
    private ArrayList<NotificationModel> notificationList = new ArrayList<>();
    private static final String API_URL = "https://huyln.info/xclone/api/notifications"; // URL API thực tế của bạn

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_all, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewNofiAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        adapter = new NotificationRecycleAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Load notifications from API
        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        // AsyncTask to load notifications from API
        new LoadNotificationsTask().execute(API_URL);
    }

    private class LoadNotificationsTask extends AsyncTask<String, Void, List<NotificationModel>> {
        @Override
        protected List<NotificationModel> doInBackground(String... urls) {
            List<NotificationModel> notifications = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(response.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int avatar = R.drawable.fukada; // Placeholder avatar
                        String username = jsonObject.getString("username");
                        String message = jsonObject.getString("message");

                        NotificationModel notification = new NotificationModel(avatar, username, message);
                        notifications.add(notification);
                    }
                } else {
                    Log.e("NotificationAll", "Error: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("NotificationAll", "Exception: " + e.getMessage(), e);
            }
            return notifications;
        }

        @Override
        protected void onPostExecute(List<NotificationModel> notifications) {
            if (notifications.isEmpty()) {
                Toast.makeText(getContext(), "No notifications available", Toast.LENGTH_SHORT).show();
            } else {
                notificationList.clear();
                notificationList.addAll(notifications);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
