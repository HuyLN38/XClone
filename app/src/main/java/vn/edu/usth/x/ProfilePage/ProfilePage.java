package vn.edu.usth.x.ProfilePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

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

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;
import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.Utils.UserFunction;
import vn.edu.usth.x.Utils.UserManager;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String API_USER_TWEETS_URL = "https://huyln.info/xclone/api/users/%s/tweets";
    private static final int PAGE_SIZE = 3;

    private RecyclerView recyclerView;
    private TweetAdapterOnline adapter;
    private List<Tweet> tweetList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 1;
    private boolean isLoading = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Initialize UI components
        initializeUI();

        // Set up RecyclerView
        setupRecyclerView();

        // Load user data and tweets
        if (getIntent().getStringExtra("userId") != null) {
            userId = getIntent().getStringExtra("userId");
        } else {
            userId = UserFunction.getUserId(this);
        }
        loadUserProfile();
        fetchTweets(currentPage);

    }

    private void initializeUI() {
        // Initialize back button
        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize Edit Profile button
        MaterialButton editProfileButton = findViewById(R.id.btn_edit_profile);
        editProfileButton.setOnClickListener(v -> {
            // Launch edit profile activity
        });

        // Initialize TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout_profile);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
//                refreshContent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Initialize FAB
        ImageButton fab = findViewById(R.id.add);
        fab.setOnClickListener(v -> {
            // Launch new tweet activity
        });

        // Initialize SwipeRefreshLayout
//        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_tweets);
        tweetList = new ArrayList<>();
        adapter = new TweetAdapterOnline(tweetList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading) {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition >= tweetList.size() - 1) {
                        isLoading = true;
                        fetchTweets(++currentPage);
                    }
                }
            }
        });
    }

    private void loadUserProfile() {
        // Load user avatar
        CircleImageView profileImage = findViewById(R.id.profile_image);
        AvatarManager.getInstance(this)
                .getAvatar(userId)
                .thenAccept(bitmap -> {
                    if (bitmap != null) {
                        runOnUiThread(() -> profileImage.setImageBitmap(bitmap));
                    }
                });

        // Load other user data
        // This would typically come from your API
        TextView usernameView = findViewById(R.id.username_profile);
        TextView displayNameView = findViewById(R.id.display_name);
        TextView joinDateView = findViewById(R.id.join_date);
        TextView followingCountView = findViewById(R.id.follower_count);
        TextView followerCountView = findViewById(R.id.following_count);
        ImageView Tick = findViewById(R.id.verified_tick);

        // Set data from API response
        // This is placeholder data
        if (getIntent().getStringExtra("userId") == null) {
            usernameView.setText("@" + UserManager.getDisplayName());
            displayNameView.setText(UserManager.getCurrentUsername());
            joinDateView.setText("Joined Sep 2024");
            followingCountView.setText(UserManager.getFollowing() + " Following");
            followerCountView.setText(UserManager.getFollowers() + " Followers");
            Tick.setVisibility(UserManager.isIsVerified() ? View.VISIBLE : View.GONE);
        } else {
            usernameView.setText("@" + getIntent().getStringExtra("username"));
            displayNameView.setText(getIntent().getStringExtra("displayName"));
            joinDateView.setText("Joined Sep 2024");
            followingCountView.setText(getIntent().getIntExtra("following", 0) + " Following");
            followerCountView.setText(getIntent().getIntExtra("followers", 0) + " Followers");
            Tick.setVisibility(getIntent().getBooleanExtra("isVerified", false) ? View.VISIBLE : View.GONE);
        }

    }

    private void fetchTweets(int page) {
        new FetchTweetsTask().execute(page);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshContent(int tabPosition) {
        // Clear existing content
        tweetList.clear();
        adapter.notifyDataSetChanged();

        // Load appropriate content based on tab
        switch (tabPosition) {
            case 0: // Posts
                break;
            case 1: // Replies
                fetchReplies();
                break;
            case 2: // Highlights
                fetchHighlights();
                break;
            case 3: // Articles
                fetchArticles();
                break;
            case 4: // Media
                fetchMedia();
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchTweetsTask extends AsyncTask<Integer, Void, List<Tweet>> {
        @Override
        protected List<Tweet> doInBackground(Integer... params) {
            int page = params[0];
            List<Tweet> fetchedTweets = new ArrayList<>();

            try {
                String urlString = String.format(API_USER_TWEETS_URL, userId) +
                        "?page=" + page +
                        "&size=" + PAGE_SIZE +
                        "&sort=created_at&order=desc";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray itemsArray = jsonResponse.getJSONArray("items");

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject tweetJson = itemsArray.getJSONObject(i);

                        // Get avatar bitmap
                        AtomicReference<Bitmap> avatarBitmap = new AtomicReference<>();
                        AvatarManager.getInstance(ProfilePage.this)
                                .getAvatar(tweetJson.getString("user_id"))
                                .thenAccept(avatarBitmap::set);

                        // Get media bitmap if exists
                        Bitmap mediaBitmap = null;
                        if (tweetJson.has("media_url") && !tweetJson.isNull("media_url")) {
                            String mediaUrl = tweetJson.getString("media_url");
                            mediaBitmap = fetchBitmapFromBase64(mediaUrl);

                        }

                        String timeAgo = formatTimeAgo(tweetJson.getString("created_at"));

                        Tweet tweet = new Tweet(
                                tweetJson.getString("id"),
                                tweetJson.getString("user_id"),
                                avatarBitmap,
                                tweetJson.getString("display_name"),
                                tweetJson.getString("username"),
                                tweetJson.getString("content"),
                                timeAgo,
                                mediaBitmap,
                                tweetJson.getInt("likes_count"),
                                tweetJson.getBoolean("user_has_liked"),
                                tweetJson.getInt("reply_count"),
                                tweetJson.getInt("retweet_count"),
                                tweetJson.getInt("view_count")
                        );
                        fetchedTweets.add(tweet);
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching tweets: " + e.getMessage());
            }

            return fetchedTweets;
        }

        @Override
        protected void onPostExecute(List<Tweet> fetchedTweets) {
            if (fetchedTweets != null && !fetchedTweets.isEmpty()) {
                tweetList.addAll(fetchedTweets);
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
            long timeInMillis = date.getTime();
            long now = System.currentTimeMillis();
            long diff = now - timeInMillis;

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
            Log.e(TAG, "Error formatting time: " + e.getMessage());
            return "";
        }
    }

    private Bitmap fetchBitmapFromBase64(String base64String) {
        if (base64String != null && !base64String.isEmpty()) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }

    // Placeholder methods for other tab content
    private void fetchReplies() {

    }

    private void fetchHighlights() {

    }

    private void fetchArticles() {

    }

    private void fetchMedia() {

    }

    @Override
    public void onBackPressed() {
        // Clear the intent
        super.onBackPressed();
        getIntent().removeExtra("userId");
        getIntent().removeExtra("displayName");
        getIntent().removeExtra("username");
        getIntent().removeExtra("isVerified");
        getIntent().removeExtra("following");
        getIntent().removeExtra("followers");

        // End the activity
        finish();
    }
}