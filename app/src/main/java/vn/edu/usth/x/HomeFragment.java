package vn.edu.usth.x;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import vn.edu.usth.x.CommunityPage.CommunityFragment;
import vn.edu.usth.x.HomePage.HomeMenuFragment;
import vn.edu.usth.x.InboxPage.InboxFragment;
import vn.edu.usth.x.NotificationPage.NotificationFragment;
import vn.edu.usth.x.NotificationPage.NotificationSettings;
import vn.edu.usth.x.SearchPage.ExploreSettings;
import vn.edu.usth.x.SearchPage.SearchFragment;
import vn.edu.usth.x.Topbar.InboxTopBar;
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
        replaceTopBar(new HomeTopBarFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                bottomNavigationView.setItemIconTintList(null);

                if (id == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeMenuFragment()).commitNow();
                    replaceTopBar(new HomeTopBarFragment());
                    return true;
                } else if (id == R.id.search) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SearchFragment()).commitNow();
                    replaceTopBar(new SearchTopBarFragment());
                    return true;
                } else if (id == R.id.notification) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationFragment()).commitNow();
                    replaceTopBar(new NotificationTopBarFragment());
                    return true;
                } else if (id == R.id.mail) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new InboxFragment()).commitNow();
                    replaceTopBar(new InboxTopBar());
                    return true;
                } else if (id == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).commitNow();
                    replaceTopBar(new CommunityTopBarFragment());
                    return true;
                }
                return false;
            }
        });
    }
    private void replaceTopBar(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_toolbar, fragment)
                .commit();
        try {
            ImageView avatar = findViewById(R.id.avatar);
            avatar.setOnClickListener(v -> drawerLayout.openDrawer(binding.sidebarView));

            ImageView settingsSearch = findViewById(R.id.settings_search);
            settingsSearch.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, ExploreSettings.class);
                startActivity(intent1);
            });
        } catch (Exception ignored) {

        }
        try {
            ImageView settingsNotification = findViewById(R.id.settings_notification);
            settingsNotification.setOnClickListener(v -> {
                Intent intent2 = new Intent(this, NotificationSettings.class);
                startActivity(intent2);
            });
        }catch (Exception ignored) {

        }
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