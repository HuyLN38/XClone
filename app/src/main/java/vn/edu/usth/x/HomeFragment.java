package vn.edu.usth.x;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import vn.edu.usth.x.CommunityPage.CommunityFragment;
import vn.edu.usth.x.HomePage.HomeMenuFragment;
import vn.edu.usth.x.InboxPage.InboxFragment;
import vn.edu.usth.x.NotificationPage.NotificationFragment;
import vn.edu.usth.x.SearchPage.SearchFragment;
import vn.edu.usth.x.Topbar.CommunityTopBarFragment;
import vn.edu.usth.x.Topbar.HomeTopBarFragment;
import vn.edu.usth.x.Topbar.InboxTopBar;
import vn.edu.usth.x.Topbar.NotificationTopBarFragment;
import vn.edu.usth.x.Topbar.SearchTopBarFragment;
import vn.edu.usth.x.databinding.ActivityHomeBinding;

public class HomeFragment extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;
    private FrameLayout frameLayout;
    private DrawerLayout drawerLayout;

    private Fragment homeFragment;
    private Fragment searchFragment;
    private Fragment notificationFragment;
    private Fragment inboxFragment;
    private Fragment communityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frameLayout);
        NavigationView navigationView = findViewById(R.id.sidebar_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Initialize fragments
        homeFragment = new HomeMenuFragment();
        searchFragment = new SearchFragment();
        notificationFragment = new NotificationFragment();
        inboxFragment = new InboxFragment();
        communityFragment = new CommunityFragment();

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);
        addFragment(homeFragment, new HomeTopBarFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                bottomNavigationView.setItemIconTintList(null);

                if (id == R.id.home) {
                    showFragment(homeFragment, new HomeTopBarFragment());
                    return true;
                } else if (id == R.id.search) {
                    showFragment(searchFragment, new SearchTopBarFragment());
                    return true;
                } else if (id == R.id.notification) {
                    showFragment(notificationFragment, new NotificationTopBarFragment());
                    return true;
                } else if (id == R.id.mail) {
                    showFragment(inboxFragment, new InboxTopBar());
                    return true;
                } else if (id == R.id.community) {
                    showFragment(communityFragment, new CommunityTopBarFragment());
                    return true;
                }
                return false;
            }
        });
    }

    private void addFragment(Fragment fragment, Fragment topBarFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, fragment);
        transaction.add(R.id.home_toolbar, topBarFragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment, Fragment topBarFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Hide all fragments
        for (Fragment frag : fragmentManager.getFragments()) {
            if (frag != fragment) {
                transaction.hide(frag);
            }
        }

        // Show the selected fragment
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.frameLayout, fragment);
        }

        // Replace the top bar fragment
        transaction.replace(R.id.home_toolbar, topBarFragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(binding.sidebarView)) {
            drawerLayout.closeDrawer(binding.sidebarView);
        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                drawerLayout.openDrawer(binding.sidebarView);
            }
        }
    }
}