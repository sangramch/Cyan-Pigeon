package com.sangramjit.projects.chatsapp.user;

import java.io.Serializable;

public class userItem implements Serializable {

    private String Number;
    private String Name;
    private String UID;

    public userItem(String number, String name, String uid) {
        Number = number;
        Name = name;
        UID = uid;
    }

    public String getNumber(){ return Number; }

    public String getName() { return Name; }

    public String getUID() { return UID; }
}
