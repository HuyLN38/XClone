package vn.edu.usth.x.NotificationPage;

import static java.sql.Types.NULL;

import android.os.Bundle;

import vn.edu.usth.x.HomePage.HomeAdapter;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Tweet.Tweet;
import vn.edu.usth.x.Tweet.TweetAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NotificationFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    NotificationApdapter notificationApdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notification, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);
        notificationApdapter = new NotificationApdapter(requireActivity());
        viewPager2.setAdapter(notificationApdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
        return view;
    }
}
