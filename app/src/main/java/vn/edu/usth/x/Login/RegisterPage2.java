package vn.edu.usth.x.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import at.favre.lib.crypto.bcrypt.BCrypt;
import vn.edu.usth.x.Login.Data.User;
import vn.edu.usth.x.Login.Data.UserManager;
import vn.edu.usth.x.R;

public class RegisterPage2 extends AppCompatActivity {
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);

        passwordEditText = findViewById(R.id.password);
        Button nextButton = findViewById(R.id.nextLogin3);

        nextButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        User user = UserManager.getUser();
        user.setPasswordHash(BCrypt.withDefaults().hashToString(12, passwordEditText.getText().toString().toCharArray()));
        Log.d("RegisterPage1", "Name: " + user.getName());
        Log.d("RegisterPage1", "Email: " + user.getEmail());
        Log.d("RegisterPage1", "DOB: " + user.getDob());
        Log.d("RegisterPage1", "Password: " + user.getPasswordHash());
        Log.d("RegisterPage1", "Profile Image: " + user.getProfileImageBase64());
        Log.d("RegisterPage1", "Display Name: " + user.getDisplayName());

        Intent intent = new Intent(RegisterPage2.this, RegisterPage3.class);
        startActivity(intent);
        finish();
    }

}