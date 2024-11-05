package vn.edu.usth.x.HomePage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import vn.edu.usth.x.Login.Data.AvatarManager;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;

public class HomeForYou extends Fragment {
    private RecyclerView recyclerView;
    private TweetAdapterOnline adapter;
    private List<Tweet> tweetList;
    private static final String API_Tweet_URL = "https://huyln.info/xclone/api/tweets/";
    private static final String API_Like_URL = "https://huyln.info/xclone/api/like/";
    private static final int PAGE_SIZE = 5;
    private int currentPage = 1;

    private boolean isLoading = false;

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
        fetchTweets(currentPage);

        // Refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onRefresh() {
                // Clear the existing tweet list
                tweetList.clear();
                currentPage = 1; // Reset to the first page

                // Fetch new tweets
                fetchTweets(currentPage);

                // Stop the refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Scroll down to update new post
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() { // Allow vertical scrolling
                return true;
            }
        };
        // Apply the layout manager to the RecyclerView
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition >= tweetList.size() - 2) {
                        isLoading = true;
                        fetchTweets(++currentPage);
                    }
                }
            }
        });

        return view;
    }

    // Call FetchTweetsTask
    public void fetchTweets(int page) {
        new FetchTweetsTask(page).execute();
    }

    private class FetchTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {
        private int page;

        public FetchTweetsTask(int page) {
            this.page = page;
        }

        @Override
        protected List<Tweet> doInBackground(Void... voids) {
            List<Tweet> fetchedTweets = new ArrayList<>();
            try {
                String urlString = API_Tweet_URL + "?page=" + page + "&size=" + PAGE_SIZE + "&sort=created_at&order=desc";
                URL url = new URL(urlString);
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

                    JSONObject jsonArray = new JSONObject(response.toString());
                    JSONArray itemsArray = jsonArray.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject tweetJson = itemsArray.getJSONObject(i);

                        AtomicReference<Bitmap> avatarBitmap = new AtomicReference<>();

                        // Fetch and decode avatar
                        AvatarManager.getInstance(getContext())
                                .getAvatar(tweetJson.getString("user_id"))
                                .thenAccept(bitmap -> {
                                    if (bitmap != null) {
                                        // Use the bitmap (e.g., set it to an ImageView)
                                        avatarBitmap.set(bitmap);
                                    } else
                                        avatarBitmap.set(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
                                });

                        // Fetch and decode media
                        Bitmap mediaBitmap = fetchBitmapFromBase64(tweetJson.optString("media_url", null));

                        // Format the created_at timestamp
                        String timeAgo = formatTimeAgo(tweetJson.getString("created_at"));

                        String tweetId = tweetJson.getString("id");

                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
                        String userId = sharedPreferences.getString("userId", null);
                        if (userId != null) {
                            Log.d("UserId", "Retrieved User ID: " + userId);
                        } else {
                            Log.e("UserId", "User ID not found in SharedPreferences");
                        }

                        int likeCount = fetchLikeCount(tweetId);

                        Tweet tweet = new Tweet(
                                tweetId,
                                avatarBitmap.get(),
                                tweetJson.getString("display_name"),
                                tweetJson.getString("username"),
                                tweetJson.getString("content"),
                                timeAgo,
                                mediaBitmap
                        );
                        tweet.setLikeCount(likeCount);
                        tweet.setLiked(isTweetLikedByUser(tweetId, userId));
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

        private boolean isTweetLikedByUser(String tweetId, String userId) {
            try {
                Log.d("FetchLikesTask", "Checking if tweet is liked by user: " + userId);
                URL likeUrl = new URL("https://huyln.info/xclone/api/like/" + tweetId);
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
                        if (likeJson.getString("user_id").equals(userId)) {
                            return true; // The tweet is liked by the current user
                        }
                    }
                }
                likeConn.disconnect();
            } catch (Exception e) {
                Log.e("FetchLikesTask", "Error checking if tweet is liked by user: " + e.getMessage());
            }
            Log.d("FetchLikesTask", "Tweet liked by userId: " + userId);
            return false; // The tweet is not liked by the user
        }

        @Override
        protected void onPostExecute(List<Tweet> fetchedTweets) {
            super.onPostExecute(fetchedTweets);
            if (fetchedTweets != null && !fetchedTweets.isEmpty()) {
                // Add the fetched tweets to the existing list
                tweetList.addAll(fetchedTweets);
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }
            isLoading = false; // Reset loading state
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