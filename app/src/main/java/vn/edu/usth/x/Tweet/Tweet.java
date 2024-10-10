package vn.edu.usth.x.Tweet;

import android.graphics.Bitmap;

// Tweet.java
public class Tweet {

    private String tweet_id;
    private int avatar;

    private Bitmap avatar_bit;

    private String username;
    private String tweetlink;
    private String tweetText;
    private String time;
    private Bitmap image_bit;

    private int image;

    //Constructor online mode
    public Tweet(String tweet_id,Bitmap avatar_bit, String username, String tweetlink, String tweetText, String time, Bitmap image_bit) {
        this.tweet_id = tweet_id;
        this.avatar_bit = avatar_bit;
        this.username = username;
        this.tweetlink = "@"+ tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image_bit = image_bit;
    }

    //Constructor offline mode
    public Tweet(int avatar, String username, String tweetlink, String tweetText, String time, int  image) {
        this.avatar = avatar;
        this.username = username;
        this.tweetlink = "@"+ tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image  = image;
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

    public Bitmap getImage_bit() {
        return image_bit;
    }

    public void setImage_bit(Bitmap image_bit) {
        this.image_bit = image_bit;
    }

    public Bitmap getAvatar_bit() {
        return avatar_bit;
    }

    public void setAvatar_bit(Bitmap avatar_bit) {
        this.avatar_bit = avatar_bit;
    }

    public String getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(String tweet_id) {
        this.tweet_id = tweet_id;
    }

}