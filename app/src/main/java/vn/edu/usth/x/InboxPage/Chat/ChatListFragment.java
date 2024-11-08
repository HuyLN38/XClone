package vn.edu.usth.x.InboxPage.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.x.InboxPage.AddChatActivity.AddChatActivity;
import ChatActivity;
import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.GlobalWebSocketManager;
import vn.edu.usth.x.Utils.UserFunction;

public class ChatListFragment extends Fragment implements ChatAdapter.OnChatClickListener {
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        chatList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        initializeViews(rootView);
        setupRecyclerView();
        setupSwipeRefresh();
        setupWebSocketObservers();
        loadChats();

        return rootView;
    }

    private void initializeViews(View rootView) {
        chatRecyclerView = rootView.findViewById(R.id.chatRecyclerView);
        ImageButton addMessageButton = rootView.findViewById(R.id.add_msg_button);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        addMessageButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddChatActivity.class)));
    }

    private void setupRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(chatList, this);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadChats);
        swipeRefreshLayout.setColorSchemeResources(R.color.light_blue);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupWebSocketObservers() {
        // Observe new messages to update chat list
        GlobalWebSocketManager.getInstance().getNewMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            String currentUserId = UserFunction.getUserId(requireContext());

            // Find if we already have a chat with this user
            Chat existingChat = null;
            int existingPosition = -1;
            String otherUserId = message.getSenderId().equals(currentUserId) ?
                    message.getRecipientId() : message.getSenderId();

            for (int i = 0; i < chatList.size(); i++) {
                if (chatList.get(i).getRecipientId().equals(otherUserId)) {
                    existingChat = chatList.get(i);
                    existingPosition = i;
                    break;
                }
            }

            if (existingChat != null) {
                // Update existing chat
                existingChat.setLastMessage(message.getContent());
                if (existingPosition > 0) {
                    // Move chat to top
                    chatList.remove(existingPosition);
                    chatList.add(0, existingChat);
                    chatAdapter.notifyDataSetChanged();
                } else {
                    // Just update the message if it's already at top
                    chatAdapter.notifyItemChanged(0);
                }
            } else {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // Observe connection state
        GlobalWebSocketManager.getInstance().getConnectionStateLiveData().observe(
                getViewLifecycleOwner(),
                isConnected -> {
                    if (!isConnected) {
                        Toast.makeText(getContext(), "Connection lost. Reconnecting...", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadChats() {
        String userId = UserFunction.getUserId(requireContext());
        if (userId == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        String url = BASE_URL + "/messages/conversations/" + userId;
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        chatList.clear(); // Clear existing chats
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject chatData = response.getJSONObject(i);
                            String otherUserId = chatData.getString("recipient_id");
                            String displayName = chatData.getString("recipient_display_name");
                            String lastMessage = chatData.getString("content");
                            String avatarUrl = chatData.optString("recipient_avatar_url");

                            Chat chat = new Chat(displayName, lastMessage, otherUserId);
                            loadAvatarAndAddChat(chat, avatarUrl);
                        }
                        chatAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        handleError("Error processing chats data");
                    }
                },
                error -> handleError("Error loading chats")
        );

        requestQueue.add(request);
    }

    private void loadAvatarAndAddChat(Chat chat, String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // Your existing avatar loading logic
            AvatarManager.getInstance(requireContext())
                    .getAvatar(chat.getRecipientId())
                    .thenAccept(bitmap -> {
                        if (bitmap != null) {
                            chat.setAvatarBitmap(bitmap);
                            requireActivity().runOnUiThread(() ->
                                    chatAdapter.notifyItemChanged(chatList.indexOf(chat))
                            );
                        }
                    });
        }
        chatList.add(chat);
    }

    private void handleError(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECIPIENT_ID", chat.getRecipientId());
        intent.putExtra("DISPLAY_NAME", chat.getDisplayName());
        intent.putExtra("CURRENT_USER_ID", UserFunction.getUserId(requireContext()));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}