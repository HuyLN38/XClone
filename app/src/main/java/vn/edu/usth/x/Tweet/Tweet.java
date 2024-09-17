package vn.edu.usth.x.Tweet;

// Tweet.java
public class Tweet {
    private int avatar;
    private String username;
    private String tweetlink;
    private String tweetText;
    private String time;
    private int image;

    public Tweet(int avatar, String username, String tweetlink, String tweetText, String time, int image) {
        this.avatar = avatar;
        this.username = username;
        this.tweetlink = "@"+ tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image = image;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTweetlink() {
        return tweetlink;
    }

    public void setTweetlink(String tweetlink) {
        this.tweetlink = tweetlink;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}