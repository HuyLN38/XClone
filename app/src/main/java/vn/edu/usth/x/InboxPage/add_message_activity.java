package vn.edu.usth.x.InboxPage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import vn.edu.usth.x.Login.Data.User;
import vn.edu.usth.x.R;

public class add_message_activity extends AppCompatActivity {

    private SearchView searchView;
    private Button buttonAddMessage;
    private User selectedUser;
    private String avatarFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        searchView = findViewById(R.id.search_view);
        buttonAddMessage = findViewById(R.id.button_add_message);

        // Back to search button
        ImageView backToSearch = findViewById(R.id.back_to_search);
        backToSearch.setOnClickListener(v -> super.onBackPressed());

        // Set search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser(query);  // Fetch user when query is submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Add message button listener
        buttonAddMessage.setOnClickListener(v -> {
            if (selectedUser != null) {
                Intent intent = new Intent(add_message_activity.this, MessageActivity.class);
                intent.putExtra("USER_NAME", selectedUser.getDisplayName());
                intent.putExtra("AVATAR_FILE_PATH", avatarFilePath); // Pass the file path, not the image
                startActivity(intent);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(add_message_activity.this, "No user selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUser(String userId) {
        // Define the URL
        String url = "https://huyln.info/xclone/api/users/" + userId;

        // Initialize Volley RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the response
                            String id = response.getString("id");
                            String username = response.getString("username");
                            String displayName = response.getString("display_name");
                            String avatarBase64 = response.getString("avatar_url");

                            // Create and set selectedUser
                            selectedUser = new User();
                            selectedUser.setName(username);
                            selectedUser.setDisplayName(displayName);

                            // Decode the avatar from Base64 and save it to local storage
                            decodeAndSaveAvatar(avatarBase64);

                            Toast.makeText(add_message_activity.this, "User found: " + selectedUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(add_message_activity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
                Toast.makeText(add_message_activity.this, "User not found or error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    private void decodeAndSaveAvatar(String avatarBase64) {
        new Thread(() -> {
            try {
                // Decode the Base64-encoded avatar string
                byte[] decodedString = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Save Bitmap to file
                avatarFilePath = saveAvatarToFile(avatarBitmap);

                runOnUiThread(() -> {
                    Toast.makeText(add_message_activity.this, "Avatar saved", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(add_message_activity.this, "Failed to decode and save avatar", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String saveAvatarToFile(Bitmap avatarBitmap) throws IOException {
        // Create a file to save the avatar
        File avatarFile = new File(getFilesDir(), "user_avatar.png");

        try (FileOutputStream fos = new FileOutputStream(avatarFile)) {
            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }

        return avatarFile.getAbsolutePath();  // Return the file path
    }
}
