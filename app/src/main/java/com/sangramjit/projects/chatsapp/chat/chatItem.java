package com.sangramjit.projects.chatsapp.chat;

import java.io.Serializable;

public class chatItem implements Serializable {
    private String ChatID;
    private String LastMessage;
    private String Title;
    private String TimeStamp;

    public chatItem(String timeStamp, String LastMessage, String Title, String chatID){
        this.ChatID=chatID;
        this.LastMessage=LastMessage;
        this.Title=Title;
        this.TimeStamp=timeStamp;
    }

    public String getChatID() {
        return ChatID;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public String getTitle() {
        return Title;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }
}
