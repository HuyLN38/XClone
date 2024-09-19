package vn.edu.usth.x.SearchPage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.x.R;

public class PageForYou extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_for_you, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.searchPageRecyclerView);

        List<searchItem> itemList = new ArrayList<searchItem>();

        itemList.add(new searchItem("Trending in Vietnam","Yagi","20k"));
        itemList.add(new searchItem("Trending in Sport","Roma","18,7k"));
        itemList.add(new searchItem("Trending in Technology","CyberAttack","10k"));
        itemList.add(new searchItem("Trending in Politics","Alaska","46,4k"));
        itemList.add(new searchItem("Trending in Politics","Hezbollah","1,13M"));
        itemList.add(new searchItem("Trending in Politics","Terrorist","464k"));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new searchItemsAdapter(getActivity().getApplicationContext(), itemList));

        return view;
    }



}