package vn.edu.usth.x.InboxPage.AddChatActivity;

public class UserItem {
    private String currentUsername;
    private String currentID;
    private String DisplayName;
    private boolean isVerified;
    private Integer following;
    private Integer followers;

    public UserItem(String currentUsername, String currentID, String displayName, boolean isVerified, Integer following, Integer followers) {
        this.currentUsername = currentUsername;
        this.currentID = currentID;
        this.DisplayName = displayName;
        this.isVerified = isVerified;
        this.following = following;
        this.followers = followers;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public String getCurrentID() {
        return currentID;
    }

    public void setCurrentID(String currentID) {
        this.currentID = currentID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }
}