package com.srinivas.mudavath.pojo;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class MessageItem {

    public static String SEND_TYPE="s";
    public static String RECEIVE_TYPE="r";
    public static int PENDING_STATUS=0;
    public static int SENT_STATUS=1;

    String messageId;
    String createdAt;
    String messageType; // s-send  r-receive
    int messageStatus;
    String message;

    public MessageItem(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public MessageItem(String messageId, String createdAt, String messageType, int messageStatus, String message) {
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.messageType = messageType;
        this.messageStatus = messageStatus;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
