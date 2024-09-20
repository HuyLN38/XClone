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

public class HomeForYou extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_for_you, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.homeForYouRecycle);

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
                new Tweet(R.drawable.avatar_bcc, "BCCNEWS", "bccnews",getString(R.string.description_yagiQN), "1h", R.drawable.img_yagiqn),
                new Tweet(R.drawable.avatar_vtv, "VTV24", "vtv24",getString(R.string.description_flood2), "2h", R.drawable.img_flood2),
                new Tweet(R.drawable.avatar_bcc, "BCCNEWS", "bccnews",getString(R.string.description_traffic), "3h", R.drawable.img_traffic),
                new Tweet(R.drawable.avatar_vtv, "VTV24", "vtv24",getString(R.string.description_flood1), "5h", R.drawable.img_flood1),
                new Tweet(R.drawable.avatar_bcc, "BCCNEWS", "bccnews",getString(R.string.description_yagiQN), "1h", R.drawable.img_yagiqn),
                new Tweet(R.drawable.avatar_vtv, "VTV24", "vtv24",getString(R.string.description_flood2), "2h", R.drawable.img_flood2),
                new Tweet(R.drawable.avatar_bcc, "BCCNEWS", "bccnews",getString(R.string.description_traffic), "3h", R.drawable.img_traffic),
                new Tweet(R.drawable.avatar_vtv, "VTV24", "vtv24",getString(R.string.description_flood1), "5h", R.drawable.img_flood1),
                new Tweet(R.drawable.avatar_bcc, "BCCNEWS", "bccnews",getString(R.string.description_yagiQN), "1h", R.drawable.img_yagiqn),
                new Tweet(R.drawable.avatar_vtv, "VTV24", "vtv24",getString(R.string.description_flood2), "2h", R.drawable.img_flood2),
                new Tweet(R.drawable.avatar_bcc, "BCCNEWS", "bccnews",getString(R.string.description_traffic), "3h", R.drawable.img_traffic),
                new Tweet(R.drawable.avatar_vtv, "VTV24", "vtv24",getString(R.string.description_flood1), "5h", R.drawable.img_flood1)
        );
    }
}