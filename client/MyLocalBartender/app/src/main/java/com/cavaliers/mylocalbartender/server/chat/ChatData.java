package com.cavaliers.mylocalbartender.server.chat;

public class ChatData {
    private boolean myMessage;
    public String message;

    public ChatData(boolean myMessage, String message) {
        this.myMessage = myMessage;
        this.message = message;
    }

    public boolean isMyMessage() {
        return myMessage;
    }

    public String getMessage() {
        return message;
    }
}
