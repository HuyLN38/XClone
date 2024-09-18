package vn.edu.usth.x.Tweet;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.comment.CommentFragment;

// TweetAdapter.java
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {

    private List<Tweet> tweetList;

    public static class TweetViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView username;
        public TextView tweetText;
        public TextView tweetlink;
        public TextView time;
        public ImageView image;

        public TweetViewHolder(View itemView) {
            super( itemView );
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            tweetText = itemView.findViewById(R.id.tweet_text);
            tweetlink = itemView.findViewById(R.id.tweetlink);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);

        }
    }

    public TweetAdapter(List<Tweet> tweetList) {
        this.tweetList = tweetList;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);
        ImageView likeButton = itemView.findViewById(R.id.like);
        TextView likeCount = itemView.findViewById(R.id.like_count);
        ImageView bookmarkButton = itemView.findViewById(R.id.bookmark);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    likeCount.setTextColor(0xFFFF0000);
                    likeButton.setImageResource(R.drawable.heart_after);
                } else {
                    likeCount.setTextColor(0xFFbfbaba);
                    likeButton.setImageResource(R.drawable.heart);
                }
            }
        });

        //BookMark
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

        // Comment
        ImageView commentButton = itemView.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.drawer_layout, new CommentFragment()).commit();

            }
        });

        return new TweetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);
        holder.username.setText(tweet.getUsername());
        holder.tweetText.setText(tweet.getTweetText());
        holder.tweetlink.setText(tweet.getTweetlink());
        holder.time.setText(tweet.getTime());
        // Load images using Glide
        Glide.with(holder.itemView.getContext())
                .load(tweet.getImage())
                .into(holder.image);

        Glide.with(holder.itemView.getContext())
                .load(tweet.getAvatar())
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }
}
