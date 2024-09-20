package vn.edu.usth.x;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

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
    private DrawerLayout drawerLayout;

    private Fragment homeFragment;
    private Fragment searchFragment;
    private Fragment notificationFragment;
    private Fragment inboxFragment;
    private Fragment communityFragment;

    private static final String CURRENT_FRAGMENT_TAG = "current_fragment_tag";
    private String currentFragmentTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationView navigationView = findViewById(R.id.sidebar_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Initialize fragments
        homeFragment = new HomeMenuFragment();
        searchFragment = new SearchFragment();
        notificationFragment = new NotificationFragment();
        inboxFragment = new InboxFragment();
        communityFragment = new CommunityFragment();

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
            if (currentFragment != null) {
                showFragment(currentFragment, getTopBarFragment(currentFragmentTag), currentFragmentTag);
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home);
            addFragment(homeFragment, new HomeTopBarFragment(), "home");
            currentFragmentTag = "home";
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                bottomNavigationView.setItemIconTintList(null);

                if (id == R.id.home) {
                    showFragment(homeFragment, new HomeTopBarFragment(), "home");
                    return true;
                } else if (id == R.id.search) {
                    showFragment(searchFragment, new SearchTopBarFragment(), "search");
                    return true;
                } else if (id == R.id.notification) {
                    showFragment(notificationFragment, new NotificationTopBarFragment(), "notification");
                    return true;
                } else if (id == R.id.mail) {
                    showFragment(inboxFragment, new InboxTopBar(), "mail");
                    return true;
                } else if (id == R.id.community) {
                    showFragment(communityFragment, new CommunityTopBarFragment(), "community");
                    return true;
                }
                return false;
            }
        });
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void addFragment(Fragment fragment, Fragment topBarFragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, fragment, tag);
        transaction.add(R.id.home_toolbar, topBarFragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment, Fragment topBarFragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment frag : fragmentManager.getFragments()) {
            if (frag != fragment) {
                transaction.hide(frag);
            }
        }

        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.frameLayout, fragment, tag);
        }

        transaction.replace(R.id.home_toolbar, topBarFragment);
        transaction.commit();

        currentFragmentTag = tag;
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT_TAG, currentFragmentTag);
    }

    private Fragment getTopBarFragment(String tag) {
        switch (tag) {
            case "home":
                return new HomeTopBarFragment();
            case "search":
                return new SearchTopBarFragment();
            case "notification":
                return new NotificationTopBarFragment();
            case "mail":
                return new InboxTopBar();
            case "community":
                return new CommunityTopBarFragment();
            default:
                return null;
        }
    }
}