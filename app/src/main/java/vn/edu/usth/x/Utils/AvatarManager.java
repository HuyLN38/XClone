package vn.edu.usth.x.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import vn.edu.usth.x.R;

public class AvatarManager {
    private static final String BASE_URL = "https://huyln.info/xclone/api";
    private static AvatarManager instance;
    private final LruCache<String, Bitmap> memoryCache;
    private final Context context;
    private final RequestQueue requestQueue;

    private AvatarManager(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(context);
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static synchronized AvatarManager getInstance(Context context) {
        if (instance == null) {
            instance = new AvatarManager(context);
        }
        return instance;
    }

    public CompletableFuture<Bitmap> getAvatar(String uuid) {
        CompletableFuture<Bitmap> future = new CompletableFuture<>();

        // Try memory cache first
        Bitmap bitmap = memoryCache.get(uuid);
        if (bitmap != null) {
            future.complete(bitmap);
            return future;
        }

        // Try file cache
        File avatarFile = new File(context.getFilesDir(), uuid + ".png");
        if (avatarFile.exists()) {
            bitmap = BitmapFactory.decodeFile(avatarFile.getPath());
            if (bitmap != null) {
                memoryCache.put(uuid, bitmap);
                future.complete(bitmap);
                return future;
            }
        }

        String url = BASE_URL + "/users/" + uuid + "/avatar";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String base64Avatar = response.getString("avatar_url");
                        Bitmap avatarBitmap = decodeAndSaveAvatar(uuid, base64Avatar);
                        future.complete(avatarBitmap);
                    } catch (Exception e) {
                        Log.e("AvatarManager", "Error processing avatar response", e);
                        future.complete(null);
                    }
                },
                error -> {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
                    future.complete(defaultBitmap);
                });

        requestQueue.add(request);
        return future;
    }

    private Bitmap decodeAndSaveAvatar(String uuid, String base64Avatar) {
        try {
            byte[] decodedString = Base64.decode(base64Avatar, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (bitmap != null) {
                saveAvatarToFile(uuid, bitmap);
                memoryCache.put(uuid, bitmap);
                return bitmap;
            }
        } catch (IllegalArgumentException e) {
            Log.e("AvatarManager", "Error decoding avatar", e);
        }
        return null;
    }

    private void saveAvatarToFile(String uuid, Bitmap bitmap) {
        File avatarFile = new File(context.getFilesDir(), uuid + ".png");
        try (FileOutputStream out = new FileOutputStream(avatarFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            Log.e("AvatarManager", "Error saving avatar to file", e);
        }
    }

    public void clearCache() {
        memoryCache.evictAll();
        File cacheDir = context.getFilesDir();
        File[] files = cacheDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}