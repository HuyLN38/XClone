package vn.edu.usth.x.InboxPage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.x.InboxPage.AddChatActivity.AddChatActivity;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class InboxFragment extends Fragment implements ChatAdapter.OnChatClickListener {
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private ImageButton addMessageButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserFunction userFunction;
    private ActivityResultLauncher<Intent> addMessageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFunction = new UserFunction();
        requestQueue = Volley.newRequestQueue(requireContext());

        // Initialize the ActivityResultLauncher
        addMessageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        handleAddMessageResult(result.getData());
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        initializeViews(rootView);
        setupRecyclerView();
        setupSwipeRefresh();
        setupAddMessageButton();
        loadChats();

        return rootView;
    }

    private void initializeViews(View rootView) {
        chatRecyclerView = rootView.findViewById(R.id.chatRecyclerView);
        addMessageButton = rootView.findViewById(R.id.add_msg_button);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        chatList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(chatList, this);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadChats);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue,
                R.color.light_purple,
                R.color.light_yellow
        );
    }

    private void setupAddMessageButton() {
        addMessageButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddChatActivity.class);
            startActivity(intent);
        });
    }

    private void loadChats() {
        String userId = UserFunction.getUserId(requireContext());
        if (userId == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        String url = BASE_URL + "/messages/conversations/" + userId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Chat> newChats = processChatsResponse(response);
                        updateChatList(newChats); // Use updateChatList here
                    } catch (JSONException e) {
                        handleError("Error processing chats data");
                    }
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    handleError("Error loading chats");
                    swipeRefreshLayout.setRefreshing(false);
                }
        );

        requestQueue.add(request);
    }

    private List<Chat> processChatsResponse(JSONArray response) throws JSONException {
        List<Chat> newChats = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject chatData = response.getJSONObject(i);

            String otherUserId = chatData.getString("recipient_id");
            String displayName = chatData.getString("recipient_display_name");
            String avatarUrl = chatData.optString("recipient_avatar_url");
            String lastMessage = chatData.getString("content");

            Chat chat = new Chat(displayName, lastMessage, otherUserId);
            loadAvatarAndAddChat(chat, avatarUrl);
            newChats.add(chat);
        }
        return newChats;
    }

    private void handleAddMessageResult(Intent data) {
        if (data != null) {
            String userName = data.getStringExtra("USER_NAME");
            String message = data.getStringExtra("MESSAGE");
            String avatarUrl = data.getStringExtra("AVATAR_URL");
            String recipientId = data.getStringExtra("RECIPIENT_ID");

            Chat newChat = new Chat(userName, message, recipientId);
            loadAvatarAndAddChat(newChat, avatarUrl);
        }
    }

    private void loadAvatarAndAddChat(Chat chat, String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(avatarUrl, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                chat.setAvatarBitmap(decodedByte);
                saveAvatarToFile(chat.getRecipientId(), decodedByte);
            } catch (IllegalArgumentException e) {
                // Handle the error if the Base64 string is invalid
                chat.setAvatarBitmap(null);
            }
        } else {
            chat.setAvatarBitmap(null);
        }
        addNewChat(chat);
    }

    private void saveAvatarToFile(String uuid, Bitmap avatarBitmap) {
        File avatarFile = new File(getContext().getFilesDir(), uuid + ".png");
        try (FileOutputStream out = new FileOutputStream(avatarFile)) {
            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewChat(Chat chat) {
        // Check if chat with this recipient already exists
        int existingPosition = findChatPosition(chat.getRecipientId());

        if (existingPosition != -1) {
            // Update existing chat
            chatList.set(existingPosition, chat);
            chatAdapter.notifyItemChanged(existingPosition);
        } else {
            // Add new chat
            chatList.add(0, chat); // Add to the top of the list
            chatAdapter.notifyItemInserted(0);
            chatRecyclerView.scrollToPosition(0);
        }

        Log.d("Chat", "Chat added: " + chat.getDisplayName());
    }

    private int findChatPosition(String recipientId) {
        for (int i = 0; i < chatList.size(); i++) {
            if (chatList.get(i).getRecipientId().equals(recipientId)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateChatList(List<Chat> newChats) {
        chatList.clear();
        chatList.addAll(newChats);
        chatAdapter.notifyDataSetChanged(); // Notify the adapter
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECIPIENT_ID", chat.getRecipientId());
        intent.putExtra("DISPLAY_NAME", chat.getDisplayName());
        intent.putExtra("CURRENT_USER_ID", UserFunction.getUserId(requireContext()));
        startActivity(intent);
    }

    private void handleError(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChats(); // Refresh chats when returning from AddChatActivity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel any pending requests
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}