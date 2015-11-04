package edu.stevens.cs522.BookstoreWithEntityManager.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.BookstoreWithEntityManager.entities.Book;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;
import edu.stevens.cs522.BookstoreWithEntityManager.managers.BookManager;
import edu.stevens.cs522.bookstore.R;



/*
* this call content provider add function to insert book and authors at the same time.
*
* */
public class AddBookActivity extends Activity {

    static final public String BOOK_RESULT = "Book Result";
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";

    // variable for results
    public static final String BOOK_ADD_RESULT = "book add result";

    //content provider variables
    //private AsyncContentResolver asyncContentResolver;
    private BookManager bookManager;

    //async call id
    private static final int ADD_BOOK = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set layout
        setContentView(R.layout.add_book);

        //initial book manager, no IEntityCreator object here
        bookManager = new BookManager<Book>(this, null, ADD_BOOK);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide SEARCH and CANCEL options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addbook_menu,menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
        switch (item.getItemId()) {
            case R.id.search:
                // SEARCH: return the book details to the BookStore activity
                persistBook();
                return true;
            case R.id.cancel:
                // CANCEL: cancel the search request
                setResult(RESULT_CANCELED, null);
                finish();
                return true;
        }
		return false;
	}

    public void persistBook(){
        //get EditTexts from UI
        EditText titleEditText = (EditText)findViewById(R.id.search_title);
        EditText authorEditText = (EditText)findViewById(R.id.search_author);
        EditText isbnEditText = (EditText)findViewById(R.id.search_isbn);
        //get String input from EditTextss
        String title_raw = titleEditText.getText().toString();
        String authorNames_raw = authorEditText.getText().toString();
        String isbn_raw = isbnEditText.getText().toString();
        String price_raw = "100";

        //construct content values to send to content provider
        ContentValues values = new ContentValues();
        Contract.putTitle(values, title_raw);
        Contract.putIsbn(values, isbn_raw);
        Contract.putPrice(values, price_raw);
        Contract.putAuthorName(values, authorNames_raw);

        //send async insert request
        bookManager.BookExecuteInsert(Contract.CONTENT_URI(), values, AsyncInsertCallback);

    }

    IContinue<Uri> AsyncInsertCallback = new IContinue<Uri>() {
        @Override
        public void kontinue(Uri value) {
            //recall cursor loader here
            System.out.println("finish adding book, callback");
            Intent intent = new Intent();
            intent.putExtra(BOOK_ADD_RESULT, value);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

}