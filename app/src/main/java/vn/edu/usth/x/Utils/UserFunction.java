package vn.edu.usth.x.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;


import vn.edu.usth.x.Login.Data.AvatarManager;


public class UserFunction {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId";


    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID_KEY, null);
    }

    public static void onUuidChanged(Context context, String newUuid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String oldUuid = sharedPreferences.getString(USER_ID_KEY, null);

        if (!newUuid.equals(oldUuid)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_ID_KEY, newUuid);
            editor.apply();

            // Delete the old avatar file
            AvatarManager.getInstance(context)
                    .getAvatar(newUuid)
                    .thenAccept(bitmap -> {
                    });

        }
    }

    public interface AvatarCallback {
        void onSuccess(Bitmap avatar);
        void onFailure(String errorMessage);
    }
}