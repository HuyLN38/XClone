package vn.edu.usth.x.NotificationPage;

import static java.sql.Types.NULL;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapter;

// HomeMenuFragment.java
public class NotificationMenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_menu, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Specify an adapter (you need to implement your own adapter)
        adapter = new TweetAdapter(getTweetData());
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Example method to get data for the adapter
    private List<Tweet> getTweetData() {
        // Return a list of tweets
        return Arrays.asList(
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL)
        );
    }
}