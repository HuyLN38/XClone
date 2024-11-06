package vn.edu.usth.x.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.Login.Data.User;
import vn.edu.usth.x.Utils.UserManager;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class RegisterPage4 extends AppCompatActivity {

    private EditText usernameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_username);

        usernameEditText = findViewById(R.id.usernameEditText);
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        User user = UserManager.getUser();
        String username = usernameEditText.getText().toString();

        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return;
        }

        if (user.getEmail() == null || user.getDob() == null || user.getPasswordHash() == null || user.getName() == null) {
            Log.e("RegisterPage4", "User information is incomplete");
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("email", user.getEmail());
            jsonBody.put("birth_date", user.getDob());
            jsonBody.put("password_hash", user.getPasswordHash());
            jsonBody.put("avatar_url", user.getProfileImageBase64());
            jsonBody.put("display_name", user.getName());

            new RegisterUserTask().execute(jsonBody.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class RegisterUserTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {
            String jsonBody = params[0];
            Log.d("RegisterUserTask", "Received JSON: " + jsonBody); // Log the received JSON
            try {
                URL url = new URL("https://huyln.info/xclone/api/users/");
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
                Log.d("RegisterUserTask", "Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    InputStream is = conn.getInputStream();
                    String responseJson = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
                    Log.d("RegisterUserTask", "Response JSON: " + responseJson);
                    // Parse the response JSON and extract the id
                    JSONObject jsonResponse = new JSONObject(responseJson);
                    String id = jsonResponse.getString("id");
                    Log.d("RegisterUserTask", "User ID: " + id);

                    // Remake the avatar
                    UserFunction.onUuidChanged(getApplicationContext(), id);

                    Intent intent = new Intent(RegisterPage4.this, HomeFragment.class);
                    startActivity(intent);
                    finish();

                    return true;
                } else {
                    Log.d("RegisterUserTask", "Response message: " + conn.getResponseMessage());
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}