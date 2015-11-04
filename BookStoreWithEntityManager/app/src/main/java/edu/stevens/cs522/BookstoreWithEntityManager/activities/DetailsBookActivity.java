package edu.stevens.cs522.BookstoreWithEntityManager.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.BookstoreWithEntityManager.entities.Book;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IEntityCreator;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.ISimpleQueryListener;
import edu.stevens.cs522.BookstoreWithEntityManager.managers.BookManager;
import edu.stevens.cs522.bookstore.R;


public class DetailsBookActivity extends Activity{

    private Book book;

    //manager
    private BookManager<Book> manager;

    private static final int FETCH_DETAILS = 0;

    //variable for result
    public static final String FETCH_RESULT = "fetch result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_book);
        //construct where clause
        Intent intent = getIntent();
        long rowId = intent.getLongExtra(BookStoreActivity.BOOKSDETAILS, -1);

        //fire off request
        manager = new BookManager<Book>(this, helper, FETCH_DETAILS);
        manager.BookExecuteSimpleQuery(Contract.CONTENT_URI(rowId + ""), null, null, null, listener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.details_cancel){
            setResult(RESULT_CANCELED, null);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    IEntityCreator<Book> helper = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    ISimpleQueryListener<Book> listener = new ISimpleQueryListener<Book>() {
        @Override
        public void handleResults(List<Book> results) {
            if(results.size() > 1 || results.size() <= 0){
                throw new IllegalStateException("Unexpected SQL response");
            }
            else{
                System.out.println("fetch book finished");
                Book book = results.get(0);
                TextView title = (TextView)findViewById(R.id.detail_title);
                TextView authors = (TextView)findViewById(R.id.detail_authors);
                TextView isbn = (TextView)findViewById(R.id.detail_isbn);
                TextView price = (TextView)findViewById(R.id.detail_price);
                title.setText("Title: " + book.title);
                authors.setText("Authors: " + book.author_names);
                isbn.setText("ISBN: " + book.isbn);
                price.setText("Price: " + book.price);
            }
        }
    };
}
