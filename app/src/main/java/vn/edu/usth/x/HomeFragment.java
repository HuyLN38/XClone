package vn.edu.usth.x;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.x.community.CommunityFragment;
import vn.edu.usth.x.databinding.ActivityHomeBinding;

public class HomeFragment extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frameLayout);

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeMenuFragment()).commitNow();
        replaceTopBar(R.layout.fragment_home_top_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                bottomNavigationView.setItemIconTintList(null);

                if (id == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeMenuFragment()).commitNow();
                    replaceTopBar(R.layout.fragment_home_top_bar);
                    return true;
                } else if (id == R.id.search) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SearchFragment()).commitNow();
                    replaceTopBar(R.layout.fragment_search_top_bar);
                    return true;
                } else if (id == R.id.notification) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationFragment()).commitNow();
                    return true;
                } else if (id == R.id.mail) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new InboxFragment()).commitNow();
                    return true;
                } else if (id == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).commitNow();
                    return true;
                }
                return false;
            }
        });
    }

    private void replaceTopBar(int layoutId) {
        FrameLayout topBar = findViewById(R.id.home_toolbar);
        topBar.removeAllViews(); // Clear previous views
        getLayoutInflater().inflate(layoutId, topBar, true);
    }
}