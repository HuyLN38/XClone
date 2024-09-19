package vn.edu.usth.x.NotificationPage.NotificationRecycle;

public class NotificationModel {
    private int avatar;
    private String username;
    private String tweetText;

    public NotificationModel(int avatar, String username, String tweetText) {
        this.avatar = avatar;
        this.username = username;
        this.tweetText = tweetText;
    }


    public int getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getTweetText() {
        return tweetText;
    }


}
