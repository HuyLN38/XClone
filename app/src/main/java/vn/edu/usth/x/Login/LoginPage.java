// File: LoginPage.java
package vn.edu.usth.x.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.R;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Button nextButton = findViewById(R.id.create_account);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, LoginPage1Activity.class);
            startActivity(intent);
        });

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, LoginPage0.class);
            startActivity(intent);
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }