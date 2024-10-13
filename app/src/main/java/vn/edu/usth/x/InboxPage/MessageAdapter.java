package vn.edu.usth.x.InboxPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.usth.x.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.isSentByUser()) {
            holder.textMessageSent.setText(message.getText());
            holder.textMessageSent.setVisibility(View.VISIBLE);
            holder.textMessageReceived.setVisibility(View.GONE);
        } else {
            holder.textMessageReceived.setText(message.getText());
            holder.textMessageReceived.setVisibility(View.VISIBLE);
            holder.textMessageSent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView textMessageSent, textMessageReceived;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageSent = itemView.findViewById(R.id.textMessageSent);
            textMessageReceived = itemView.findViewById(R.id.textMessageReceived);
        }
    }
}
