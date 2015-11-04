package edu.stevens.cs522.cloudchatapp.managers;

/**
 * Created by nisha0634 on 3/12/15.
 */

import android.database.Cursor;

import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;

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

    public T getEntity(){
        return helper.create(cursor);
    }
}

