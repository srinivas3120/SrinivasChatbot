package com.srinivas.mudavath.pojo;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class ResponseItem {

    int success;
    String errorType;
    String errorMessage;
    MessageResponse message;

    public ResponseItem(int success, String errorType, String errorMessage, MessageResponse message) {
        this.success = success;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MessageResponse getMessage() {
        return message;
    }

    public void setMessage(MessageResponse message) {
        this.message = message;
    }
}
