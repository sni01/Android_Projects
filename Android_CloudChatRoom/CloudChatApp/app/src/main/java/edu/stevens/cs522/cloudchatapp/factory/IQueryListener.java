package edu.stevens.cs522.cloudchatapp.factory;

import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;

/**
 * Created by nisha0634 on 2/21/15.
 */
/*
 * for cursor loader
 */
public interface IQueryListener<T> {
    public void handleResults(TypedCursor<T> results);
    public void closeResults();
}
