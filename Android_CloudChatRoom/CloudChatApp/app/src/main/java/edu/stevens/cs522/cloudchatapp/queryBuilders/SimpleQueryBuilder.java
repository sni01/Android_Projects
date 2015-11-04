package edu.stevens.cs522.cloudchatapp.queryBuilders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.LinkedList;
import java.util.List;

import edu.stevens.cs522.cloudchatapp.factory.IContinue;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.ISimpleQueryListener;
import edu.stevens.cs522.cloudchatapp.resolvers.AsyncContentResolver;

/**
 * Created by nisha0634 on 2/21/15.
 */
public class SimpleQueryBuilder<T> implements IContinue<Cursor> {

    /*
     * SimpleQueryBuilder variables
     */
    private IEntityCreator<T> helper;
    private ISimpleQueryListener<T> listener;
    private static AsyncContentResolver asyncResolver;

    //token variables
    private static int query_id;

    //private constructor, only called by itself
    private SimpleQueryBuilder(Context context, IEntityCreator<T> helper, ISimpleQueryListener<T> listener, int queryId){
        this.helper = helper;
        this.listener = listener;
        asyncResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public static <T> void executeQuery(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, IEntityCreator<T> helper, ISimpleQueryListener<T> listener, int query_Id){
        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(context, helper, listener, query_Id);
        asyncResolver.queryAsync(query_id, qb, uri, projection, selection, selectionArgs, null);
    }

    //implements IContinue callback
    @Override
    public void kontinue(Cursor cursor) {
            List<T> instances = new LinkedList<T>();
            if(cursor.moveToFirst()){
                do{
                    T instance = helper.create(cursor);
                    instances.add(instance);
                } while(cursor.moveToNext());
            }
            cursor.close();
            listener.handleResults(instances);
    }

}
