package vn.edu.usth.x.InboxPage.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private final List<Chat> chatList;
    private final OnChatClickListener chatClickListener;
    private Context context;

    public ChatAdapter(List<Chat> chatList, OnChatClickListener chatClickListener) {
        this.chatList = chatList;
        this.chatClickListener = chatClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_box, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.displayName.setText(chat.getDisplayName());
        holder.messagePreview.setText(chat.getLastMessage());

        // Try to load avatar from local storage first
        AvatarManager.getInstance(context)
                .getAvatar(chat.getRecipientId())
                .thenAccept(bitmap -> {
                    if (bitmap != null) {
                        holder.avatarImageView.setImageBitmap(bitmap);
                    }
                });
        holder.itemView.setOnClickListener(v -> {
            if (chatClickListener != null) {
                chatClickListener.onChatClick(chat);
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarImageView;
        TextView displayName, messagePreview;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            displayName = itemView.findViewById(R.id.chatDisplayName);
            messagePreview = itemView.findViewById(R.id.chatMessagePreview);
        }
    }

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }
}