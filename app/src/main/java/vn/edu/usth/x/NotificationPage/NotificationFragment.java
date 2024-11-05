package vn.edu.usth.x.NotificationPage;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
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

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationRecycleAdapter adapter;
    private ArrayList<NotificationModel> notificationList = new ArrayList<>();

    private static final String API_NOTIFICATION_URL = "https://huyln.info/xclone/api/notifications/userId";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_all, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNofiAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationRecycleAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        loadNotifications();
        return view;
    }

    private void loadNotifications() {
        AsyncTask.execute(() -> {
            try {
                URL url = new URL(API_NOTIFICATION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    NotificationModel notification = new NotificationModel(
                            jsonObject.getString("id"),
                            jsonObject.getString("user_id"),
                            jsonObject.getString("tweet_id"),
                            jsonObject.getString("type"),
                            jsonObject.getString("message")
                    );
                    notificationList.add(notification);
                }

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading notifications", e);
            }
        });
    }
}
