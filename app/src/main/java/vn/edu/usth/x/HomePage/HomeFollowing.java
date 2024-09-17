package vn.edu.usth.x.HomePage;

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

public class HomeFollowing extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_follwing, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.homeFollowingRecycle);

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
        return asList(
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "\"I'm Johnny Sins, and I'm not just a plumber.\"\n" +
                        "\n" +
                        "Life is full of surprises, and you never know what role you’ll need to play next. But me? I’m always ready. Whether I’m called to a hospital room, a burning building, or a space station, I’ll show up, ready to give my all. Because that’s what I do.", "3h", R.drawable.johnnysins),
                new Tweet(R.drawable.avatar,"Elon Musk", "elonmusk",  getString(R.string.ElonMusk),  "2h",R.drawable.avatar),
                new Tweet(R.drawable.tokuda, "Tokuda", "tokuda123", getString(R.string.tokuda), "3h", R.drawable.tokuda)
        );
    }
}