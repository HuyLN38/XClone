package vn.edu.usth.x.InboxPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.usth.x.Login.Data.AvatarManager;
import vn.edu.usth.x.R;

public class ChatActivity extends AppCompatActivity implements WebSocketManager.ChatCallback {
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ProgressBar loadingIndicator;
    private TextView emptyStateText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private WebSocketManager webSocketManager;
    private RequestQueue requestQueue;
    private String currentUserId;
    private String otherUserId;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean hasMoreMessages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initializeViews();
        setupWebSocket();
        setupRecyclerView();
        loadInitialMessages();
    }

    private void initializeViews() {
        TextView name = findViewById(R.id.userName);
        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        ImageButton sendButton = findViewById(R.id.send);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        emptyStateText = findViewById(R.id.emptyStateText);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        CircleImageView avatar = findViewById(R.id.profileImage);
        ImageButton backButton = findViewById(R.id.backButton);

        // Ensure name TextView is not null before setting text

        backButton.setOnClickListener(v -> finish());
        String displayname = getIntent().getStringExtra("DISPLAY_NAME");
        currentUserId = getIntent().getStringExtra("CURRENT_USER_ID");
        otherUserId = getIntent().getStringExtra("RECIPIENT_ID");
        name.setText(displayname);// Ensure this is retrieved
            AvatarManager.getInstance(this)
                    .getAvatar(otherUserId)
                    .thenAccept(bitmap -> {
                        if (bitmap != null) {
                            Glide.with(this)
                                    .load(bitmap)
                                    .into(avatar);
                        } else {
                            Glide.with(this)
                                    .load(R.drawable.avatar)
                                    .into(avatar);
                        }
                    });

        requestQueue = Volley.newRequestQueue(this);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, currentUserId);

        sendButton.setOnClickListener(v -> sendMessage());

        swipeRefreshLayout.setOnRefreshListener(this::loadMoreMessages);
        swipeRefreshLayout.setColorSchemeResources(R.color.light_blue);
    }

    private void setupWebSocket() {
        webSocketManager = new WebSocketManager(this);
        webSocketManager.connect(this);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (hasMoreMessages && !isLoading && lastVisible >= totalItemCount - 5) {
                    loadMoreMessages();
                }
            }
        });
    }

    private void loadInitialMessages() {
        currentPage = 1;
        loadMessages(true);
    }

    private void loadMoreMessages() {
        if (!isLoading && hasMoreMessages) {
            currentPage++;
            loadMessages(false);
        }
    }

    private void loadMessages(boolean isInitialLoad) {
        if (isLoading) return;
        isLoading = true;

        if (isInitialLoad) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        String url = String.format("%s/messages/conversation?user1_id=%s&user2_id=%s&page=%d&size=%d",
                BASE_URL, currentUserId, otherUserId, currentPage, PAGE_SIZE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");
                        JSONObject pagination = response.getJSONObject("pagination");

                        List<Message> newMessages = new ArrayList<>();
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            Message message = new Message(
                                    item.getString("id"),
                                    item.getString("sender_id"),
                                    item.getString("recipient_id"),
                                    item.getString("content")
                            );
                            message.setStatus(item.optString("status", "sent"));
                            newMessages.add(message);
                        }

                        if (isInitialLoad) {
                            messageList.clear();
                        }
                        messageList.addAll(newMessages);
                        messageAdapter.notifyDataSetChanged();

                        hasMoreMessages = pagination.getBoolean("hasNext");
                        updateEmptyState();

                    } catch (Exception e) {
                        Log.e("ChatActivity", "Error parsing messages", e);
                        Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                    }

                    isLoading = false;
                    loadingIndicator.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    Log.e("ChatActivity", "Error loading messages", error);
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    loadingIndicator.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    updateEmptyState();
                });

        requestQueue.add(request);
    }

    private void updateEmptyState() {
        if (messageList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No messages yet");
        } else {
            emptyStateText.setVisibility(View.GONE);
        }
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (!content.isEmpty()) {
            Message message = new Message(currentUserId, otherUserId, content);
            messageList.add(0, message);
            messageAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            messageInput.setText("");
            webSocketManager.sendMessage(message, this);
            updateEmptyState();
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        runOnUiThread(() -> {
            messageList.add(0, message);
            messageAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            updateEmptyState();
        });
    }

    @Override
    public void onStatusUpdate(String messageId, String status) {
        runOnUiThread(() -> {
            for (int i = 0; i < messageList.size(); i++) {
                Message message = messageList.get(i);
                if (message.getId().equals(messageId)) {
                        message.setStatus(status);
                    messageAdapter.notifyItemChanged(i);
                    break;
                }
            }
        });
    }

    @Override
    public void onConnectionStateChange(boolean connected) {
        runOnUiThread(() -> {

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnect();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}