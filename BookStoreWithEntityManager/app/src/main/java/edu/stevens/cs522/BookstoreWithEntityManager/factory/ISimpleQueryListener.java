package edu.stevens.cs522.BookstoreWithEntityManager.factory;

import java.util.List;

/**
 * Created by nisha0634 on 2/21/15.
 */
public interface ISimpleQueryListener<T> {
    public void handleResults(List<T> results);
}
