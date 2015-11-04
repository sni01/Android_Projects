package edu.stevens.cs522.cloudchatapp.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.cloudchatapp.Entities.Message;
import edu.stevens.cs522.cloudchatapp.factory.IContinue;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.factory.ISimpleQueryListener;

/**
 * Created by nisha0634 on 3/12/15.
 */
public class MessageManager extends Manager {
    public MessageManager(Context context, IEntityCreator<Message> creator, int loaderID){
        super(context, creator, loaderID);
    }

    public void MessageExecuteSimpleQuery(Uri uri, String[] projection, String select, String[] selectionArgs, ISimpleQueryListener<Message> listener, int queryId){
        super.executeSimpleQuery(uri, projection, select, selectionArgs, listener, queryId);
    }

    public void MessageExecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<Message> listener){
        super.executeQuery(uri, projection, select, selectionArgs, listener);
    }

    public void MessageReexecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<Message>listener){
        super.reexecuteQuery(uri, projection, select, selectionArgs, listener);
    }

    public void MessageExecuteInsert(Uri uri, ContentValues values, IContinue<Uri> callback, int insertId){
        super.executeInsert(uri, values, callback, insertId);
    }

    public void MessageExecuteUpdate(Uri uri, ContentValues values, IContinue<Integer> callback, int updateId, String selection, String[] selectionArgs){
        super.executeUpdate(uri, values, callback, updateId, selection, selectionArgs);
    }

    public void MessageExecuteDelete(Uri uri, IContinue<Integer> callback, int deleteId, String selection, String[] selectionArgs){
        super.executeDelete(uri, callback, deleteId, selection, selectionArgs);
    }
}