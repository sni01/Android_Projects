package edu.stevens.cs522.BookstoreWithEntityManager.managers;

import android.database.Cursor;

import edu.stevens.cs522.BookstoreWithEntityManager.factory.IEntityCreator;

/**
 * Created by nisha0634 on 2/21/15.
 */
/*
 * provide save api to access cursor
 */
public class TypedCursor<T> {
    public Cursor cursor;
    public IEntityCreator<T> helper;
    public TypedCursor(Cursor cursor, IEntityCreator<T> helper){
        this.cursor = cursor;
        this.helper = helper;
    }

    public Cursor getCursor(){
        return cursor;
    }

    public void moveToFirst() { cursor.moveToFirst(); }

    public void moveToNext() { cursor.moveToNext(); }

    public boolean isAfterLast() { return cursor.isAfterLast(); }

    public int getCount(){
        return cursor.getCount();
    }
}
