package vn.edu.usth.x.InboxPage.AddChatActivity;

public class UserItem {
    private String id;
    private String username;
    private String displayName;
    private String avatarUrl;

    public UserItem(String id, String username, String displayName, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
}