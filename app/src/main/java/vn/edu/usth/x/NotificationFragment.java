package vn.edu.usth.x;

import static java.sql.Types.NULL;

import android.os.Bundle;

import vn.edu.usth.x.Tweet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TweetAdapter(getTweetData());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Tweet> getTweetData() {
        return Arrays.asList(
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL),
                new Tweet(R.drawable.johnnysins, "Johnny Sins", "johnnysins", "I'm a plumber", "3h", NULL)
        );
    }
}
