package vn.edu.usth.x.NotificationPage;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

public class NotificationRepository {
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private static final int PAGE_SIZE = 10;

    private final MutableLiveData<LoadingState> loadingState = new MutableLiveData<>();
    private final MutableLiveData<List<NotificationModel>> notifications = new MutableLiveData<>();
    private boolean hasNextPage = true;
    private int currentPage = 1;
    private boolean isLoading = false;

    public LiveData<LoadingState> getLoadingState() {
        return loadingState;
    }

    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    @SuppressLint("StaticFieldLeak")
    public void loadNotifications(String userId, boolean refresh) {
        if (isLoading) return;
        if (refresh) {
            currentPage = 1;
            hasNextPage = true;
        }
        if (!hasNextPage) return;

        isLoading = true;
        loadingState.setValue(LoadingState.LOADING);

        @SuppressLint("DefaultLocale") String url = String.format("%s/notifications/%s?page=%d&size=%d",
                BASE_URL, userId, currentPage, PAGE_SIZE);

        new AsyncTask<Void, Void, ApiResponse>() {
            @Override
            protected ApiResponse doInBackground(Void... voids) {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        return new ApiResponse(true, response.toString(), null);
                    } else {
                        return new ApiResponse(false, null,
                                "Server returned code: " + responseCode);
                    }
                } catch (Exception e) {
                    return new ApiResponse(false, null, e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(ApiResponse result) {
                if (result.success) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result.data);
                        JSONObject pagination = jsonResponse.getJSONObject("pagination");
                        JSONArray items = jsonResponse.getJSONArray("items");

                        hasNextPage = pagination.getBoolean("hasNext");
                        List<NotificationModel> newNotifications = parseNotifications(items);

                        if (refresh) {
                            notifications.setValue(newNotifications);
                        } else {
                            List<NotificationModel> currentList =
                                    notifications.getValue() != null ?
                                            new ArrayList<>(notifications.getValue()) :
                                            new ArrayList<>();
                            currentList.addAll(newNotifications);
                            notifications.setValue(currentList);
                        }

                        currentPage++;
                        loadingState.setValue(LoadingState.SUCCESS);
                    } catch (JSONException e) {
                        loadingState.setValue(LoadingState.ERROR);
                    }
                } else {
                    loadingState.setValue(LoadingState.ERROR);
                }
                isLoading = false;
            }
        }.execute();
    }

    private List<NotificationModel> parseNotifications(JSONArray jsonArray) throws JSONException {
        List<NotificationModel> notificationsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            NotificationModel notification = new NotificationModel(
                    json.getString("id"),
                    json.getString("user_id"),
                    json.getString("notifier_id"),
                    json.getString("notifier_username"),
                    json.getString("tweet_id"),
                    json.getString("type"),
                    json.getString("content")
            );
            notification.setIs_read(json.getBoolean("is_read"));
            notificationsList.add(notification);
        }
        return notificationsList;
    }

    public enum LoadingState {
        LOADING,
        SUCCESS,
        ERROR
    }

    private static class ApiResponse {
        final boolean success;
        final String data;
        final String error;

        ApiResponse(boolean success, String data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
    }
}
