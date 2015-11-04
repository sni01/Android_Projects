package edu.stevens.cs522.BookstoreWithEntityManager.queryBuilder;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.LinkedList;
import java.util.List;

import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IEntityCreator;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.ISimpleQueryListener;
import edu.stevens.cs522.BookstoreWithEntityManager.resolvers.AsyncContentResolver;

/**
 * Created by nisha0634 on 2/21/15.
 */
public class SimpleQueryBuilder<T> implements IContinue<Cursor>{
    //wrap query and link between activity and manager
    private IEntityCreator<T> helper;
    private ISimpleQueryListener<T> listener;
    private static AsyncContentResolver asyncResolver;
    private static int loaderID;


    private SimpleQueryBuilder(Activity context, int loaderID, IEntityCreator<T> helper, ISimpleQueryListener<T> listener){
        this.loaderID = loaderID;
        this.helper = helper;
        this.listener = listener;
        asyncResolver = new AsyncContentResolver(context.getContentResolver());
    }

    //query, why this cannot be static function?

    public static <T> void executeQuery(Activity context, Uri uri, int loaderID, String[] projection, String selection, String[] selectionArgs, IEntityCreator<T> helper, ISimpleQueryListener<T> listener){
        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(context, loaderID, helper, listener);
        asyncResolver.queryAsync(loaderID, uri, projection, selection, selectionArgs, null, qb);
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
