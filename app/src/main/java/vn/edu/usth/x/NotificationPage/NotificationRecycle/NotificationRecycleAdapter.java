package vn.edu.usth.x.NotificationPage.NotificationRecycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.CommunityPage.CommunityTweet;
import vn.edu.usth.x.R;

public class NotificationRecycleAdapter extends RecyclerView.Adapter<NotificationRecycleAdapter.MyNotificationHolder> {

    private ArrayList<NotificationModel> notificationList;

    public NotificationRecycleAdapter(ArrayList<NotificationModel> notificationList){
        this.notificationList=notificationList;
    }

    @NonNull
    @Override
    public MyNotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notification,parent,false);
        return new NotificationRecycleAdapter.MyNotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotificationHolder holder, int position) {
        NotificationModel nofiModel = notificationList.get(position);
        holder.notification_username.setText(nofiModel.getUsername());
        holder.notification_tweetText.setText(nofiModel.getTweetText());
        holder.notificaiton_avatar.setImageResource(nofiModel.getAvatar());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class MyNotificationHolder extends RecyclerView.ViewHolder{
        public CircleImageView notificaiton_avatar;
        public TextView notification_username;

        public TextView notification_tweetText;

        public MyNotificationHolder(@NonNull View itemView) {
            super(itemView);
            notificaiton_avatar = itemView.findViewById(R.id.notification_avatar);
            notification_username = itemView.findViewById(R.id.notification_username);
            notification_tweetText = itemView.findViewById(R.id.notification_tweet_text);
        }
    }

}
