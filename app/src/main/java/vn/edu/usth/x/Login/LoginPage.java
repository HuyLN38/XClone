// File: LoginPage.java
package vn.edu.usth.x.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}