package vn.edu.usth.x.CommunityPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.R;


public class CommunityTweetAdapter extends RecyclerView.Adapter<CommunityTweetAdapter.MyCommunityHolder> {
    private ArrayList<CommunityTweet> communityList;

    public CommunityTweetAdapter(ArrayList<CommunityTweet> communityList){
        this.communityList = communityList;
    }

    @NonNull
    @Override
    public CommunityTweetAdapter.MyCommunityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_community_tweet,parent,false);
        return new CommunityTweetAdapter.MyCommunityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityTweetAdapter.MyCommunityHolder holder, int position) {
        CommunityTweet cmTweet = communityList.get(position);
        holder.community_name.setText(cmTweet.getCommunity());
        holder.community_username.setText(cmTweet.getUsername());
        holder.community_tweetlink.setText(cmTweet.getTweetlink());
        holder.community_tweetText.setText(cmTweet.getTweetText());
        holder.community_time.setText(cmTweet.getTime());
        holder.community_image.setImageResource(cmTweet.getImage());
        holder.community_avatar.setImageResource(cmTweet.getAvatar());
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public static class MyCommunityHolder extends RecyclerView.ViewHolder{
        public TextView community_name;
        public CircleImageView community_avatar;
        public TextView community_username;
        public TextView community_tweetText;
        public TextView community_tweetlink;
        public TextView community_time;
        public ImageView community_image;

        public MyCommunityHolder(@NonNull View itemView) {
            super(itemView);
            community_name = itemView.findViewById(R.id.community_name);
            community_avatar = itemView.findViewById(R.id.community_avatar);
            community_username = itemView.findViewById(R.id.community_username);
            community_tweetText = itemView.findViewById(R.id.community_tweet_text);
            community_tweetlink = itemView.findViewById(R.id.community_tweetlink);
            community_time = itemView.findViewById(R.id.community_time);
            community_image = itemView.findViewById(R.id.community_image);
        }
    }
}
