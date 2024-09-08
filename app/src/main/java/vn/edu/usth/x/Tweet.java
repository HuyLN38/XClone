package vn.edu.usth.x;

// Tweet.java
public class Tweet {
    private String username;
    private String tweetText;

    public Tweet(String username, String tweetText) {
        this.username = username;
        this.tweetText = tweetText;
    }

    public String getUsername() {
        return username;
    }

    public String getTweetText() {
        return tweetText;
    }
}