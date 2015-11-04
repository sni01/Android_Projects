package edu.stevens.cs522.BookstoreWithEntityManager.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;

import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IEntityCreator;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IQueryListener;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.ISimpleQueryListener;

/**
 * Created by nisha0634 on 2/21/15.
 */
/*
 * specific T = Book
 */
public class BookManager<Book> extends Manager{
    //get content resolvers from parent

    public BookManager(Activity context, IEntityCreator<Book> creator, int loaderID){
        super(context, creator, loaderID);
    }

    public void BookExecuteSimpleQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, ISimpleQueryListener<Book> listener){
        super.executeSimpleQuery(uri, projection, selection, selectionArgs, listener);
    }

    public void BookExecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<Book> listener){
        super.executeQuery(uri, projection, select, selectionArgs, listener);
    }

    public void BookReexecuteQuery(Uri uri, String[] projection, String select, String[] selectionArgs, IQueryListener<Book>listener){
        super.reexecuteQuery(uri, projection, select, selectionArgs, listener);
    }

    public void BookExecuteDelete(Uri uri, String select, String[] selectArgs, IContinue<Integer> callback){
        super.executeDelete(uri, select, selectArgs, callback);
    }

    public void BookExecuteInsert(Uri uri, ContentValues values, IContinue<Uri> callback){
        super.executeInsert(uri, values, callback);
    }
}
