package vn.edu.usth.x.InboxPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import vn.edu.usth.x.Utils.UserFunction;

public class WebSocketManager {
    private static final UserFunction userFunction = new UserFunction();
    private static final String WEBSOCKET_URL = "wss://huyln.info/socket/ws/chat?userId=";
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final ChatCallback callback;
    private final Gson gson;
    private boolean isConnected = false;

    public interface ChatCallback {
        void onMessageReceived(Message message);
        void onStatusUpdate(String messageId, String status);
        void onConnectionStateChange(boolean connected);
    }

    public WebSocketManager(ChatCallback callback) {
        this.callback = callback;
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // No timeout for WebSocket
                .build();
    }

    public void connect(Context context) {
        String userId = userFunction.getUserId(context);
        Log.i("WebSocketManager", WEBSOCKET_URL + userId);
        Request request = new Request.Builder()
                .url(WEBSOCKET_URL + userId)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnected = true;
                callback.onConnectionStateChange(true);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject jsonMessage = new JSONObject(text);
                    handleIncomingMessage(jsonMessage);
                    handleMessage(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void handleIncomingMessage(JSONObject jsonMessage) {
                // Process the JSON message here
                System.out.println("Received message: " + jsonMessage.toString());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                isConnected = false;
                callback.onConnectionStateChange(false);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isConnected = false;
                callback.onConnectionStateChange(false);
                reconnect(context);
            }
        });
    }

    private void handleMessage(String text) {
        try {
            JsonObject json = gson.fromJson(text, JsonObject.class);
            String type = json.get("type").getAsString();

            switch (type) {
                case "new_message":
                    Message message = gson.fromJson(json.get("message"), Message.class);
                    callback.onMessageReceived(message);
                    break;
                case "status":
                    String messageId = json.get("id").getAsString();
                    String status = json.get("status").getAsString();
                    callback.onStatusUpdate(messageId, status);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message, Context context) {
        if (!isConnected) {
            reconnect(context);
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", "message");
        json.add("data", gson.toJsonTree(message));
        webSocket.send(json.toString());
        Log.e("TEST","Message sent: " + json);
    }

    private void reconnect(Context context) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> connect(context), 5000);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User disconnected");
        }
    }
}