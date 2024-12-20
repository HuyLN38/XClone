package vn.edu.usth.x.InboxPage.AddChatActivity;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.ProfilePage.ProfilePage;
import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.R;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private List<UserItem> userList;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserItem user);
    }

    public UserListAdapter(List<UserItem> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem user = userList.get(position);
        holder.displayName.setText(user.getDisplayName());
        holder.username.setText("@" + user.getCurrentUsername());
        Context context = holder.itemView.getContext();

        AtomicReference<Bitmap> bitmapRef = new AtomicReference<>();
        AvatarManager.getInstance(context)
                .getAvatar(user.getCurrentID())
                .thenAccept(bitmap -> {
                    if (bitmap != null) {
                        bitmapRef.set(bitmap);
                        holder.avatar.post(() -> holder.avatar.setImageBitmap(bitmapRef.get()));
                    }
                });

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));

        CircleImageView avatarImageView = holder.avatar;
        avatarImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfilePage.class);
            intent = intent.putExtra("userId", user.getCurrentID());
            intent = intent.putExtra("displayName", user.getDisplayName());
            intent = intent.putExtra("username", user.getCurrentUsername());
            intent = intent.putExtra("isVerified", user.isVerified());
            intent = intent.putExtra("following", user.getFollowing());
            intent = intent.putExtra("followers", user.getFollowers());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView displayName;
        TextView username;

        UserViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.userAvatar);
            displayName = itemView.findViewById(R.id.userDisplayName);
            username = itemView.findViewById(R.id.userUsername);
        }
    }
}