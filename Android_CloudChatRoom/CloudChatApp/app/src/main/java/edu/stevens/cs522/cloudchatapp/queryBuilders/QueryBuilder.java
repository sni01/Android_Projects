package edu.stevens.cs522.cloudchatapp.queryBuilders;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;

/**
 * Created by nisha0634 on 2/21/15.
 */
public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor>{
    /*
     * QueryBuilder variables
     */
    private IEntityCreator<T> helper;
    private IQueryListener<T> listener;
    private static Activity context;
    private static Uri uri;
    private static int loaderID;

    private static String[] projection;
    private static String select;
    private static String[] selectionArgs;

    //private contructor
    private QueryBuilder(Activity context, Uri uri, String[] projection, String select, String[] selectionArgs, int loaderID, IEntityCreator<T> helper, IQueryListener<T> listener){
        this.helper = helper;
        this.listener = listener;
        this.context = context;
        this.uri = uri;
        this.loaderID = loaderID;

        //for cursor loader later
        this.projection = projection;
        this.select = select;
        this.selectionArgs = selectionArgs;
    }

    //query
    public static <T> void executeQuery(Activity context, Uri uri, int loaderID, IEntityCreator<T> helper, IQueryListener<T>listener, String[] projection, String select, String[] selectionArgs){
        QueryBuilder<T> qb = new QueryBuilder<T>(context, uri, projection, select, selectionArgs, loaderID, helper, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
    }

    public static <T> void reexecuteQuery(Activity context, Uri uri, int loaderID, IEntityCreator<T> helper, IQueryListener<T>listener, String[] projection, String select, String[] selectionArgs){
        QueryBuilder<T> qb = new QueryBuilder<T>(context, uri, projection, select, selectionArgs, loaderID, helper, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.restartLoader(loaderID, null, qb);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        if(id == loaderID){
            return new CursorLoader(context, uri, projection, select, selectionArgs, null);
        }
        else{
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        if(loader.getId() == loaderID){
            listener.handleResults(new TypedCursor<T>(cursor, helper));
        }
        else{
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        if(loader.getId() == loaderID){
            listener.closeResults();
        }
        else{
            throw new IllegalStateException("Unexpected loader callback");
        }
    }
}
