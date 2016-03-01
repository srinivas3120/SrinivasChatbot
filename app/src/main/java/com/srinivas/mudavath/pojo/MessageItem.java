package com.srinivas.mudavath.pojo;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class MessageItem {

    public static int SENT=0;
    public static int RECEIVED=1;
    int messageType; // 0-sent  1-received
    String message;

    public MessageItem(int messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
