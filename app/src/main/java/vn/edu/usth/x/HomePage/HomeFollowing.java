package vn.edu.usth.x.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;
import vn.edu.usth.x.Utils.UserFunction;

public class HomeFollowing extends Fragment {
    private RecyclerView recyclerView;
    private TweetAdapterOnline adapter;
    private List<Tweet> tweetList;
    private static final String API_Tweet_URL = "https://huyln.info/xclone/api/tweets/following";
    private static final int PAGE_SIZE = 3;
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
            @SuppressLint({"StaticFieldLeak", "NotifyDataSetChanged"})
            @Override
            public void onRefresh() {
                // Clear the existing tweet list
                tweetList.clear();
                currentPage = 1;
                recyclerView.getRecycledViewPool().clear();
                adapter.notifyDataSetChanged();
                fetchTweets(currentPage);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() { // Allow vertical scrolling
                return true;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition >= tweetList.size()-1) {
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

    @SuppressLint("StaticFieldLeak")
    public class FetchTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {
        private final int page;

        public FetchTweetsTask(int page) {
            this.page = page;
        }

        @Override
        protected List<Tweet> doInBackground(Void... voids) {
            List<Tweet> fetchedTweets = new ArrayList<>();
            try {
                String urlString = API_Tweet_URL + "?page=" + page + "&size=" + PAGE_SIZE + "&sort=created_at&order=desc&viewas=" + UserFunction.getUserId(requireContext());
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
                                        avatarBitmap.set(bitmap);
                                    } else
                                        avatarBitmap.set(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
                                });

                        Bitmap mediaBitmap = fetchBitmapFromBase64(tweetJson.optString("media_url", null));

                        String timeAgo = formatTimeAgo(tweetJson.getString("created_at"));

                        String tweetId = tweetJson.getString("id");

                        requireContext();
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        String userId = sharedPreferences.getString("userId", null);
                        if (userId != null) {
                            Log.d("UserId", "Retrieved User ID: " + userId);
                        } else {
                            Log.e("UserId", "User ID not found in SharedPreferences");
                        }

                        int likeCount = tweetJson.getInt("likes_count");
                        boolean isTweetLikedByUser = tweetJson.getBoolean("user_has_liked");
                        int commentCount = tweetJson.getInt("reply_count");
                        int reTweetCount = tweetJson.getInt("retweet_count");
                        int seenCount = tweetJson.getInt("view_count");

                        Tweet tweet = new Tweet(
                                tweetId,
                                tweetJson.getString("user_id"),
                                avatarBitmap,
                                tweetJson.getString("display_name"),
                                tweetJson.getString("username"),
                                tweetJson.getString("content"),
                                timeAgo,
                                mediaBitmap,
                                likeCount,
                                isTweetLikedByUser,
                                commentCount,
                                reTweetCount,
                                seenCount,
                                tweetJson.getBoolean("user_follows_author")
                        );
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

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<Tweet> fetchedTweets) {
            super.onPostExecute(fetchedTweets);
            if (fetchedTweets != null && !fetchedTweets.isEmpty()) {
                tweetList.addAll(fetchedTweets);
                recyclerView.getRecycledViewPool().clear();
                adapter.notifyDataSetChanged();
            }
            isLoading = false;
        }
    }

    private String formatTimeAgo(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(timestamp);
            long timeInMillis = Objects.requireNonNull(date).getTime();
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