package vn.edu.usth.x.NotificationPage.NotificationRecycle;

public class NotificationModel{
    private String id;
    private String userId;
    private String tweetId;
    private String type;
    private String message;
    private String username;
    private String tweetText;
    private int avatar;

    public NotificationModel(String id, String tweetId, String userId, String type, String message, String username, String tweetText, int avatar) {
        this.id = id;
        this.tweetId = tweetId;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.username = username;
        this.tweetText = tweetText;
        this.avatar = avatar;
    }

    public NotificationModel(int avatar, String username, String message) {
        this.avatar = avatar;
        this.username = username;
        this.message = message;
    }

    public NotificationModel(String id, String userId, String tweetId, String type, String message) {
        this.id = id;
        this.userId = userId;
        this.tweetId = tweetId;
        this.type = type;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }
}