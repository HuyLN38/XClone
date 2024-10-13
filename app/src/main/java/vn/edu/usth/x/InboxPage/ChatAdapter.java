package vn.edu.usth.x.InboxPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private Context context;
    private OnChatClickListener chatClickListener;

    public ChatAdapter(List<Chat> chatList, Context context, OnChatClickListener chatClickListener) {
        this.chatList = chatList;
        this.context = context;
        this.chatClickListener = chatClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_box, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.displayName.setText(chat.getDisplayName());
        holder.messagePreview.setText(chat.getLastMessage());
        // Load avatar image if available
        // Set the avatar
        if (chat.getAvatarBitmap() != null) {
            holder.avatarImageView.setImageBitmap(chat.getAvatarBitmap());
        } else {
            // Set a default avatar if none exists
            holder.avatarImageView.setImageResource(R.drawable.potter);
        }

        holder.itemView.setOnClickListener(v -> chatClickListener.onChatClick(chat));
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
