package vn.edu.usth.x.Blog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationModel;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;
import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.Utils.CommentManager;
import vn.edu.usth.x.Utils.GlobalWebSocketManager;
import vn.edu.usth.x.Utils.LikeEventManager;
import vn.edu.usth.x.Utils.UserFunction;
import vn.edu.usth.x.Utils.UserManager;

public class ResponseTweet extends Fragment implements CommentManager.CommentUpdateListener {
    private static final String TAG = "ResponseTweet";
    private View rootView;
    private ViewHolder viewHolder;
    private final Object lock = new Object();
    private String tweet_id;
    private boolean isDataBound = false;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String base64Image = "";
    Bundle args;
    private boolean isLiked = false;
    private int likeCount = 0;
    private int commentCount = 0;
    private int seenCount = 0;
    private static final String API_REPLIES_URL = "https://huyln.info/xclone/api/tweets/%s/replies";

    public static ResponseTweet newInstance(Bundle args) {
        ResponseTweet fragment = new ResponseTweet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCommentUpdated(String tweetId) {
        commentCount++;
        rootView.post(() -> viewHolder.commentCountView.setText(String.valueOf(commentCount)));
    }

    private static class ViewHolder {
        final ImageView avatarImageView;
        final TextView usernameTextView;
        final TextView tweetLinkTextView;
        final TextView tweetTextView;
        final TextView timeTextView;
        final RecyclerView recyclerView;
        final EditText replyEditText;
        final ImageView addMediaButton;
        final ImageView selectedMediaPreview;
        final ImageView postReplyButton;
        final ImageView tweetImageView;
        final Button followButton;
        TweetAdapterOnline adapter;
        final TextView seenCountView;
        final ImageView likeButton;
        final TextView likeCountView;
        final TextView commentCountView;
        final ImageView bookmarkButton;

        ViewHolder(View view) {
            avatarImageView = view.findViewById(R.id.tweet_avatar);
            usernameTextView = view.findViewById(R.id.tweet_name);
            tweetLinkTextView = view.findViewById(R.id.tweet_username);
            tweetTextView = view.findViewById(R.id.tweet_text);
            timeTextView = view.findViewById(R.id.tweet_time);
            tweetImageView = view.findViewById(R.id.tweet_image);
            followButton = view.findViewById(R.id.follow_button);
            recyclerView = view.findViewById(R.id.response_tweet_recycle);
            likeButton = view.findViewById(R.id.btn_anim);
            seenCountView = view.findViewById(R.id.view_count_root);
            likeCountView = view.findViewById(R.id.like_count);
            bookmarkButton = view.findViewById(R.id.bookmark);
            replyEditText = view.findViewById(R.id.edit_text_comment);
            commentCountView = view.findViewById(R.id.comment_count_root);
            addMediaButton = view.findViewById(R.id.add_picture_button_comment);
            selectedMediaPreview = view.findViewById(R.id.previewImage);
            postReplyButton = view.findViewById(R.id.add_button);

            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            adapter = new TweetAdapterOnline(new ArrayList<>());
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        imageUri
                );
                viewHolder.selectedMediaPreview.setImageBitmap(bitmap);
                viewHolder.selectedMediaPreview.setVisibility(View.VISIBLE);
                base64Image = bitmapToBase64(bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Error processing image: " + e.getMessage());
                Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_response_tweet, container, false);
        viewHolder = new ViewHolder(rootView);

        setupButtons();
        setupLikeButton();
        setupBookmarkButton();
        setupRecyclerView();
        setupReplyUI();


        return rootView;
    }

    private void setupReplyUI() {
        viewHolder.addMediaButton.setOnClickListener(v -> openGallery());

        viewHolder.postReplyButton.setOnClickListener(v -> {
            String content = viewHolder.replyEditText.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Please enter some content", Toast.LENGTH_SHORT).show();
                return;
            }
            postReply(content);
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void postReply(String content) {
        String userId = UserFunction.getUserId(requireContext());
        if (userId == null || tweet_id == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable post button to prevent double posting
        viewHolder.postReplyButton.setEnabled(false);

        String url = "https://huyln.info/xclone/api/tweets";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("content", content);
            jsonBody.put("reply_to_tweet_id", tweet_id);

            if (!base64Image.isEmpty()) {
                jsonBody.put("media_url", base64Image);
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        Log.d(TAG, "Reply posted successfully");
                        Toast.makeText(getContext(), "Reply posted successfully", Toast.LENGTH_SHORT).show();
                        // Clear the input fields
                        viewHolder.replyEditText.setText("");
                        viewHolder.selectedMediaPreview.setVisibility(View.GONE);
                        base64Image = "";
                        // Refresh the replies
                        new FetchRepliesTask().execute(tweet_id);
                        viewHolder.postReplyButton.setEnabled(true);
                    },
                    error -> {
                        Log.e(TAG, "Error posting reply: " + error.getMessage());
                        Toast.makeText(getContext(), "Error posting reply", Toast.LENGTH_SHORT).show();
                        viewHolder.postReplyButton.setEnabled(true);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating reply request", e);
            Toast.makeText(getContext(), "Error creating reply", Toast.LENGTH_SHORT).show();
            viewHolder.postReplyButton.setEnabled(true);
        }
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void setupRecyclerView() {
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewHolder.adapter = new TweetAdapterOnline(new ArrayList<>());
        viewHolder.recyclerView.setAdapter(viewHolder.adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isDataBound) {
            bindData();
        }

        // Fetch replies after binding data
        if (tweet_id != null) {
            new FetchRepliesTask().execute(tweet_id);
        }

        startPostponedEnterTransition();
    }

    private class FetchRepliesTask extends AsyncTask<String, Void, List<Tweet>> {
        @Override
        protected List<Tweet> doInBackground(String... params) {
            List<Tweet> replies = new ArrayList<>();
            String tweetId = params[0];

            try {
                String urlString = String.format(API_REPLIES_URL, tweetId);
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonResponse.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject replyJson = jsonArray.getJSONObject(i);

                        // Convert base64 avatar to bitmap
                        AtomicReference<Bitmap> avatarBitmap = new AtomicReference<>();

                        // Fetch and decode avatar
                        AvatarManager.getInstance(getContext())
                                .getAvatar(replyJson.getString("user_id"))
                                .thenAccept(bitmap -> {
                                    if (bitmap != null) {
                                        avatarBitmap.set(bitmap);
                                    } else
                                        avatarBitmap.set(BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
                                });

                        // Convert base64 media to bitmap if exists
                        Bitmap mediaBitmap = null;
                        if (replyJson.has("media_url") && !replyJson.isNull("media_url")) {
                            mediaBitmap = fetchBitmapFromBase64(replyJson.getString("media_url"));
                        }

                        String timeAgo = formatTimeAgo(replyJson.getString("created_at"));

                        Tweet reply = new Tweet(
                                replyJson.getString("id"),
                                replyJson.getString("user_id"),
                                avatarBitmap,
                                replyJson.getString("display_name"),
                                replyJson.getString("username"),
                                replyJson.getString("content"),
                                timeAgo,
                                mediaBitmap,
                                replyJson.getInt("likes_count"),
                                replyJson.getBoolean("user_has_liked"),
                                replyJson.getInt("reply_count"),
                                replyJson.getInt("retweet_count"),
                                replyJson.getInt("view_count")
                        );
                        replies.add(reply);
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching replies: " + e.getMessage());
            }

            return replies;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<Tweet> replies) {
            if (viewHolder != null && replies != null && !replies.isEmpty()) {
                viewHolder.adapter = new TweetAdapterOnline(replies);
                viewHolder.recyclerView.setAdapter(viewHolder.adapter);
                viewHolder.adapter.notifyDataSetChanged();
            }
        }
    }

    private Bitmap fetchBitmapFromBase64(String base64String) {
        if (base64String != null && !base64String.isEmpty()) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
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

    private void setupButtons() {
        ImageView turnBackButton = rootView.findViewById(R.id.reponse_turn_back);
        turnBackButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        viewHolder.followButton.setOnClickListener(v -> {
            boolean isFollowing = viewHolder.followButton.getText().toString().equals("Following");
            viewHolder.followButton.setText(isFollowing ? "Follow" : "Following");
            if (isFollowing) {
                unfollowUser();
            } else {
                followUser();
            }
        });
    }

    private void setupLikeButton() {
        viewHolder.likeButton.setImageResource(R.drawable.animation);
        updateLikeState();

        viewHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick());
    }

    private void setupBookmarkButton() {
        viewHolder.bookmarkButton.setOnClickListener(v -> {
            boolean isBookmarked = viewHolder.bookmarkButton.isSelected();
            viewHolder.bookmarkButton.setSelected(!isBookmarked);
            viewHolder.bookmarkButton.setImageResource(!isBookmarked ?
                    R.drawable.bookmark_after : R.drawable.bookmark);
        });
    }

    private void updateLikeState() {
        Context context = requireContext();
        int textColor = isLiked ?
                ContextCompat.getColor(context, R.color.heart) :
                ContextCompat.getColor(context, R.color.gray);
        viewHolder.likeCountView.setTextColor(textColor);
        viewHolder.likeCountView.setText(String.valueOf(likeCount));

        if (isLiked) {
            viewHolder.likeButton.setImageResource(R.drawable.liked);
        }
    }

    private void handleLikeButtonClick() {
        viewHolder.likeButton.setImageResource(R.drawable.animation);
        AnimationDrawable anim = (AnimationDrawable) viewHolder.likeButton.getDrawable();
        anim.selectDrawable(0);

        if (isLiked) {
            handleUnlike(anim);
        } else {
            if (viewHolder.likeButton.getDrawable() == ContextCompat.getDrawable(requireContext(), R.drawable.liked)) {
                handleUnlike(anim);
            } else {
                handleLike(anim);
            }
        }
    }

    private void handleUnlike(AnimationDrawable anim) {
        isLiked = false;
        viewHolder.likeButton.setImageResource(R.drawable.animation);
        anim.selectDrawable(0);
        anim.stop();
        viewHolder.likeCountView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        updateLikeCount();
    }

    private void handleLike(AnimationDrawable anim) {
        isLiked = true;
        viewHolder.likeCountView.setTextColor(ContextCompat.getColor(requireContext(), R.color.heart));
        anim.start();
        updateLikeCount();
        args = getArguments();
        GlobalWebSocketManager.getInstance().sendNotification(

                new NotificationModel(
                        args.getString("user_id"),
                        UserFunction.getUserId(this.requireContext()),
                        UserManager.getCurrentUsername(),
                        args.getString("id"),
                        "like",
                        "liked your tweet"
                )
        );
    }

    private void updateLikeCount() {
        try {
            likeCount += (isLiked ? 1 : -1);
            viewHolder.likeCountView.setText(String.valueOf(likeCount));
            LikeEventManager.getInstance().notifyLikeUpdate(tweet_id, isLiked, likeCount);

            toggleLikeOnServer();
        } catch (Exception e) {
            Log.e(TAG, "Error updating like count", e);
        }
    }

    private void toggleLikeOnServer() {
        String userId = UserFunction.getUserId(requireContext());
        if (userId == null || tweet_id == null) return;

        String url = "https://huyln.info/xclone/api/like";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("tweet_id", tweet_id);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            boolean liked = response.getBoolean("liked");
                            Log.d(TAG, "Like status updated: " + liked);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing like response", e);
                        }
                    },
                    error -> Log.e(TAG, "Error toggling like: " + error.getMessage())
            );

            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating like request", e);
        }
    }

    private void bindData() {
        Bundle args = getArguments();
        if (args == null) return;

        tweet_id = args.getString("id", "");
        viewHolder.usernameTextView.setText(args.getString("username", ""));
        viewHolder.tweetLinkTextView.setText(args.getString("tweetLink", ""));
        viewHolder.tweetTextView.setText(args.getString("tweetText", ""));
        viewHolder.timeTextView.setText(args.getString("time", ""));
        viewHolder.avatarImageView.setImageBitmap(args.getParcelable("avatarBitmap"));
        viewHolder.tweetImageView.setImageBitmap(args.getParcelable("imageBitmap"));
        Log.e("TESTT", String.valueOf(args.getInt("ViewCount", 0)));
        Log.e("TESTT", String.valueOf(args.getInt("likeCount", 0)));
        ;
        Log.e("TESTT", String.valueOf(args.getInt("CommentCount", 0)));
        viewHolder.seenCountView.setText(String.valueOf(args.getInt("ViewCount")));

        viewHolder.commentCountView.setText(String.valueOf(args.getInt("CommentCount")));

        viewHolder.likeCountView.setText(String.valueOf(args.getInt("likeCount")));

        likeCount = args.getInt("likeCount", 0);
        commentCount = args.getInt("commentCount", 0);
        seenCount = args.getInt("seenCount", 0);

        isLiked = args.getBoolean("isLiked", false);
        updateLikeState();

        Bitmap avatarBitmap = args.getParcelable("avatarBitmap");
        if (avatarBitmap != null) {
            viewHolder.avatarImageView.setImageBitmap(avatarBitmap);
        }

        Bitmap imageBitmap = args.getParcelable("imageBitmap");
        if (imageBitmap != null) {
            viewHolder.tweetImageView.setImageBitmap(imageBitmap);
            viewHolder.tweetImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tweetImageView.setVisibility(View.GONE);
        }

        isDataBound = true;
    }

    private void followUser() {
        if (tweet_id == null) return;
        // Implementation of follow API call
    }

    private void unfollowUser() {
        if (tweet_id == null) return;
        // Implementation of unfollow API call
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewHolder = null;
    }

}