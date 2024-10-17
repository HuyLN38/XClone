package vn.edu.usth.x.HomePage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapter;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;

public class HomeForYou extends Fragment {
    private RecyclerView recyclerView;
    private TweetAdapterOnline adapter;
    private List<Tweet> tweetList;
    private static final String API_Tweet_URL = "https://huyln.info/xclone/api/tweets/";
    private static final String API_Like_URL = "https://huyln.info/xclone/api/like/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_for_you, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.homeForYouRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tweetList = new ArrayList<>();
        adapter = new TweetAdapterOnline(tweetList);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Fetch tweets
        new FetchTweetsTask().execute();

        //Refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // call FetchTweetsTask
                new FetchTweetsTask().execute();

                // stop circle loading
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private class FetchTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {
        @Override
        protected List<Tweet> doInBackground(Void... voids) {
            List<Tweet> fetchedTweets = new ArrayList<>();
            try {
                URL url = new URL(API_Tweet_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tweetJson = jsonArray.getJSONObject(i);

                        // Fetch and decode avatar
                        Bitmap avatarBitmap = fetchBitmapFromBase64(tweetJson.getString("avatar_url"));

                        // Fetch and decode media
                        Bitmap mediaBitmap = fetchBitmapFromBase64(tweetJson.optString("media_url", null));

                        // Format the created_at timestamp
                        String timeAgo = formatTimeAgo(tweetJson.getString("created_at"));

                        String tweetId = tweetJson.getString("id");

                        int likeCount = fetchLikeCount(tweetId);

                        Tweet tweet = new Tweet(
                                tweetId,
                                avatarBitmap,
                                tweetJson.getString("display_name"),
                                tweetJson.getString("username"),
                                tweetJson.getString("content"),
                                timeAgo,
                                mediaBitmap
                        );
                        tweet.setLikeCount(likeCount);
                        fetchedTweets.add(tweet);
                    }
                }
                conn.disconnect();

            } catch (Exception e) {
                Log.e("FetchTweetsTask", "Error fetching tweets: " + e.getMessage());
            }
            return fetchedTweets;
        }

        private Bitmap fetchBitmapFromBase64(String base64String) {
            if (base64String != null && !base64String.isEmpty()) {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            return null;
        }

        private int fetchLikeCount(String tweetId) {
            int likeCount = 0;
            try {
                // Fetch likes
                URL likeUrl = new URL(API_Like_URL + tweetId);
                HttpURLConnection likeConn = (HttpURLConnection) likeUrl.openConnection();
                likeConn.setRequestMethod("GET");
                int likeResponseCode = likeConn.getResponseCode();
                if (likeResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(likeConn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JSONArray likeArray = new JSONArray(response.toString());
                    for (int i = 0; i < likeArray.length(); i++) {
                        JSONObject likeJson = likeArray.getJSONObject(i);
                        if (likeJson.getString("tweet_id").equals(tweetId)) {
                            likeCount++;
                        }
                    }
                }
                likeConn.disconnect();
            } catch (Exception e) {
                Log.e("FetchLikesTask", "Error fetching likes: " + e.getMessage());
            }
            return likeCount;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            if (tweets != null && !tweets.isEmpty()) {
                tweetList.clear();
                tweetList.addAll(tweets);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private String formatTimeAgo(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(timestamp);
            long timeInMillis = date.getTime();
            long now = System.currentTimeMillis();
            long diff = now - timeInMillis;

            // Convert to appropriate time format
            if (diff < 60000) { // less than 1 minute
                return "just now";
            } else if (diff < 3600000) { // less than 1 hour
                return (diff / 60000) + "m";
            } else if (diff < 86400000) { // less than 24 hours
                return (diff / 3600000) + "h";
            } else { // days
                return (diff / 86400000) + "d";
            }
        } catch (Exception e) {
            Log.e("HomeForYou", "Error formatting time: " + e.getMessage());
            return "";
        }
    }
}