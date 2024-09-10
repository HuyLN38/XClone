package vn.edu.usth.x.community;

public class CommunityTweet {
    private String community;
    private int avatar;
    private String username;
    private String tweetlink;
    private String tweetText;
    private String time;
    private int image;

    public CommunityTweet(String community, int avatar, String username, String tweetlink, String tweetText, String time, int image) {
        this.community = community;
        this.avatar = avatar;
        this.username = username;
        this.tweetlink = tweetlink;
        this.tweetText = tweetText;
        this.time = time;
        this.image = image;
    }

    public String getCommunity() {
        return community;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
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
}
