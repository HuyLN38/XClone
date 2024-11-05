package vn.edu.usth.x.InboxPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.usth.x.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final Context context;
    private final List<Message> messages;
    private final String currentUserId;

    // Updated constructor to match the usage pattern
    public MessageAdapter(Context context, List<Message> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getContent());

        // Only show status for sent messages
        if (getItemViewType(position) == VIEW_TYPE_SENT && holder.messageStatus != null) {
            String status = message.getStatus();
            if (status != null) {
                switch (status) {
                    case "sent":
                        holder.messageStatus.setText("✓");
                        break;
                    case "delivered":
                        holder.messageStatus.setText("✓✓");
                        break;
                    case "seen":
                        holder.messageStatus.setText("✓✓");
                        holder.messageStatus.setTextColor(context.getResources().getColor(R.color.light_blue));
                        break;
                }
            }
        }

        // Set timestamp
        if (holder.timestamp != null && message.getTimestamp() != null) {
            holder.timestamp.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageStatus;
        TextView timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    private String formatTimestamp(String timestamp) {
        try {
            long timeInMillis = Long.parseLong(timestamp);
            return android.text.format.DateUtils.getRelativeTimeSpanString(
                    timeInMillis,
                    System.currentTimeMillis(),
                    android.text.format.DateUtils.MINUTE_IN_MILLIS
            ).toString();
        } catch (NumberFormatException e) {
            return "";
        }
    }
}