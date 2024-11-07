package vn.edu.usth.x.CommunityPage;

import static java.util.Arrays.asList;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapter;

public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore,container,false);

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.Community_recyclerView);

        // Set a layout manager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and set it to the RecyclerView
        adapter = new TweetAdapter(getTweetData());
        recyclerView.setAdapter(adapter);

        return view;
    }


    private List<Tweet> getTweetData() {
        // Return an ArrayList of tweets
        return asList(

        );
    }
}

