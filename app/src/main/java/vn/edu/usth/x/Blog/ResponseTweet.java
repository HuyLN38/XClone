package vn.edu.usth.x.Blog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.usth.x.HomePage.HomeForYou;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;

public class ResponseTweet extends Fragment {
    private static final String TAG = "Reponse_Tweet";
    private static final int PICK_IMAGE_REQUEST = 1;
    private String tweet_id;

    private List<Tweet> tweetList;
    private TweetAdapterOnline adapter;
    private RecyclerView recyclerView;

    private String API_URL;
    private String API_Like_URL = "https://huyln.info/xclone/api/like/";
    private EditText commentEditText;

    private Button postButton;

    private ImageView avatarImageView;
    private TextView usernameTextView;

    private TextView tweetLinkTextView;
    private TextView responseUsernameView;


    private TextView tweetTextView;

    private TextView timeTextView;
    private ImageView tweetImageView;

    private ImageView selectedImageView;

    private String base64Image = "";

    private Button followButton;

    private static final String ARG_TWEET_ID = "tweet_id";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static ResponseTweet newInstance(Bundle bundle) {
        ResponseTweet fragment = new ResponseTweet();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tweet_id = getArguments().getString(ARG_TWEET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_tweet, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.response_tweet_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tweetList = new ArrayList<>();
        adapter = new TweetAdapterOnline(tweetList);
        recyclerView.setAdapter(adapter);
        followButton = view.findViewById(R.id.follow_button);

        followButton.setOnClickListener(v -> {
            // Logic for following/unfollowing
            boolean isFollowing = followButton.getText().toString().equals("Following");

            if (isFollowing) {
                unfollowUser(); // Call the method to unfollow
                followButton.setText("Follow");
            } else {
                followUser(); // Call the method to follow
                followButton.setText("Following");
            }
        });

//        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

//        // Fetch tweets
//        new ResponseTweet.FetchTweetsTask().execute();

        //Refresh
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // call FetchTweetsTask
//                new ResponseTweet.FetchTweetsTask().execute();
//
//                // stop circle loading
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        avatarImageView = view.findViewById(R.id.tweet_avatar);
        usernameTextView = view.findViewById(R.id.tweet_name);
        tweetLinkTextView = view.findViewById(R.id.tweet_username);
        tweetTextView = view.findViewById(R.id.tweet_text);
        timeTextView = view.findViewById(R.id.tweet_time);
        tweetImageView = view.findViewById(R.id.tweet_image);

        ImageView turnBackButton = view.findViewById(R.id.reponse_turn_back);
        turnBackButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            // Pop the back stack to go back to the previous fragment
            fragmentManager.popBackStack();
        });

        // Get in4 from Bundle
        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String tweetLink = args.getString("tweetLink");
            String tweetText = args.getString("tweetText");
            String time = args.getString("time");
            tweet_id = args.getString("id");

            usernameTextView.setText(username);
            tweetLinkTextView.setText(tweetLink);
            tweetTextView.setText(tweetText);
            timeTextView.setText(time);

            byte[] avatarByteArray = args.getByteArray("avatar");
            if (avatarByteArray != null) {
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarByteArray, 0, avatarByteArray.length);
                avatarImageView.setImageBitmap(avatarBitmap);
            }

            byte[] imageByteArray = args.getByteArray("tweetImage");
            if (imageByteArray != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                tweetImageView.setImageBitmap(imageBitmap);
                tweetImageView.setVisibility(View.VISIBLE);
            } else {
                tweetImageView.setVisibility(View.GONE);
            }

            if (tweet_id != null && !tweet_id.isEmpty()) {
                API_URL = "https://huyln.info/xclone/api/tweets/" + tweet_id + "/comments";
                new ResponseTweet.FetchTweetsTask().execute();
                Log.d(TAG, " tweet: " + API_URL);
            }
        }
    }

    private class FetchTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {
        @Override
        protected List<Tweet> doInBackground(Void... voids) {
            List<Tweet> fetchedTweets = new ArrayList<>();
            try {
                URL url = new URL(API_URL);
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
                        Bitmap imageBitmap = fetchBitmapFromBase64(tweetJson.optString("image_url", null));
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
                                imageBitmap
                        );
                        tweet.setLikeCount(likeCount);
                        Log.d("Tweet", "Tweet: " + tweet);
                        fetchedTweets.add(tweet);
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("FetchTweetsTask", "Error fetching tweets: " + e.getMessage());
            }
            return fetchedTweets;
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
                    Log.d("FetchLikesTask", "Likes Array Length: " + likeArray.length());
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


        private Bitmap fetchBitmapFromBase64(String base64String) {
            if (base64String != null && !base64String.isEmpty()) {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Tweet> tweets) {
            if (tweets != null && !tweets.isEmpty()) {
                tweetList.clear();
                tweetList.addAll(tweets);
                adapter.notifyDataSetChanged();
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

    private void followUser() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://huyln.info/xclone/api/follow/" + tweet_id);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.getResponseCode(); // Send request
                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Error following user: " + e.getMessage());
                }
            }
        });
    }

    private void unfollowUser() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://huyln.info/xclone/api/unfollow/" + tweet_id);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.getResponseCode(); // Send request
                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Error unfollowing user: " + e.getMessage());
                }
            }
        });
    }
}