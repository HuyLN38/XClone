package vn.edu.usth.x.InboxPage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.R;

public class InboxFragment extends Fragment implements ChatAdapter.OnChatClickListener{

    private static final int ADD_MESSAGE_REQUEST_CODE = 1;  // Request code for starting add_message_activity
    private CircleImageView addMessageButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        // Initialize the RecyclerView
        chatRecyclerView = rootView.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the chat list
        chatList = new ArrayList<>();
//        chatList.add(new Chat("Eimi Fukada", "You: Wanna hangout with me?"));
//        chatList.add(new Chat("Johnny Sins", "You: Just want to tell you that ilysm"));
//        chatList.add(new Chat("Lionel Messi", "Messi: Mes que un club"));
//        chatList.add(new Chat("The Boy Who Lived", "The Experliamus"));
//        chatList.add(new Chat("Jack Dorsey", "You: I'm gonna block your account"));

        // Set up the adapter
        chatAdapter = new ChatAdapter(chatList, getContext(),this);
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize and set OnClickListener for addMessageButton
        addMessageButton = rootView.findViewById(R.id.add_msg_button);
        addMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), add_message_activity.class);
                startActivityForResult(intent, ADD_MESSAGE_REQUEST_CODE);
            }
        });

        return rootView;
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("USER_NAME", chat.getDisplayName());
        intent.putExtra("RECIPIENT_ID", chat.getRecipientId());// Pass the avatar if needed
        startActivity(intent);
    }

    // Handle result from add_message_activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MESSAGE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Extract data from the intent
            String userName = data.getStringExtra("USER_NAME");
            String message = data.getStringExtra("MESSAGE");
            String avatarFilePath = data.getStringExtra("AVATAR_FILE_PATH");
            String recipientId = data.getStringExtra("RECIPIENT_ID");

            // Decode the image file into a Bitmap
            Bitmap avatarBitmap = null;
            if (avatarFilePath != null) {
                File imgFile = new File(avatarFilePath);
                if (imgFile.exists()) {
                    avatarBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }
            }

            // Add new chat to the list and notify the adapter
            addNewChat(userName, message, avatarBitmap, recipientId);
        }
    }

    // Method to dynamically add a new chat
    private void addNewChat(String displayName, String message, Bitmap avatarBitmap, String recipientId) {
        Chat newChat = new Chat(displayName, message, recipientId);
        newChat.setAvatarBitmap(avatarBitmap);  // Assuming your Chat model has this method
        chatList.add(newChat);
        chatAdapter.notifyItemInserted(chatList.size() - 1);  // Notify adapter to update the RecyclerView
    }
}
