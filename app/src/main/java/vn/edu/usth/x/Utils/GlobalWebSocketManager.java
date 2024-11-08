package vn.edu.usth.x.Utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import vn.edu.usth.x.InboxPage.Chat.Chat;
import vn.edu.usth.x.InboxPage.Message.Message;
import vn.edu.usth.x.NotificationPage.NotificationRecycle.NotificationModel;

public class GlobalWebSocketManager {
    private static final String WEBSOCKET_URL = "wss://huyln.info/socket/ws/chat?userId=";
    private static GlobalWebSocketManager instance;
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final Gson gson;
    private boolean isConnected = false;

    // LiveData for broadcasting events
    private final MutableLiveData<Message> newMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Chat> newChatLiveData = new MutableLiveData<>();
    private final MutableLiveData<MessageStatus> messageStatusLiveData = new MutableLiveData<>();
    private final MutableLiveData<NotificationModel> newNotificationLiveData = new MutableLiveData<>();
    private final MutableLiveData<NotificationStatus> notificationStatusLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> connectionStateLiveData = new MutableLiveData<>();


    public static class NotificationStatus {
        public String id;

        public NotificationStatus(String notificationId) {
            this.id = notificationId;
        }
    }
    public static class MessageStatus {
        public String messageId;

        public MessageStatus(String messageId) {
            this.messageId = messageId;
        }
    }

    private GlobalWebSocketManager() {
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }

    public static synchronized GlobalWebSocketManager getInstance() {
        if (instance == null) {
            instance = new GlobalWebSocketManager();
        }
        return instance;
    }

    public LiveData<Boolean> getConnectionStateLiveData() {
        return connectionStateLiveData;
    }

    public void connect(Context context) {
        String userId = UserFunction.getUserId(context);
        if (userId == null) return;

        Log.i("GlobalWebSocketManager", WEBSOCKET_URL + userId);
        Request request = new Request.Builder()
                .url(WEBSOCKET_URL + userId)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnected = true;
                Log.d("GlobalWebSocketManager", "WebSocket Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                isConnected = false;
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isConnected = false;
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
                    newMessageLiveData.postValue(message);
                    // Also create/update chat for inbox list
                    Chat chat = new Chat(
                            json.getAsJsonObject("message").get("sender_display_name").getAsString(),
                            message.getContent(),
                            message.getRecipientId()
                    );
                    newChatLiveData.postValue(chat);
                    break;
                case "ping":{
                    webSocket.send("pong");
                    Log.d("WEBSOCKET", "Pong sent in response to ping");
                }
                case "status":
                    String messageId = json.get("id").getAsString();
                    messageStatusLiveData.postValue(new MessageStatus(messageId));
                    break;
                case "new_notifications":
                    NotificationModel notification = gson.fromJson(json.get("message"), NotificationModel.class);
                    newNotificationLiveData.postValue(notification);
//                    notificationStatusLiveData.postValue(new NotificationStatus(notifications));
                    break;
            }
        } catch (Exception e) {
            Log.e("GlobalWebSocketManager", "Error handling message", e);
        }
    }

    public void sendMessage(Message message) {
        if (!isConnected) return;

        JsonObject json = new JsonObject();
        json.addProperty("type", "message");
        json.add("data", gson.toJsonTree(message));
        webSocket.send(json.toString());
    }

    public void sendNotification(NotificationModel notification) {
        if (!isConnected) return;

        JsonObject json = new JsonObject();
        json.addProperty("type", "notifications");
        json.add("data", gson.toJsonTree(notification));
        webSocket.send(json.toString());
        Log.d("GlobalWebSocketManager", json.toString());
    }

    private void reconnect(Context context) {
        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> connect(context), 1000);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "App closed");
        }
    }

    // Getters for LiveData
    public LiveData<Message> getNewMessageLiveData() {
        return newMessageLiveData;
    }


    public LiveData<MessageStatus> getMessageStatusLiveData() {
        return messageStatusLiveData;
    }

    public LiveData<NotificationModel> getNewNotificationLiveData() {
        return newNotificationLiveData;
    }
}