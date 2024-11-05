package vn.edu.usth.x.InboxPage;

import java.util.UUID;

public class Message {
    private String id;
    private String sender_id;
    private String recipient_id;
    private String content;
    private String timestamp;
    private String status; // "sent", "delivered", "seen"

    public Message(String id , String senderId, String recipientId, String content) {
        this.id = id;
        this.sender_id = senderId;
        this.recipient_id = recipientId;
        this.content = content;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.status = "sent";
    }

    public Message(String senderId, String recipientId, String content) {
        this.id = UUID.randomUUID().toString();
        this.sender_id = senderId;
        this.recipient_id = recipientId;
        this.content = content;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.status = "sent";
    }

    // Getters and setters
    public String getId() { return id; }
    public String getSenderId() { return sender_id; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}