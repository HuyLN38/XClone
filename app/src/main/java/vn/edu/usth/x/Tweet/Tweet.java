package vn.edu.usth.x.Tweet;

import android.graphics.Bitmap;

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
    private int likeCount;
    private boolean isLike;

    //Constructor online mode
    public Tweet(String tweet_id, Bitmap avatar_bit, String username, String tweetlink, String tweetText, String time, Bitmap image_bit, int likeCount, boolean isLike) {
        this.tweet_id = tweet_id;
        this.avatar_bit = avatar_bit;
        this.username = username;
        this.tweetlink = "@"+ tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image_bit = image_bit;
        this.likeCount = likeCount;
        this.isLike = isLike;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
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

    public String getTweetText() {
        return tweetText;
    }

    public String getTime() {
        return time;
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

    public Bitmap getAvatar_bit() {
        return avatar_bit;
    }

    public String getTweet_id() {
        return tweet_id;
    }

    public void setLiked(boolean isLike){
        this.isLike = isLike;
    }

    public boolean isLiked(){
        return this.isLike;
    }
}