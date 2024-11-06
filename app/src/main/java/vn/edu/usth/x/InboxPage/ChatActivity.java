package vn.edu.usth.x.InboxPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import vn.edu.usth.x.Utils.AvatarManager;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.GlobalWebSocketManager;

public class ChatActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ProgressBar loadingIndicator;
    private TextView emptyStateText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
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
        setupRecyclerView();
        loadInitialMessages();

        // Observe new messages
        GlobalWebSocketManager.getInstance().getNewMessageLiveData().observe(this, message -> {
            if (message.getSenderId().equals(otherUserId) ||
                    message.getRecipientId().equals(otherUserId)) {
                messageList.add(0, message);
                messageAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                updateEmptyState();
            }
        });

        // Observe message status updates
        GlobalWebSocketManager.getInstance().getMessageStatusLiveData().observe(this, status -> {
            for (int i = 0; i < messageList.size(); i++) {
                Message message = messageList.get(i);
                if (message.getId().equals(status.messageId)) {
                    messageAdapter.notifyItemChanged(i);
                    break;
                }
            }
        });
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


    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager != null ? layoutManager.getItemCount() : 0;
                int lastVisible = layoutManager != null ? layoutManager.findLastVisibleItemPosition() : 0;

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

        @SuppressLint("DefaultLocale") String url = String.format("%s/messages/conversation?user1_id=%s&user2_id=%s&page=%d&size=%d",
                BASE_URL, currentUserId, otherUserId, currentPage, PAGE_SIZE);

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
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
            GlobalWebSocketManager.getInstance().sendMessage(message);
            updateEmptyState();
        }
    }
}