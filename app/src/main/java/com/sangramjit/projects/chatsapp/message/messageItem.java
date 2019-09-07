package com.sangramjit.projects.chatsapp.message;

public class messageItem {
    private String message,time,senderid;

    public messageItem(String message,String time,String senderid){
        this.message=message;
        this.time=time;
        this.senderid=senderid;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderid() {
        return senderid;
    }

    public String getTime() {
        return time;
    }
}
