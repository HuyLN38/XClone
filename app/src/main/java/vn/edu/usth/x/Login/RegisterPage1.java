package vn.edu.usth.x.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.x.Login.Data.User;
import vn.edu.usth.x.Login.Data.UserManager;
import vn.edu.usth.x.R;

public class RegisterPage1 extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.dob);
        Button nextButton = findViewById(R.id.nextLogin2);

        nextButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        User user = UserManager.getUser();
        user.setName(nameEditText.getText().toString());
        user.setEmail(emailEditText.getText().toString());
        user.setDob(passwordEditText.getText().toString());
        Log.d("RegisterPage1", "Name: " + user.getName());
        Log.d("RegisterPage1", "Email: " + user.getEmail());
        Log.d("RegisterPage1", "DOB: " + user.getDob());
        Log.d("RegisterPage1", "Password: " + user.getPasswordHash());
        Log.d("RegisterPage1", "Profile Image: " + user.getProfileImageBase64());
        Log.d("RegisterPage1", "Display Name: " + user.getDisplayName());

        Intent intent = new Intent(RegisterPage1.this, RegisterPage2.class);
        startActivity(intent);
    }
}