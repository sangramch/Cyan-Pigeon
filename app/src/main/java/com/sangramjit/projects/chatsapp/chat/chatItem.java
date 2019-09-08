package com.sangramjit.projects.chatsapp.chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class chatItem implements Parcelable {
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

    protected chatItem(Parcel parcel){
        this.ChatID=parcel.readString();
        this.LastMessage=parcel.readString();
        this.Title=parcel.readString();
        this.TimeStamp=parcel.readString();
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

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public void setTitle(String title) {
        Title = title;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ChatID);
        parcel.writeString(this.LastMessage);
        parcel.writeString(this.Title);
        parcel.writeString(this.TimeStamp);
    }

    public static final Creator<chatItem> CREATOR = new Creator<chatItem>() {
        @Override
        public chatItem createFromParcel(Parcel parcel) {
            return new chatItem(parcel);
        }

        @Override
        public chatItem[] newArray(int i) {
            return new chatItem[i];
        }
    };

    public static Creator<chatItem> getCREATOR(){
        return CREATOR;
    }
}
