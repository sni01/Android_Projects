package edu.stevens.cs522.cloudchatapp.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.cloudchatapp.factory.IContinue;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.factory.ISimpleQueryListener;
import edu.stevens.cs522.cloudchatapp.queryBuilders.QueryBuilder;
import edu.stevens.cs522.cloudchatapp.queryBuilders.SimpleQueryBuilder;
import edu.stevens.cs522.cloudchatapp.resolvers.AsyncContentResolver;

/**
 * Created by nisha0634 on 3/12/15.
 */
public abstract class Manager<T> {

    /*
     * manager variables
     */
    private final Context context;
    private final IEntityCreator<T> helper;
    private final int loaderID;

    //providers
    private ContentResolver syncResolver;
    private AsyncContentResolver asyncContentResolver;

    protected Manager(Context context, IEntityCreator<T> helper, int loaderID){
        this.context = context;
        this.helper = helper;
        this.loaderID = loaderID;
    }

    protected ContentResolver getContentResolver(){
        if(syncResolver == null){
            syncResolver = context.getContentResolver();
        }
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncContentResolver(){
        if(asyncContentResolver == null){
            asyncContentResolver = new AsyncContentResolver(context.getContentResolver());
        }
        return asyncContentResolver;
    }

    //cursor loader query by calling QueryBuilder
    protected void executeQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, IQueryListener<T> listener){
        QueryBuilder.executeQuery((Activity) context, uri, loaderID, helper, listener, projection, selection, selectionArgs);
    }

    protected void reexecuteQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, IQueryListener<T> listener){
        QueryBuilder.reexecuteQuery((Activity)context, uri, loaderID, helper, listener, projection, selection, selectionArgs);
    }

    //query by calling SimpleQueryBuilder
    protected void executeSimpleQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, ISimpleQueryListener<T> listener, int queryId){
        SimpleQueryBuilder.executeQuery((Activity) context, uri, projection, selection, selectionArgs, helper, listener, queryId);
    }

    protected void executeInsert(Uri uri, ContentValues values, IContinue<Uri> callback, int insertId){
        getAsyncContentResolver();
        asyncContentResolver.persistAsync(insertId, callback, uri, values);
    }

    protected void executeUpdate(Uri uri, ContentValues values, IContinue<Uri> callback, int updateId, String selection, String[] selectionArgs){
        getAsyncContentResolver();
        asyncContentResolver.updateAsync(updateId, callback, uri, values, selection, selectionArgs);
    }

    protected void executeDelete(Uri uri, IContinue<Integer> callback, int deleteId, String selection, String[] selectArgs){
        getAsyncContentResolver();
        asyncContentResolver.deleteAsync(deleteId, callback, uri, selection, selectArgs);
    }
}
