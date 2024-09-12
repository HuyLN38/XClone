package vn.edu.usth.x.CommunityPage;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import vn.edu.usth.x.R;

public class ExploreFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore,container,false);

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.Community_recyclerView);

        // Set a layout manager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and set it to the RecyclerView
        CommunityTweetAdapter adapter = new CommunityTweetAdapter(getTweetData());
        recyclerView.setAdapter(adapter);

        return view;
    }


    private ArrayList<CommunityTweet> getTweetData() {
        // Return an ArrayList of tweets
        return new ArrayList<>(
                Arrays.asList(
                        new CommunityTweet("Football", R.drawable.avatar, "Elon Musk", "elonmusk", "Doge", "2h", R.drawable.avatar),
                        new CommunityTweet("Book Club", R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", R.drawable.johnnysins)
                )
        );
    }
}

