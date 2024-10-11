package vn.edu.usth.x.Blog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class CommentFragment extends Fragment {
    private static final String TAG = "Comment_Tweet";
    private static final String API_URL = "https://huyln.info/xclone/api/comments";
    private EditText commentEditText;
    private String tweet_id;

    private Button postButton;

    private ImageView avatarImageView;
    private TextView usernameTextView;

    private TextView tweetLinkTextView;
    private TextView responseUsernameView;


    private TextView tweetTextView;

    private TextView timeTextView;
    private ImageView tweetImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize view
        avatarImageView = view.findViewById(R.id.tweet_avatar);
        usernameTextView = view.findViewById(R.id.tweet_name);
        tweetLinkTextView = view.findViewById(R.id.tweet_username);
        responseUsernameView = view.findViewById(R.id.response_username);
        tweetTextView = view.findViewById(R.id.tweet_text);
        timeTextView = view.findViewById(R.id.tweet_time);
        tweetImageView = view.findViewById(R.id.tweet_image);
        commentEditText = view.findViewById(R.id.edit_text_comment);

        Button cancelButton = view.findViewById(R.id.comment_cancel);
        postButton = view.findViewById(R.id.comment_post);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        postButton.setOnClickListener(v -> {
            String content = commentEditText.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Please enter some content", Toast.LENGTH_SHORT).show();
                return;
            }
            //transmit comment to postTweet
            postTweet(content);
        });

        //set avatar of current user
        ImageView avatar = view.findViewById(R.id.user_avatar);

        Context context = getContext();
        if (context != null) {
            UserFunction.getAvatar(context, new UserFunction.AvatarCallback() {
                @Override
                public void onSuccess(Bitmap avatarBitmap) {
                    Glide.with(context)
                            .load(avatarBitmap)
                            .into(avatar);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("Error Avatar: ", errorMessage);
                }
            });
        }

        // Get in4 from Bundle
        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String tweetLink = args.getString("tweetLink");
            String tweetText = args.getString("tweetText");
            String time = args.getString("time");
            tweet_id = args.getString("id");

            usernameTextView.setText(username);
            tweetLinkTextView.setText(tweetLink);
            tweetTextView.setText(tweetText);
            timeTextView.setText(time);
            responseUsernameView.setText(tweetLink);

            byte[] avatarByteArray = args.getByteArray("avatar");
            if (avatarByteArray != null) {
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarByteArray, 0, avatarByteArray.length);
                avatarImageView.setImageBitmap(avatarBitmap);
            }

            byte[] imageByteArray = args.getByteArray("tweetImage");
            if (imageByteArray != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                tweetImageView.setImageBitmap(imageBitmap);
                tweetImageView.setVisibility(View.VISIBLE);
            } else {
                tweetImageView.setVisibility(View.GONE);
            }
        }
    }
    //Handle Post Comment through API
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
            new PostTweetTask().execute(content, userId, tweet_id);
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
            String tweetId = params[2];
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
                jsonBody.put("tweet_id", tweetId);

//                if (!base64Image.isEmpty()) {
//                    jsonBody.put("media_url", base64Image);
//                }
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
                Log.d(TAG, "Comment posted successfully");
                Toast.makeText(getContext(), "Comment posted successfully", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            } else {
                Log.e(TAG, "Failed to post comment");
                Toast.makeText(getContext(), "Failed to post comment", Toast.LENGTH_SHORT).show();
            }
        }
    }

}