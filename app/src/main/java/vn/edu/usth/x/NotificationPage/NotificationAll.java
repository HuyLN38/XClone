package vn.edu.usth.x.NotificationPage;

import static java.sql.Types.NULL;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationModel;
import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationRecycleAdapter;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapter;


public class NotificationAll extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_all, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewNofiAll);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Specify an adapter (you need to implement your own adapter)
        adapter = new NotificationRecycleAdapter(getTweetData());
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Example method to get data for the adapter
    private ArrayList<NotificationModel> getTweetData() {
        // Return a list of tweets
        return new ArrayList<>(
                Arrays.asList(
                        new NotificationModel(R.drawable.avatar_bcc, "BBC NEWS",  getString(R.string.description_yagiQN)),
                        new NotificationModel(R.drawable.avatar_vtv, "VTV24",  getString(R.string.description_flood2)),
                        new NotificationModel(R.drawable.avatar3, "Naruto",  getString(R.string.description_traffic)),
                        new NotificationModel(R.drawable.avatar_2, "Johnny Smith",  getString(R.string.description_flood1)),
                        new NotificationModel(R.drawable.johnnysins, "Johnny Sins",  getString(R.string.description_flood2))
                )
        );
    }
}