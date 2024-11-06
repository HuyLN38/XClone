package vn.edu.usth.x.Blog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapterOnline;
import vn.edu.usth.x.Utils.UserFunction;

public class ResponseTweet extends Fragment {
    private static final String TAG = "ResponseTweet";
    private View rootView;
    private ViewHolder viewHolder;
    private String tweet_id;
    private boolean isDataBound = false;
    private RequestQueue requestQueue;
    private boolean isLiked = false;
    private int likeCount = 0;

    public static ResponseTweet newInstance(Bundle args) {
        ResponseTweet fragment = new ResponseTweet();
        fragment.setArguments(args);
        return fragment;
    }

    private static class ViewHolder {
        final ImageView avatarImageView;
        final TextView usernameTextView;
        final TextView tweetLinkTextView;
        final TextView tweetTextView;
        final TextView timeTextView;
        final ImageView tweetImageView;
        final Button followButton;
        final RecyclerView recyclerView;
        final TweetAdapterOnline adapter;
        final ImageView likeButton;
        final TextView likeCountView;
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
            likeCountView = view.findViewById(R.id.like_count);
            bookmarkButton = view.findViewById(R.id.bookmark);

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_response_tweet, container, false);
        viewHolder = new ViewHolder(rootView);

        setupButtons();
        setupLikeButton();
        setupBookmarkButton();

        return rootView;
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
    }

    private void updateLikeCount() {
        try {
            likeCount += (isLiked ? 1 : -1);
            viewHolder.likeCountView.setText(String.valueOf(likeCount));
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isDataBound) {
            bindData();
        }

        startPostponedEnterTransition();
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

        likeCount = args.getInt("likeCount", 0);
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