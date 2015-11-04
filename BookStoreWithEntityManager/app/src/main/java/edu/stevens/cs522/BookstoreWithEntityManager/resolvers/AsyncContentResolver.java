package edu.stevens.cs522.BookstoreWithEntityManager.resolvers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;

/**
 * Created by nisha0634 on 2/17/15.
 */
public class AsyncContentResolver extends AsyncQueryHandler {

    //Log variables
    private static final String CRUD_TAG = "CRUD response: ";

    //constructor
    public AsyncContentResolver(ContentResolver cr){
        super(cr);
    }

    //token variables
//    private static final int FETCH_BOOKS = 1;
//    private static final int FETCH_BOOK = 2;
//    private static final int INSERT_BOOK = 3;
//    private static final int DELETE_BOOKS = 4;


    //Async CRUD methods
    public void persistAsync(int token, IContinue<Uri> callback, Uri uri, ContentValues values){
        this.startInsert(token, callback, uri, values);
    }

    //query
    public void queryAsync(int token, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy, IContinue<Cursor> callback){
        this.startQuery(token, callback, uri, projection, selection, selectionArgs, orderBy);
    }

    //delete
    public void deleteAsync(int token, Uri uri, String selection, String[] selectionArgs, IContinue<Integer> callback){
        this.startDelete(token, callback, uri, selection, selectionArgs);
    }

    //update
    public void updateAsync(int token, Uri uri, ContentValues values, String selection, String[] selectionArgs){
        this.startUpdate(token, null, uri, values, selection, selectionArgs);
    }


    //Async CURD complete methods
    public void onInsertComplete(int token, Object cookie, Uri uri){
        IContinue<Uri> callback = (IContinue<Uri>) cookie;
        callback.kontinue(uri);
    }

    public void onQueryComplete(int token, Object cookie, Cursor cursor){
        IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
        callback.kontinue(cursor);
    }

    public void onUpdateComplete(int token, Object cookie, int result){

    }

    public void onDeleteComplete(int token, Object cookie, int result){
        IContinue<Integer> callback = (IContinue<Integer>) cookie;
        callback.kontinue(result);
    }

}
