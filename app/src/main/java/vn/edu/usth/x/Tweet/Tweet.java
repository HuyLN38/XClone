package vn.edu.usth.x.Tweet;

import android.graphics.Bitmap;

import java.util.concurrent.atomic.AtomicReference;

public class Tweet {

    private String tweet_id;
    private int avatar;
    private String user_id;
    private AtomicReference<Bitmap> avatar_bit;
    private String username;
    private String tweetlink;
    private String tweetText;
    private String time;
    private Bitmap image_bit;
    private int image;
    private int likeCount;
    private boolean isLike;
    private int commentCount;
    private int reTweetCount;
    private int seenCount;
    private boolean isFollowed;

    //Constructor online mode
    public Tweet(String tweet_id, String userID, AtomicReference<Bitmap> avatar_bit, String username, String tweetlink, String tweetText, String time, Bitmap image_bit, int likeCount, boolean isLike, int commentCount, int reTweetCount, int seenCount) {
        this.tweet_id = tweet_id;
        this.user_id = userID;
        this.avatar_bit = avatar_bit;
        this.username = username;
        this.tweetlink = "@"+ tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image_bit = image_bit;
        this.likeCount = likeCount;
        this.isLike = isLike;
        this.commentCount = commentCount;
        this.reTweetCount = reTweetCount;
        this.seenCount = seenCount;
    }

    public Tweet(String tweet_id, String userID, AtomicReference<Bitmap> avatar_bit, String username, String tweetlink, String tweetText, String time, Bitmap image_bit, int likeCount, boolean isLike, int commentCount, int reTweetCount, int seenCount, boolean isFollowed) {
        this.tweet_id = tweet_id;
        this.user_id = userID;
        this.avatar_bit = avatar_bit;
        this.username = username;
        this.tweetlink = "@"+ tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image_bit = image_bit;
        this.likeCount = likeCount;
        this.isLike = isLike;
        this.commentCount = commentCount;
        this.reTweetCount = reTweetCount;
        this.seenCount = seenCount;
        this.isFollowed = isFollowed;
    }

    public String getUser_id() {
        return user_id;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean follwed) {
        isFollowed = follwed;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
        return avatar_bit.get();
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

    public int getCommentCount() {return this.commentCount;}

    public int getReTweetCount() {return this.reTweetCount;}

    public int getSeenCount() {return this.seenCount;}

    public void setCommentCount(int commentCount) {this.commentCount = commentCount;}

    public void setReTweetCount(int reTweetCount) {this.reTweetCount = reTweetCount;}

    public void setSeenCount(int seenCount) {this.seenCount = seenCount;}
}