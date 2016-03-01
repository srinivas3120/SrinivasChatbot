package com.srinivas.mudavath.pojo;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class MessageResponse {

    String chatBotName;
    int chatBotID;
    String message;
    String emotion;

    public MessageResponse(String chatBotName, int chatBotID, String message, String emotion) {
        this.chatBotName = chatBotName;
        this.chatBotID = chatBotID;
        this.message = message;
        this.emotion = emotion;
    }

    public String getChatBotName() {
        return chatBotName;
    }

    public void setChatBotName(String chatBotName) {
        this.chatBotName = chatBotName;
    }

    public int getChatBotID() {
        return chatBotID;
    }

    public void setChatBotID(int chatBotID) {
        this.chatBotID = chatBotID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
