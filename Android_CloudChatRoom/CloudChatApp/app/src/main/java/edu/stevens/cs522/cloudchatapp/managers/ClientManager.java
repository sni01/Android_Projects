package edu.stevens.cs522.cloudchatapp.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.cloudchatapp.Entities.Sender;
import edu.stevens.cs522.cloudchatapp.factory.IContinue;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.factory.ISimpleQueryListener;

/**
 * Created by nisha0634 on 4/18/15.
 */
public class ClientManager extends Manager{
    public ClientManager(Context context, IEntityCreator<Sender> creator, int loaderID){
        super(context, creator, loaderID);
    }

    public void MessageExecuteSimpleQuery(Uri uri, String[] projection, String select, String[] selectionArgs, ISimpleQueryListener<Sender> listener, int queryId){
        super.executeSimpleQuery(uri, projection, select, selectionArgs, listener, queryId);
    }

    public void MessageExecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<Sender> listener){
        super.executeQuery(uri, projection, select, selectionArgs, listener);
    }

    public void MessageReexecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<Sender>listener){
        super.reexecuteQuery(uri, projection, select, selectionArgs, listener);
    }

    public void MessageExecuteInsert(Uri uri, ContentValues values, IContinue<Uri> callback, int insertId){
        super.executeInsert(uri, values, callback, insertId);
    }

    public void MessageExecuteUpdate(Uri uri, ContentValues values, IContinue<Uri> callback, int updateId, String selection, String[] selectionArgs){
        super.executeUpdate(uri, values, callback, updateId, selection, selectionArgs);
    }

    public void MessageExecuteDelete(Uri uri, IContinue<Integer> callback, int deleteId, String selection, String[] selectionArgs){
        super.executeDelete(uri, callback, deleteId, selection, selectionArgs);
    }
}
