package vn.edu.usth.x.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;


public class LoginPage1 extends AppCompatActivity {

    private EditText indentify;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView loginStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page1);

        indentify = findViewById(R.id.name);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.Login);
        loginStatus = findViewById(R.id.loginStatus);


        loginButton.setOnClickListener(v -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("identifier", indentify.getText().toString());
                jsonBody.put("password", passwordEditText.getText().toString());

                new LoginTask().execute(jsonBody.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String jsonBody = params[0];
            Log.d("RegisterUserTask", "Received JSON: " + jsonBody); // Log the received JSON
            try {
                URL url = new URL("https://huyln.info/xclone/api/login/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d("LoginTask", "Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    String responseJson = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
                    // Parse the response JSON and extract the id
                    JSONObject jsonResponse = new JSONObject(responseJson);
                    String id = jsonResponse.getString("id");
                    Log.d("LoginTask", "User ID: " + id);

                    // Remake the avatar
                    UserFunction.onUuidChanged(getApplicationContext(), id);

                    Intent intent = new Intent(LoginPage1.this, HomeFragment.class);
                    startActivity(intent);
                    finish();

                    return true;
                } else {
                    loginStatus.setVisibility(View.VISIBLE);
                    Log.d("LoginTask", "Response message: " + conn.getResponseMessage());
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}