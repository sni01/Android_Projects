package edu.stevens.cs522.cloudchatapp.resolvers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.cloudchatapp.factory.IContinue;

/**
 * Created by nisha0634 on 2/17/15.
 */
public class AsyncContentResolver extends AsyncQueryHandler {

    //constructor
    public AsyncContentResolver(ContentResolver cr){
        super(cr);
    }

    /*
     * CRUD methods
     */
    public void persistAsync(int token, IContinue<Uri> callback, Uri uri, ContentValues values){
        this.startInsert(token, callback, uri, values);
    }

    public void queryAsync(int token, IContinue<Cursor> callback, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
        this.startQuery(token, callback, uri, projection, selection, selectionArgs, orderBy);
    }

    //why this update parameters are not int good order
    public void updateAsync(int token, IContinue<Uri> callback, Uri uri, ContentValues values, String selection, String[] selectionArgs){
        this.startUpdate(token, callback, uri, values, selection, selectionArgs);
    }

    public void deleteAsync(int token, IContinue<Integer> callback, Uri uri, String selection, String[] selectionArgs){
        this.startDelete(token, callback, uri, selection, selectionArgs);
    }

    /*
     * complete methods
     */
    public void onInsertComplete(int token, Object cookie, Uri uri){
        IContinue<Uri> callback = (IContinue<Uri>) cookie;
        callback.kontinue(uri);
    }

    public void onQueryComplete(int token, Object cookie, Cursor cursor){
        IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
        callback.kontinue(cursor);
    }

    public void onUpdateComplete(int token, Object cookie, int result){
        IContinue<Integer> callback = (IContinue<Integer>) cookie;
        callback.kontinue(result);
    }

    public void onDeleteComplete(int token, Object cookie, int result){
        IContinue<Integer> callback = (IContinue<Integer>) cookie;
        callback.kontinue(result);
    }

}
