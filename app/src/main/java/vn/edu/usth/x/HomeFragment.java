package vn.edu.usth.x;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import vn.edu.usth.x.CommunityPage.CommunityFragment;
import vn.edu.usth.x.HomePage.HomeMenuFragment;
import vn.edu.usth.x.InboxPage.InboxFragment;
import vn.edu.usth.x.NotificationPage.NotificationFragment;
import vn.edu.usth.x.SearchPage.SearchFragment;
import vn.edu.usth.x.databinding.ActivityHomeBinding;

public class HomeFragment extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;
    private FrameLayout frameLayout;
    private DrawerLayout drawerLayout;

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

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeMenuFragment()).commitNow();
        replaceTopBar(R.layout.topbar_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                bottomNavigationView.setItemIconTintList(null);

                if (id == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeMenuFragment()).commitNow();
                    replaceTopBar(R.layout.topbar_home);
                    return true;
                } else if (id == R.id.search) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SearchFragment()).commitNow();
                    replaceTopBar(R.layout.topbar_search);
                    return true;
                } else if (id == R.id.notification) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationFragment()).commitNow();
                    replaceTopBar(R.layout.settings_inbox);
                    return true;
                } else if (id == R.id.mail) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new InboxFragment()).commitNow();
                    replaceTopBar(R.layout.topbar_inbox);
                    return true;
                } else if (id == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).commitNow();
                    replaceTopBar(R.layout.topbar_community);
                    return true;
                }
                return false;
            }
        });
    }

    private void replaceTopBar(int layoutId) {
        FrameLayout topBar = findViewById(R.id.home_toolbar);
        topBar.removeAllViews();
        getLayoutInflater().inflate(layoutId, topBar, true);

        ImageView avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(binding.sidebarView);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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