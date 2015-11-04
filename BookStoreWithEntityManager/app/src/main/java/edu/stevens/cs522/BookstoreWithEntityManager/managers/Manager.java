package edu.stevens.cs522.BookstoreWithEntityManager.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IEntityCreator;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IQueryListener;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.ISimpleQueryListener;
import edu.stevens.cs522.BookstoreWithEntityManager.queryBuilder.QueryBuilder;
import edu.stevens.cs522.BookstoreWithEntityManager.queryBuilder.SimpleQueryBuilder;
import edu.stevens.cs522.BookstoreWithEntityManager.resolvers.AsyncContentResolver;

/**
 * Created by nisha0634 on 2/21/15.
 */
/*
 * provide generic class for async requests
 */
public abstract class Manager<T> {

    //variables
    private final Activity context;
    private final IEntityCreator<T> helper;

    private final String tag;

    //content provider
    private ContentResolver syncResolver;
    private AsyncContentResolver asyncContentResolver;


    //tokens for CRUD operation
    private final int loaderID;
    private static final int INSERT_BOOK = 3;
    private static final int DELETE_BOOKS = 4;

    //constructor
    protected Manager(Activity context, IEntityCreator<T> helper,int loaderID){
        this.context = context;
        this.helper = helper;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    //initial content solver
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
        QueryBuilder.executeQuery(context, uri, loaderID, projection, selection, selectionArgs, helper, listener);
    }

    //query by calling SimpleQueryBuilder
    protected void executeSimpleQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, ISimpleQueryListener<T> listener){
        SimpleQueryBuilder.executeQuery(context, uri, loaderID, projection, selection, selectionArgs, helper, listener);
    }

    protected void reexecuteQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, IQueryListener<T> listener){
        QueryBuilder.reexecuteQuery(context, uri, loaderID, projection, selection, selectionArgs, helper, listener);
    }

    protected void executeInsert(Uri uri, ContentValues values, IContinue<Uri> callback){
        getAsyncContentResolver();
        asyncContentResolver.persistAsync(INSERT_BOOK, callback, uri, values);
    }

    protected void executeDelete(Uri uri, String select, String[] selectArgs, IContinue<Integer> callback){
        getAsyncContentResolver();
        asyncContentResolver.deleteAsync(DELETE_BOOKS, uri, select, selectArgs, callback);
    }
}
