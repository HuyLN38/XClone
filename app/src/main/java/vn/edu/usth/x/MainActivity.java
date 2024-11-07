package vn.edu.usth.x;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.x.Login.LoginPage;
import vn.edu.usth.x.Utils.GlobalWebSocketManager;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId";
    private static final long SPLASH_DELAY = 1000; // 1 second
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isWebSocketInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setupSystemUI();
        scheduleNavigation();

    }

    private void setupSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void scheduleNavigation() {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        handler.postDelayed(() -> {
            Intent intent;
            if (userId != null) {
                intent = new Intent(MainActivity.this, HomeFragment.class);
            } else {
                intent = new Intent(MainActivity.this, LoginPage.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Finish MainActivity to prevent returning to it
        }, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (isFinishing()) {
            GlobalWebSocketManager.getInstance().disconnect();
            isWebSocketInitialized = false;
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}