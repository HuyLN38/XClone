package vn.edu.usth.x.Tweet;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.Blog.ResponseTweet;
import vn.edu.usth.x.Comment.CommentFragment;
import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationModel;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.CommentManager;
import vn.edu.usth.x.Utils.GlobalWebSocketManager;
import vn.edu.usth.x.Utils.LikeEventManager;
import vn.edu.usth.x.Utils.UserFunction;
import vn.edu.usth.x.Utils.UserManager;

public class TweetAdapterOnline extends RecyclerView.Adapter<TweetAdapterOnline.TweetViewHolder>
        implements LikeEventManager.LikeUpdateListener, CommentManager.CommentUpdateListener {
    private static final String TAG = "TweetAdapterOnline";
    private final List<Tweet> tweetList;
    private final Object lock = new Object(); // For thread safety

    public TweetAdapterOnline(List<Tweet> tweetList) {
        this.tweetList = tweetList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LikeEventManager.getInstance().addListener(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        LikeEventManager.getInstance().removeListener(this);
    }

    @Override
    public void onLikeUpdated(String tweetId, boolean isLiked, int newLikeCount) {
        synchronized (lock) {
            for (int i = 0; i < tweetList.size(); i++) {
                Tweet tweet = tweetList.get(i);
                if (tweet.getTweet_id().equals(tweetId)) {
                    tweet.setLiked(isLiked);
                    tweet.setLikeCount(newLikeCount);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        synchronized (lock) {
            if (position < 0 || position >= tweetList.size()) {
                Log.e(TAG, "Invalid position: " + position);
                return;
            }
            Tweet tweet = tweetList.get(position);
            holder.bind(tweet);
        }
    }

    @Override
    public int getItemCount() {
        synchronized (lock) {
            return tweetList.size();
        }
    }

    @Override
    public void onCommentUpdated(String tweetId) {
        synchronized (lock) {
            for (int i = 0; i < tweetList.size(); i++) {
                Tweet tweet = tweetList.get(i);
                if (tweet.getTweet_id().equals(tweetId)) {
                    tweet.setCommentCount(tweet.getCommentCount() + 1);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public static class TweetViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView avatar;
        private final TextView username;
        private final TextView tweetText;
        private final TextView tweetlink;
        private final TextView commentCount;
        private final TextView reTweetCount;
        private final TextView seenCount;
        private final TextView time;
        private final ImageView image;
        private final ImageView btnAnim;
        private final TextView likeCountView;
        private final ImageView bookmarkButton;
        private final ImageView commentButton;
        private final LinearLayout tweetContent;

        public TweetViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            tweetText = itemView.findViewById(R.id.tweet_text);
            tweetlink = itemView.findViewById(R.id.tweetlink);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            btnAnim = itemView.findViewById(R.id.btn_anim);
            likeCountView = itemView.findViewById(R.id.like_count);
            bookmarkButton = itemView.findViewById(R.id.bookmark);
            commentButton = itemView.findViewById(R.id.comment_button);
            tweetContent = itemView.findViewById(R.id.tweet_content);
            commentCount = itemView.findViewById(R.id.comment_count);
            reTweetCount = itemView.findViewById(R.id.reTweet_count);
            seenCount = itemView.findViewById(R.id.seen_count);
        }

        public void bind(Tweet tweet) {
            bindBasicInfo(tweet);
            bindImages(tweet);
            initializeLikeButton(tweet);
            initializeBookmarkButton();
            initializeCommentButton(tweet);
            initializeTweetContent(tweet);
        }

        private void bindBasicInfo(Tweet tweet) {
            username.setText(tweet.getUsername());
            tweetText.setText(tweet.getTweetText());
            tweetlink.setText(tweet.getTweetlink());
            commentCount.setText(String.valueOf(tweet.getCommentCount()));
            reTweetCount.setText(String.valueOf(tweet.getReTweetCount()));
            seenCount.setText(String.valueOf(tweet.getSeenCount()));
            time.setText(tweet.getTime());
            likeCountView.setText(String.valueOf(tweet.getLikeCount()));
        }

        private void bindImages(Tweet tweet) {
            // Load tweet image
            if (tweet.getImage_bit() != null) {
                Glide.with(itemView.getContext())
                        .load(tweet.getImage_bit())
                        .into(image);
                image.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.GONE);
            }

            // Load avatar
            Glide.with(itemView.getContext())
                    .load(tweet.getAvatar_bit())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(avatar);
        }

        private void initializeLikeButton(Tweet tweet) {
            Context context = itemView.getContext();
            btnAnim.setImageResource(R.drawable.animation);
            updateLikeState(tweet, context);

            btnAnim.setOnClickListener(v -> handleLikeButtonClick(tweet));
        }

        private void updateLikeState(Tweet tweet, Context context) {
            int textColor = tweet.isLiked() ?
                    ContextCompat.getColor(context, R.color.heart) :
                    ContextCompat.getColor(context, R.color.gray);
            likeCountView.setTextColor(textColor);

            if (tweet.isLiked()) {
                btnAnim.setImageResource(R.drawable.liked);
                Log.d(TAG, "Animation started for liked tweet");
            }
        }

        private void handleLikeButtonClick(Tweet tweet) {
            btnAnim.setImageResource(R.drawable.animation);

            AnimationDrawable anim = (AnimationDrawable) btnAnim.getDrawable();
            anim.selectDrawable(0);
            anim.start();

            boolean isCurrentlyLiked = tweet.isLiked();

            if (isCurrentlyLiked) {
                    handleUnlike(tweet, anim);
            } else {
                if ((btnAnim.getDrawable() == ContextCompat.getDrawable(itemView.getContext(), R.drawable.liked))) {
                    handleUnlike(tweet, anim);
                } else {
                    handleLike(tweet, anim);
                }
            }
        }

        private void handleUnlike(Tweet tweet, AnimationDrawable anim) {
            tweet.setLiked(false);
            btnAnim.setImageResource(R.drawable.animation);
            anim.selectDrawable(0);
            anim.stop();
            likeCountView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
            updateLikeCount(tweet);

        }

        private void handleLike(Tweet tweet, AnimationDrawable anim) {
            tweet.setLiked(true);
            likeCountView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.heart));
            anim.start();
            GlobalWebSocketManager.getInstance().sendNotification(
                    new NotificationModel(
                    tweet.getUser_id(),
                    UserFunction.getUserId(itemView.getContext()),
                    UserManager.getCurrentUsername(),
                    tweet.getTweet_id(),
                    "like",
                    "liked your tweet"
                    )
            );
            updateLikeCount(tweet);
        }

        private void updateLikeCount(Tweet tweet) {
            try {
                int newCount = tweet.getLikeCount() + (tweet.isLiked() ? 1 : -1);
                tweet.setLikeCount(newCount);
                likeCountView.setText(String.valueOf(newCount));
                toggleLikeOnServer(UserFunction.getUserId(itemView.getContext()), tweet.getTweet_id());
            } catch (Exception e) {
                Log.e(TAG, "Error updating like count", e);
            }
        }

        private void toggleLikeOnServer(String userId, String tweetId) {
            if (userId == null || tweetId == null) {
                Log.e(TAG, "Invalid user ID or tweet ID");
                return;
            }

            String url = "https://huyln.info/xclone/api/like";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("user_id", userId);
                jsonBody.put("tweet_id", tweetId);

                RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
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

        private void initializeBookmarkButton() {
            bookmarkButton.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
                bookmarkButton.setImageResource(v.isSelected() ?
                        R.drawable.bookmark_after : R.drawable.bookmark);
            });
        }

        private void initializeCommentButton(Tweet tweet) {
            commentButton.setOnClickListener(v -> {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                if (activity != null) {
                    showCommentFragment(activity, tweet);
                }
            });
        }

        private void initializeTweetContent(Tweet tweet) {
            tweetContent.setOnClickListener(v -> {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                if (activity != null) {
                    showResponseTweet(activity, tweet);
                }
            });
        }

        private void showCommentFragment(FragmentActivity activity, Tweet tweet) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);

            CommentFragment commentFragment = new CommentFragment();
            commentFragment.setArguments(createTweetBundle(tweet));

            transaction.replace(R.id.drawer_layout, commentFragment)
                    .addToBackStack(null)
                    .commit();
        }

        private void showResponseTweet(FragmentActivity activity, Tweet tweet) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

            // Pop back stack to HomeFragment
            activity.getSupportFragmentManager().popBackStack("HomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            ResponseTweet responseTweet = ResponseTweet.newInstance(createTweetBundle(tweet));
            transaction.replace(R.id.drawer_layout, responseTweet)
                    .addToBackStack("HomeFragment")
                    .commit();
        }

        private Bundle createTweetBundle(Tweet tweet) {
            Bundle bundle = new Bundle();
            bundle.putString("id", tweet.getTweet_id());
            bundle.putString("user_id", tweet.getUser_id());
            bundle.putString("username", tweet.getUsername());
            bundle.putString("tweetLink", tweet.getTweetlink());
            bundle.putString("tweetText", tweet.getTweetText());
            bundle.putString("time", tweet.getTime());
            bundle.putInt("likeCount", tweet.getLikeCount());
            bundle.putInt("reTweetCount", tweet.getReTweetCount());
            bundle.putInt("CommentCount", tweet.getCommentCount());
            bundle.putInt("ViewCount", tweet.getSeenCount());
            bundle.putBoolean("isLiked", tweet.isLiked());
            bundle.putParcelable("avatarBitmap", tweet.getAvatar_bit());
            bundle.putParcelable("imageBitmap", tweet.getImage_bit());


            // Add avatar bitmap if available
            if (tweet.getAvatar_bit() != null) {
                bundle.putByteArray("avatar", bitmapToByteArray(tweet.getAvatar_bit()));
            }

            // Add tweet image if available
            if (tweet.getImage_bit() != null) {
                bundle.putByteArray("tweetImage", bitmapToByteArray(tweet.getImage_bit()));
            }

            return bundle;
        }

        private byte[] bitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            return stream.toByteArray();
        }
    }
}