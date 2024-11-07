package vn.edu.usth.x.InboxPage;

import java.util.UUID;

public class Message {
    private final String id;
    private final String sender_id;
    private final String recipient_id;
    private final String content;


    public Message(String id , String senderId, String recipientId, String content) {
        this.id = id;
        this.sender_id = senderId;
        this.recipient_id = recipientId;
        this.content = content;

    }

    public Message(String senderId, String recipientId, String content) {
        this.id = UUID.randomUUID().toString();
        this.sender_id = senderId;
        this.recipient_id = recipientId;
        this.content = content;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getSenderId() { return sender_id; }
    public String getContent() { return content; }
    public String getRecipientId() {
        return recipient_id;
    }
}