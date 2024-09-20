package vn.edu.usth.x.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.R;

public class LoginPage1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page_2);

        Button nextButton = findViewById(R.id.nextLogin2);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage1Activity.this, HomeFragment.class);
            startActivity(intent);
        });
    }
}