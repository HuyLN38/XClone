package vn.edu.usth.x.Tweet;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Comment.CommentFragment;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {

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

        public TweetViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            tweetText = itemView.findViewById(R.id.tweet_text);
            tweetlink = itemView.findViewById(R.id.tweetlink);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            btnAnim = itemView.findViewById(R.id.btn_anim);
        }

        public void bind(Tweet tweet) {
            username.setText(tweet.getUsername());
            tweetText.setText(tweet.getTweetText());
            tweetlink.setText(tweet.getTweetlink());
            time.setText(tweet.getTime());

            // Load images using Glide
            Glide.with(itemView.getContext())
                    .load(tweet.getImage())
                    .into(image);

            Glide.with(itemView.getContext())
                    .load(tweet.getAvatar())
                    .into(avatar);

            ImageView bookmarkButton = itemView.findViewById(R.id.bookmark);
            TextView likeCount = itemView.findViewById(R.id.like_count);

            // Set up animation
            btnAnim.setImageResource(R.drawable.animation);
            btnAnim.setOnClickListener(v -> {
                AnimationDrawable animationDrawable = (AnimationDrawable) btnAnim.getDrawable();
                int lastFrameIndex = animationDrawable.getNumberOfFrames() - 1;
                if (animationDrawable.isRunning()) {
                    if (animationDrawable.getCurrent() == animationDrawable.getFrame(lastFrameIndex)) {
                        animationDrawable.selectDrawable(0); // Reset to the first frame
                        animationDrawable.stop();
                        likeCount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                    }
                } else {
                    if (animationDrawable.getCurrent() == animationDrawable.getFrame(lastFrameIndex)) {
                        animationDrawable.selectDrawable(0); // Reset to the first frame
                        animationDrawable.stop();
                        likeCount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                    } else {
                        likeCount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.heart));
                    }
                    animationDrawable.start();
                }
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
        }
    }

    public TweetAdapter(List<Tweet> tweetList) {
        this.tweetList = tweetList;
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);

        ImageView commentButton = itemView.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) v.getContext();
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.drawer_layout, new CommentFragment()).commit();
        });

        return new TweetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }
}