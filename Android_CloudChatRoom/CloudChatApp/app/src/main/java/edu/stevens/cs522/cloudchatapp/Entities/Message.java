package edu.stevens.cs522.cloudchatapp.Entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class Message implements Parcelable {
    public long id;
    public String messageContent;
    public long sequence;
    public long timestamp;
    public String username;
    //public String chatRoomId;
    public String latitude;
    public String longitude;
    public long chatRoomFK;

    //constructor
    public Message(String content, long sequence, long timestamp, String username, String latitude, String longitude, long chatRoomFK){
        this.messageContent = content;
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.chatRoomFK = chatRoomFK;
    }

    public void setChatRoomFK(long chatRoomFK){
        this.chatRoomFK = chatRoomFK;
    }

    //parcelable functions
    public int describeContents(){ return 0; }

    private Message(Parcel in){
        this.id = in.readLong();
        this.messageContent = in.readString();
        this.sequence = in.readLong();
        this.timestamp = in.readLong();
        this.username = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.chatRoomFK = in.readLong();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(id);
        out.writeString(messageContent);
        out.writeLong(sequence);
        out.writeLong(timestamp);
        out.writeString(username);
        out.writeString(latitude);
        out.writeString(longitude);
        out.writeLong(chatRoomFK);
    }

    public Message(Cursor cursor){
        this.messageContent = MessageContract.getContent(cursor);
        this.sequence = MessageContract.getSequence(cursor);
        this.timestamp = MessageContract.getTimestamp(cursor);
        this.username = MessageContract.getSenderUsername(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
        this.longitude = MessageContract.getLongitude(cursor);
        this.chatRoomFK = MessageContract.getChatRoomFK(cursor);
    }

    public void writeToProvider(ContentValues values){
        values.put(MessageContract.CONTENT, this.messageContent);
        values.put(MessageContract.SEQUENCE_NUMBER, this.sequence);
        values.put(MessageContract.TIMESTAMP, this.timestamp);
        values.put(MessageContract.SENDER_USERNAME, this.username);
        values.put(MessageContract.LATITUDE, this.latitude);
        values.put(MessageContract.LONGITUDE, this.longitude);
        values.put(MessageContract.CHAT_ROOM_FK, this.chatRoomFK);
    }
}
