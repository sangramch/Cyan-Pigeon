package com.sangramjit.projects.chatsapp.message;

import java.io.Serializable;

public class messageItem implements Serializable {
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
