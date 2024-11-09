package vn.edu.usth.x.InboxPage.AddChatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.edu.usth.x.InboxPage.Message.MessageActivity;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class AddChatActivity extends AppCompatActivity implements UserListAdapter.OnUserClickListener {
    private static final String TAG = "AddChatActivity";
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private static final int PAGE_SIZE = 10;
    private static final int SEARCH_DELAY = 500; // milliseconds

    private RecyclerView userRecyclerView;
    private UserListAdapter userAdapter;
    private List<UserItem> userList;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private TextView emptyStateText;
    private TextView initialStateText;
    private Set<String> existingChatUserIds;

    private boolean isLoading = false;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        initializeViews();
        setupRecyclerView();
        setupSearch();
        loadExistingChats();
    }

    private void initializeViews() {
        requestQueue = Volley.newRequestQueue(this);
        userList = new ArrayList<>();
        existingChatUserIds = new HashSet<>();

        progressBar = findViewById(R.id.progressBar);
        searchEditText = findViewById(R.id.searchEditText);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        initialStateText = findViewById(R.id.initialStateText);

        // Set initial state
        userRecyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);
        initialStateText.setVisibility(View.VISIBLE);
        initialStateText.setText("Search for users to start a conversation");
    }

    private void setupRecyclerView() {
        userAdapter = new UserListAdapter(userList, this);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // Clear results and show initial state
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                    userRecyclerView.setVisibility(View.GONE);
                    emptyStateText.setVisibility(View.GONE);
                    initialStateText.setVisibility(View.VISIBLE);
                    return;
                }

                searchRunnable = () -> searchUsers(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadExistingChats() {
        String currentUserId = UserFunction.getUserId(this);
        if (currentUserId == null) {
            finish();
            return;
        }

        String url = BASE_URL + "/messages/conversations/" + currentUserId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray conversations = response.optJSONArray("items");
                        if (conversations != null) {
                            for (int i = 0; i < conversations.length(); i++) {
                                JSONObject chat = conversations.getJSONObject(i);
                                existingChatUserIds.add(chat.getString("recipient_id"));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading existing chats", e);
                    }
                },
                error -> Log.e(TAG, "Error loading existing chats", error));

        requestQueue.add(request);
    }

    private void searchUsers(String query) {
        if (isLoading) return;
        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);
        initialStateText.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);

        String url = String.format("%s/getallusers?page=1&size=%d&search=%s",
                BASE_URL, PAGE_SIZE, query);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        userList.clear();
                        JSONArray items = response.getJSONArray("items");
                        String currentUserId = UserFunction.getUserId(this);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject user = items.getJSONObject(i);
                            String userId = user.getString("id");

                            if (!userId.equals(currentUserId) && !existingChatUserIds.contains(userId)) {
                                userList.add(new UserItem(
                                        user.getString("username"),
                                        userId,
                                        user.getString("display_name"),
                                        user.optBoolean("is_verified"),
                                        user.optInt("following"),
                                        user.optInt("followers")
                                ));
                            }
                        }

                        updateUI();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing search results", e);
                        handleError();
                    }
                },
                error -> {
                    Log.e(TAG, "Error searching users", error);
                    handleError();
                });

        requestQueue.add(request);
    }

    private void updateUI() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);

        if (userList.isEmpty()) {
            userRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No users found matching '" + searchEditText.getText() + "'");
        } else {
            userRecyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            userAdapter.notifyDataSetChanged();
        }
    }

    private void handleError() {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        userRecyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateText.setText("Error searching users");
    }

    @Override
    public void onUserClick(UserItem user) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("RECIPIENT_ID", user.getCurrentID());
        intent.putExtra("DISPLAY_NAME", user.getDisplayName());
        intent.putExtra("CURRENT_USER_ID", UserFunction.getUserId(this));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchHandler.removeCallbacksAndMessages(null);
    }
}