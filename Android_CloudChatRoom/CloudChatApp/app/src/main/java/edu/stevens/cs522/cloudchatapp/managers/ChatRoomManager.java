package edu.stevens.cs522.cloudchatapp.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.cloudchatapp.Entities.ChatRoom;
import edu.stevens.cs522.cloudchatapp.factory.IContinue;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.factory.ISimpleQueryListener;

/**
 * Created by nisha0634 on 4/24/15.
 */
public class ChatRoomManager extends Manager<ChatRoom> {
    public ChatRoomManager(Context context, IEntityCreator<ChatRoom> creator, int loaderID){
        super(context, creator, loaderID);
    }

    public void ChatRoomExecuteSimpleQuery(Uri uri, String[] projection, String select, String[] selectionArgs, ISimpleQueryListener<ChatRoom> listener, int queryId){
        super.executeSimpleQuery(uri, projection, select, selectionArgs, listener, queryId);
    }

    public void ChatRoomExecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<ChatRoom> listener){
        super.executeQuery(uri, projection, select, selectionArgs, listener);
    }

    public void ChatRoomReexecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<ChatRoom>listener){
        super.reexecuteQuery(uri, projection, select, selectionArgs, listener);
    }

    public void ChatRoomExecuteInsert(Uri uri, ContentValues values, IContinue<Uri> callback, int insertId){
        super.executeInsert(uri, values, callback, insertId);
    }

    public void ChatRoomExecuteUpdate(Uri uri, ContentValues values, IContinue<Uri> callback, int updateId, String selection, String[] selectionArgs){
        super.executeUpdate(uri, values, callback, updateId, selection, selectionArgs);
    }

    public void ChatRoomExecuteDelete(Uri uri, IContinue<Integer> callback, int deleteId, String selection, String[] selectionArgs){
        super.executeDelete(uri, callback, deleteId, selection, selectionArgs);
    }
}
