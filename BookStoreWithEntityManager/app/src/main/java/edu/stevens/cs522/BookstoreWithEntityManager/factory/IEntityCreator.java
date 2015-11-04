package edu.stevens.cs522.BookstoreWithEntityManager.factory;

import android.database.Cursor;

/**
 * Created by nisha0634 on 2/21/15.
 */
public interface IEntityCreator<T> {
    public T create(Cursor cursor);
}
