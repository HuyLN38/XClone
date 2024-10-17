package vn.edu.usth.x.Tweet;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.Blog.ResponseTweet;
import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Blog.CommentFragment;

public class TweetAdapterOnline  extends RecyclerView.Adapter<TweetAdapterOnline.TweetViewHolder> {
    private static AnimationDrawable animationDrawable;
    private int flag = 0;
    private List<Tweet> tweetList;

    public static class TweetViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView username;
        public TextView tweetText;
        public TextView tweetlink;
        public TextView time;
        public ImageView image;
        public ImageView btnAnim;
        public TextView likeCountView;
        public int likeCount;

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
        }

        public void bind(Tweet tweet) {
            username.setText(tweet.getUsername());
            tweetText.setText(tweet.getTweetText());
            tweetlink.setText(tweet.getTweetlink());
            time.setText(tweet.getTime());
            likeCountView.setText(String.valueOf(tweet.getLikeCount()));
            likeCount =(int) tweet.getLikeCount();

            // Load images using Glide
            Glide.with(itemView.getContext())
                    .load(tweet.getImage_bit())
                    .into(image);

            Glide.with(itemView.getContext())
                    .load(tweet.getAvatar_bit())
                    .into(avatar);

            ImageView bookmarkButton = itemView.findViewById(R.id.bookmark);


            // Set up animation
            btnAnim.setImageResource(R.drawable.animation);
            btnAnim.setOnClickListener(v -> {
                AnimationDrawable animationDrawable = (AnimationDrawable) btnAnim.getDrawable();
                int lastFrameIndex = animationDrawable.getNumberOfFrames() - 1;
                if (animationDrawable.isRunning()) {
                    if (animationDrawable.getCurrent() == animationDrawable.getFrame(lastFrameIndex)) {
                        animationDrawable.selectDrawable(0); // Reset to the first frame
                        animationDrawable.stop();
                        likeCountView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                        likeCount--;
                    }
                } else {
                    if (animationDrawable.getCurrent() == animationDrawable.getFrame(lastFrameIndex)) {
                        animationDrawable.selectDrawable(0); // Reset to the first frame
                        animationDrawable.stop();
                        likeCountView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                    } else {
                        likeCountView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.heart));
                        likeCount++;
                    }
                    animationDrawable.start();
                }
                likeCountView.setText(Integer.toString(likeCount));
            });

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    if (v.isSelected()) {
                        bookmarkButton.setImageResource(R.drawable.bookmark_after);
                    } else {
                        bookmarkButton.setImageResource(R.drawable.bookmark);
                    }
                }
            });

            //comment button
            ImageView commentButton = itemView.findViewById(R.id.comment_button);
            commentButton.setOnClickListener(v -> {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
                fragmentTransaction.addToBackStack(null);

                Bundle bundle = transmitionIn4(tweet);

                //Create a new CommentFragment and set the arguments
                CommentFragment commentFragment = new CommentFragment();
                commentFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.drawer_layout, commentFragment).commit();
            });

            //tweet change to reponse fragment
            LinearLayout tweetContent = itemView.findViewById(R.id.tweet_content);
            tweetContent.setOnClickListener(v -> {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.addToBackStack(null);

                Bundle bundle = transmitionIn4(tweet);

                // Create a new ResponseTweet instance using the newInstance method
                ResponseTweet responseTweet = ResponseTweet.newInstance(bundle);

                // If you still need to pass other information, you can do so like this:
                Bundle additionalInfo = transmitionIn4(tweet);
                responseTweet.getArguments().putAll(additionalInfo);

                fragmentTransaction.replace(R.id.drawer_layout, responseTweet).commit();
            });
        }
        public Bundle transmitionIn4(Tweet tweet){
            //save through bundle to CommentFragment
            Bundle bundle = new Bundle();
            bundle.putString("id", tweet.getTweet_id());
            bundle.putString("username", tweet.getUsername());
            bundle.putString("tweetLink", tweet.getTweetlink());
            bundle.putString("tweetText", tweet.getTweetText());
            bundle.putString("time", tweet.getTime());

            // avatar_bit
            if (tweet.getAvatar_bit() != null) {
                ByteArrayOutputStream avatarStream = new ByteArrayOutputStream();
                tweet.getAvatar_bit().compress(Bitmap.CompressFormat.PNG, 100, avatarStream);
                byte[] avatarByteArray = avatarStream.toByteArray();
                bundle.putByteArray("avatar", avatarByteArray);
            }

            //image_bit
            if (tweet.getImage_bit() != null) {
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                tweet.getImage_bit().compress(Bitmap.CompressFormat.PNG, 100, imageStream);
                byte[] imageByteArray = imageStream.toByteArray();
                bundle.putByteArray("tweetImage", imageByteArray);
            }
            return bundle;
        }
    }

    public TweetAdapterOnline(List<Tweet> tweetList) {
        this.tweetList = tweetList;
    }

    @NonNull
    @Override
    public TweetAdapterOnline.TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);
        return new TweetAdapterOnline.TweetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TweetAdapterOnline.TweetViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

}
