package vn.edu.usth.x.NotificationPage;

import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationModel;
import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationRecycleAdapter;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.GlobalWebSocketManager;
import vn.edu.usth.x.Utils.UserFunction;

public class NotificationAll extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private NotificationRecycleAdapter adapter;
    private NotificationRepository repository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar loadingMore;
    private final ArrayList<NotificationModel> notificationList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_all, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupRepository();
        setupWebSocket();
        loadNotifications(true);

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewNofiAll);
        emptyText = view.findViewById(R.id.empty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        loadingMore = view.findViewById(R.id.loadingMore);

        swipeRefreshLayout.setOnRefreshListener(() -> loadNotifications(true));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NotificationRecycleAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && e.getAction() == MotionEvent.ACTION_UP) {
                    int position = rv.getChildAdapterPosition(child);
                    if (position != RecyclerView.NO_POSITION) {
                        handleNotificationClick(notificationList.get(position));
                        return true;
                    }
                }
                return false;
            }
        });

        // Add pagination scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadNotifications(false);
                    }
                }
            }
        });
    }



    private void handleNotificationClick(NotificationModel notification) {
        notification.setIs_read(true);
        new GetRequestTask().execute("https://huyln.info/xclone/api/notification/" + notification.getId());

        adapter.notifyItemChanged(notificationList.indexOf(notification));

        switch (notification.getType().toUpperCase()) {
            case "like":
            case "RETWEET":
            case "REPLY":
                if (notification.getTweet_id() != null) {

                }
                break;
            case "FOLLOW":
                if (notification.getNotifier_id() != null) {
                    // Navigate to profile
                    // TODO: Implement navigation to user profile
                }
                break;
        }
    }

    private class GetRequestTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }
    }

    private void setupRepository() {
        repository = new NotificationRepository();

        repository.getLoadingState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case LOADING:
                    loadingMore.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    loadingMore.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case ERROR:
                    loadingMore.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Error loading notifications",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        repository.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            notificationList.clear();
            notificationList.addAll(notifications);
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private void setupWebSocket() {
        GlobalWebSocketManager.getInstance().getNewNotificationLiveData()
                .observe(getViewLifecycleOwner(), notification -> {
                    if (notification != null) {
                        notificationList.add(0, notification);
                        adapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                        updateEmptyState();
                    }
                });
    }

    private void loadNotifications(boolean refresh) {
        String userId = UserFunction.getUserId(requireContext());
        if (userId != null) {
            repository.loadNotifications(userId, refresh);
        }
    }

    private void updateEmptyState() {
        emptyText.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
