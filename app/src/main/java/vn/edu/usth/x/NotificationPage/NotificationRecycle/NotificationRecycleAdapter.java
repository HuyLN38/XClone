package vn.edu.usth.x.NotificationPage.NotificationRecycle;

import android.util.Log;
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
import vn.edu.usth.x.Utils.AvatarManager;

public class NotificationRecycleAdapter extends RecyclerView.Adapter<NotificationRecycleAdapter.MyNotificationHolder> {

    private ArrayList<NotificationModel> notificationList;

    public NotificationRecycleAdapter(ArrayList<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public MyNotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notification, parent, false);
        return new MyNotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotificationHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        // Set the data
        holder.notification_username.setText(notification.getNotifier_username());
        holder.notification_tweetText.setText(notification.getContent());

        // Set type icon based on notification type
        setTypeIcon(holder.type, notification.getType());


        holder.itemView.setBackgroundResource(
                notification.isIs_read() ?
                        R.color.background_read :
                        R.color.background_unread
        );

        // Load avatar with error handling
        AvatarManager.getInstance(holder.itemView.getContext())
                .getAvatar(notification.getNotifier_id())
                .thenAccept(bitmap -> {
                    if (bitmap != null) {
                        holder.notificaiton_avatar.setImageBitmap(bitmap);
                    } else {
                        holder.notificaiton_avatar.setImageResource(R.drawable.avatar);
                    }
                })
                .exceptionally(throwable -> {
                    Log.e("NotificationAdapter", "Error loading avatar", throwable);
                    holder.notificaiton_avatar.setImageResource(R.drawable.avatar);
                    return null;
                });
    }

    private void setTypeIcon(ImageView typeIcon, String type) {
        int iconRes;
        switch (type.toUpperCase()) {
            case "LIKE":
                iconRes = R.drawable.heart; // or your like icon
                break;
            case "COMMENT":
                iconRes = R.drawable.comment_icon; // or your retweet icon
                break;
            case "FOLLOW":
                iconRes = R.drawable.user; // or your follow icon
                break;
            default:
                iconRes = R.drawable.star; // default notification icon
                break;
        }
        typeIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class MyNotificationHolder extends RecyclerView.ViewHolder {
        public CircleImageView notificaiton_avatar;
        public ImageView type;
        public TextView notification_username;
        public TextView notification_tweetText;

        public MyNotificationHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            notificaiton_avatar = itemView.findViewById(R.id.notification_avatar);
            notification_username = itemView.findViewById(R.id.notification_username);
            notification_tweetText = itemView.findViewById(R.id.notification_tweet_text);
        }
    }
}