package vn.edu.usth.x.InboxPage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import android.content.SharedPreferences;

import vn.edu.usth.x.R;

public class MessageActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView textUserName;
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ImageButton buttonSendMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private SharedPreferences sharedPreferences;
    private static final String USER_ID_KEY = "userId";
    private String senderId;
    private String recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Initialize SharedPreferences inside onCreate
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        senderId = sharedPreferences.getString(USER_ID_KEY, null);

        avatarImageView = findViewById(R.id.avatarImageView);
        textUserName = findViewById(R.id.textUserName);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        // Set up RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Get the user data from intent
        String userName = getIntent().getStringExtra("USER_NAME");
        recipientId = getIntent().getStringExtra("RECIPIENT_ID"); // Define recipientId globally
        String avatarFilePath = getIntent().getStringExtra("AVATAR_FILE_PATH");

        if (userName != null) {
            textUserName.setText(userName);
        }

        // Decode and set avatar
        if (avatarFilePath != null) {
            Bitmap avatarBitmap = BitmapFactory.decodeFile(avatarFilePath);
            avatarImageView.setImageBitmap(avatarBitmap);
        }

        // Load messages (fetch from API or local database)
        fetchMessages(senderId, recipientId);

        // Handle message sending
        buttonSendMessage.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText, recipientId); // Pass recipientId
                editTextMessage.setText("");
            }
        });
    }

    private void sendMessage(String messageText, String recipientId) { // Add recipientId parameter
        // Prepare the JSON payload
        JSONObject postData = new JSONObject();
        try {
            postData.put("sender_id", senderId);
            postData.put("recipient_id", recipientId);
            postData.put("content", messageText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a new Thread to send the message in the background
        new Thread(() -> {
            try {
                URL url = new URL("https://huyln.info/xclone/api/messages");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Write the data to the output stream
                OutputStream os = conn.getOutputStream();
                os.write(postData.toString().getBytes("UTF-8"));
                os.close();

                // Get the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String result = convertStreamToString(in);
                    // Process the returned message
                    runOnUiThread(() -> processPostedMessage(result));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Helper function to convert InputStream to String
    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    // Process the posted message
    private void processPostedMessage(String result) {
        try {
            // Parse the returned JSON
            JSONObject jsonResponse = new JSONObject(result);
            String messageId = jsonResponse.getString("id");
            String messageContent = jsonResponse.getString("content");
            String createdAt = jsonResponse.getString("created_at");

            // Add the message to the list and update the UI
            Message newMessage = new Message(messageContent, true);
            newMessage.setCreatedAt(createdAt);
            messageList.add(newMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchMessages(String senderId, String recipientId) { // Fetch messages with both senderId and recipientId
        new Thread(() -> {
            try {
                // Prepare the URL with user1_id and user2_id
                URL url = new URL("https://huyln.info/xclone/api/messages?user1_id=" + senderId + "&user2_id=" + recipientId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                // Get the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String result = convertStreamToString(in);
                    // Process the received messages
                    runOnUiThread(() -> processReceivedMessages(result));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Process the received messages
    private void processReceivedMessages(String result) {
        try {
            // Parse the JSON array
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonMessage = jsonArray.getJSONObject(i);
                String content = jsonMessage.getString("content");
                String senderId = jsonMessage.getString("sender_id");
                String createdAt = jsonMessage.getString("created_at");

                boolean isSentByUser = this.senderId.equals(senderId); // Check if the message is sent by the current user
                Message message = new Message(content, isSentByUser);
                message.setCreatedAt(createdAt);

                // Add the message to the list
                messageList.add(message);
            }

            // Notify the adapter to update the RecyclerView
            messageAdapter.notifyDataSetChanged();
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
