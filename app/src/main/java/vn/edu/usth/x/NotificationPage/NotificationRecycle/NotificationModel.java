package vn.edu.usth.x.NotificationPage.NotificationRecycle;

public class NotificationModel{

    public String getUser_id() {
        return user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNotifier_id() {
        return notifier_id;
    }

    public void setNotifier_id(String notifier_id) {
        this.notifier_id = notifier_id;
    }

    public String getNotifier_username() {
        return notifier_username;
    }

    public void setNotifier_username(String notifier_username) {
        this.notifier_username = notifier_username;
    }

    public String getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(String tweet_id) {
        this.tweet_id = tweet_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String id;
    private String user_id;
    private String notifier_id;
    private String notifier_username;
    private String tweet_id;
    private String type;
    private boolean is_read;
    private String content;


    public NotificationModel(String id, String userId, String notifierId, String notifier_username, String tweetId, String type, String content) {
        this.id = id;
        this.user_id = userId;
        this.notifier_id = notifierId;
        this.notifier_username = notifier_username;
        this.tweet_id = tweetId;
        this.type = type;
        this.content = content;
        this.is_read = false;
    }

    public NotificationModel( String userId, String notifierId, String notifier_username, String tweetId, String type, String content) {
        this.user_id = userId;
        this.notifier_id = notifierId;
        this.notifier_username = notifier_username;
        this.tweet_id = tweetId;
        this.type = type;
        this.content = content;
        this.is_read = false;
    }

}