package vn.edu.usth.x.InboxPage;

import android.graphics.Bitmap;

public class Chat {
    private String displayName;
    private String lastMessage;
    private Bitmap avatarBitmap;

    public Chat(String displayName, String lastMessage) {
        this.displayName = displayName;
        this.lastMessage = lastMessage;
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
}
