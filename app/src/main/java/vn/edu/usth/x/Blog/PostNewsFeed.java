package vn.edu.usth.x.Blog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import vn.edu.usth.x.Login.Data.AvatarManager;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class PostNewsFeed extends Fragment {
    private static final String TAG = "PostNewsFeed";
    private static final String API_URL = "https://huyln.info/xclone/api/tweets";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView selectedImageView;
    private EditText contentEditText;
    private String base64Image = "";
    private Button postButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        Button cancelButton = view.findViewById(R.id.post_cancel);
        postButton = view.findViewById(R.id.post_button);
        ImageView addPictureButton = view.findViewById(R.id.add_picture_button);
        selectedImageView = view.findViewById(R.id.selected_image_view);
        contentEditText = view.findViewById(R.id.content_edit_text);

        cancelButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        postButton.setOnClickListener(v -> {
            String content = contentEditText.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Please enter some content", Toast.LENGTH_SHORT).show();
                return;
            }
            postTweet(content);
        });

        addPictureButton.setOnClickListener(v -> openGallery());

        //set avatar of current user
        ImageView avatar = view.findViewById(R.id.avatar_post);

        Context context = getContext();
        if (context != null) {
            AvatarManager.getInstance(context)
                    .getAvatar(UserFunction.getUserId(context))
                    .thenAccept(bitmap -> {
                        if (bitmap != null) {Glide.with(context)
                                .load(bitmap)
                                .into(avatar);
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.avatar3)
                                    .into(avatar);
                        }
                    });
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        imageUri
                );
                selectedImageView.setImageBitmap(bitmap);
                selectedImageView.setVisibility(View.VISIBLE);
                base64Image = bitmapToBase64(bitmap);
            } catch (IOException e) {
                Log.e("PostNewsFeed", "Error processing image: " + e.getMessage());
                Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void postTweet(String content) {
        // Disable post button to prevent double posting
        postButton.setEnabled(false);

        // Extract userId from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        // Check if userId was retrieved successfully
        if (userId != null) {
            // Log the retrieved userId for debugging purposes
            Log.d("PostTweet", "User ID: " + userId);

            // Send content and userId to API
            new PostTweetTask().execute(content, userId);
        } else {
            // If userId is null, log an error or show a toast message
            Log.e("PostTweet", "User ID not found in SharedPreferences");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            postButton.setEnabled(true);
        }
    }

    private class PostTweetTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String content = params[0];
            String userId = params[1];
            HttpURLConnection conn = null;

            try {
                // Log the request details
                Log.d(TAG, "Preparing to send tweet - Content: " + content + ", UserId: " + userId);

                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Create and log JSON body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("content", content);
                jsonBody.put("user_id", userId);
                if (!base64Image.isEmpty()) {
                    jsonBody.put("media_url", base64Image);
                }
                String jsonString = jsonBody.toString();
                Log.d(TAG, "Request JSON body: " + jsonString);

                // Write to output stream
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    // Read and log the successful response
                    InputStream is = conn.getInputStream();
                    String responseJson = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
                    Log.d(TAG, "Success response: " + responseJson);
                    return true;
                } else {
                    // Read and log the error response
                    InputStream es = conn.getErrorStream();
                    String errorResponse = new BufferedReader(new InputStreamReader(es, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
                    Log.e(TAG, "Error response: " + errorResponse);
                    Log.e(TAG, "Response message: " + conn.getResponseMessage());
                    return false;
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception while posting tweet", e);
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            postButton.setEnabled(true);

            if (success) {
                Log.d(TAG, "Tweet posted successfully");
                Toast.makeText(getContext(), "Tweet posted successfully", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            } else {
                Log.e(TAG, "Failed to post tweet");
                Toast.makeText(getContext(), "Failed to post tweet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
