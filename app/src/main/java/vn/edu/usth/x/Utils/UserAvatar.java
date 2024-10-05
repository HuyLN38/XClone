package vn.edu.usth.x.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class UserAvatar {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId";
    private static final String AVATAR_FILE_NAME = "user_avatar.png";

    public static void getAvatar(Context context, AvatarCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        if (userId == null) {
            Log.e("UserAvatar", "User ID not found in SharedPreferences");
            callback.onFailure("User ID not found in SharedPreferences");
            return;
        }

        File avatarFile = new File(context.getFilesDir(), AVATAR_FILE_NAME);
        if (avatarFile.exists()) {
            Bitmap avatarBitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
            callback.onSuccess(avatarBitmap);
        } else {
            new FetchAvatarTask(context, callback).execute("https://huyln.info/xclone/api/users/" + userId);
        }
    }

    public static void onUuidChanged(Context context, String newUuid, AvatarCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID_KEY, newUuid);
        editor.apply();

        // Fetch the new avatar
        getAvatar(context, callback);
    }

    private static class FetchAvatarTask extends AsyncTask<String, Void, Bitmap> {
        private Context context;
        private AvatarCallback callback;

        FetchAvatarTask(Context context, AvatarCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                Log.d("UserAvatar", "Fetching user data from: " + url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    String responseJson = new BufferedReader(new InputStreamReader(is))
                            .lines().collect(Collectors.joining("\n"));
                    InputStream avatarIs = getInputStream(responseJson);
                    Bitmap avatarBitmap = BitmapFactory.decodeStream(avatarIs);
                    saveBitmapToFile(context, avatarBitmap);
                    return avatarBitmap;
                } else {
                    Log.e("UserAvatar", "Failed to fetch user data: " + conn.getResponseMessage());
                }
            } catch (Exception e) {
                Log.e("UserAvatar", "Failed to fetch user data", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure("Failed to fetch user data");
            }
        }

        private InputStream getInputStream(String responseJson) throws JSONException, IOException {
            JSONObject jsonResponse = new JSONObject(responseJson);
            String avatarBase64 = jsonResponse.getString("avatar_url");

            // Decode the base64 string
            byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            return new ByteArrayInputStream(decodedBytes);
        }

        private void saveBitmapToFile(Context context, Bitmap bitmap) {
            File avatarFile = new File(context.getFilesDir(), AVATAR_FILE_NAME);
            try (FileOutputStream fos = new FileOutputStream(avatarFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (IOException e) {
                Log.e("UserAvatar", "Failed to save avatar image", e);
            }
        }
    }

    public interface AvatarCallback {
        void onSuccess(Bitmap avatar);
        void onFailure(String errorMessage);
    }
}