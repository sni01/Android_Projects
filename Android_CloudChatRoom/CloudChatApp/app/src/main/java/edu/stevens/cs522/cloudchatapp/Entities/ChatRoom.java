package edu.stevens.cs522.cloudchatapp.Entities;

import android.content.ContentValues;
import android.database.Cursor;

import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 4/24/15.
 */
public class ChatRoom {
    public long id;
    public String chatRoomName;

    public ChatRoom(String chatRoomName){
        this.chatRoomName = chatRoomName;
    }


    public ChatRoom(Cursor cursor){
        this.chatRoomName = MessageContract.getChatRoomName(cursor);
    }

    public void writeToProvider(ContentValues values){
        values.put(MessageContract.CHAT_ROOM_NAME, this.chatRoomName);
    }

    public String getChatRoomName(){ return this.chatRoomName; }
}
