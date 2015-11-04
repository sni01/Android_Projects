package edu.stevens.cs522.BookstoreWithEntityManager.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.bookstore.R;

/**
 * Created by nisha0634 on 2/19/15.
 */
public class BookAdapter extends ResourceCursorAdapter {
    protected final static int ROW_LAYOUT = R.layout.simple_list_item_2;

    //constructor
    public BookAdapter(Context context, Cursor cursor){
        super(context, ROW_LAYOUT, cursor, 0);
    }

    //override functions
    public View newView(Context context,Cursor cursor,ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor){
        //to update UI
        TextView titleLine = (TextView)view.findViewById(R.id.book_title);
        TextView authorLine = (TextView)view.findViewById(R.id.book_author);
        titleLine.setText(Contract.getTitle(cursor));
        authorLine.setText(Contract.getAuthorNames(cursor));
    }

}
