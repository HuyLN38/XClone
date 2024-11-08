package vn.edu.usth.x.HomePage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import vn.edu.usth.x.Blog.PostNewsFeed;
import vn.edu.usth.x.R;


// HomeMenuFragment.java
public class HomeMenuFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    HomeAdapter homeAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_menu, container, false);

        tabLayout = view.findViewById(R.id.homeTabLayout);
        viewPager2 = view.findViewById(R.id.homeViewPager);
        homeAdapter = new HomeAdapter(requireActivity());
        viewPager2.setAdapter(homeAdapter);

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Now set up the button listener
        ImageButton createNewsfeed = view.findViewById(R.id.create_newsfeed);

        createNewsfeed.setOnClickListener(v -> {
            // Navigate to PostNewsFeed
            FragmentActivity activity = (FragmentActivity) v.getContext();
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.drawer_layout, new PostNewsFeed()).commit();
        });
    }
}