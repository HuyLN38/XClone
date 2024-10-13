package vn.edu.usth.x.InboxPage;

import android.graphics.Bitmap;

public class Chat {
    private String displayName;
    private String lastMessage;
    private Bitmap avatarBitmap;
    private String recipientId;

    public Chat(String displayName, String lastMessage, String recipientId) {
        this.displayName = displayName;
        this.lastMessage = lastMessage;
        this.recipientId = recipientId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setAvatarBitmap(Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }

    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }

    public String getRecipientId() {
        return recipientId;
    }
}
