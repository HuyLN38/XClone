package vn.edu.usth.x.InboxPage.Chat;

import android.graphics.Bitmap;

public class Chat {
    private final String displayName;
    private String lastMessage;
    private Bitmap avatarBitmap;
    private final String recipientId;

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

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getRecipientId() {
        return recipientId;
    }


}
