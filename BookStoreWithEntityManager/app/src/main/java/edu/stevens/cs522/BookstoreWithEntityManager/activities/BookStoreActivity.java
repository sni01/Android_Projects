package edu.stevens.cs522.BookstoreWithEntityManager.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import edu.stevens.cs522.BookstoreWithEntityManager.adapters.BookAdapter;
import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.BookstoreWithEntityManager.entities.Book;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IEntityCreator;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IQueryListener;
import edu.stevens.cs522.BookstoreWithEntityManager.managers.BookManager;
import edu.stevens.cs522.BookstoreWithEntityManager.managers.TypedCursor;
import edu.stevens.cs522.bookstore.R;

public class BookStoreActivity extends Activity{
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = BookStoreActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    static final private int DETAIL_REQUEST = ADD_REQUEST + 2;

	// There is a reason this must be an ArrayList instead of a List.
	@SuppressWarnings("unused")

    static final private String SHOPPINGCART_STATE_KEY = "Shopping Cart State";

    static final public String BOOKSDETAILS = "books details";

    //database adapter
    //private static Cursor cursor;
    private static TypedCursor<Book> typedCursor;
    private static ListView listView;

    //Action Mode
    private static ActionMode mode;
    private boolean[] selectedState;


    //Loader ids for Loader callbacks
    public static final int BOOKS_LOADER_ID = 1;

    //Content Resolver
    public static LoaderManager loaderManager;
    //private static AsyncContentResolver asyncContentResolver;
    private BookManager<Book> bookManager;

    //adapter
    private BookAdapter bookAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)

		// TODO Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

		// TODO use an array adapter to display the cart contents.
        // set listeners for cart_list
        listView = (ListView)findViewById(R.id.cart_list);
        listView.setOnItemLongClickListener(longListener);
        listView.setOnItemClickListener(clickListener);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);

        //set adapter
        typedCursor = new TypedCursor<Book>(null, null);
        bookAdapter = new BookAdapter(this, typedCursor.getCursor());
        listView.setAdapter(bookAdapter);

        //init book manager
        bookManager = new BookManager<Book>(this, bookIEntityCreator, BOOKS_LOADER_ID);
        bookManager.BookExecuteQuery(Contract.CONTENT_URI(), null, null, null, queryListener);

        //asyncContentResolver = new AsyncContentResolver(this.getContentResolver()); //initial content resolver
        loaderManager = getLoaderManager();
	}

    OnItemLongClickListener longListener = new OnItemLongClickListener(){
        public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id){
            System.out.println("long click");
            if(mode != null){
                return false;
            }
            mode = startActionMode(multiChoiceModeListener);
            selectedState[pos] = true;
            return true;
        }
    };

    AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            System.out.println("state changed" + i);
            if(i >= selectedState.length || i < 0) return;
            if(b){
                selectedState[i] = true;
            }
            else{
                selectedState[i] = false;
            }
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()){
                case R.id.cab_delete:
                    System.out.println(selectedState.length);
                    typedCursor.moveToFirst();
                    int list_pos = 0;
                    String where = "";
                    while(!typedCursor.isAfterLast() && list_pos < selectedState.length){
                        if(selectedState[list_pos]){
                            long row_id = Contract.getBookId(typedCursor.getCursor());
                            where = where + row_id + "%";
                        }
                        list_pos++;
                        typedCursor.moveToNext();
                    }
                    if(where != null && where.length() > 0){
                        where = where.substring(0, where.length()-1);
                        System.out.println(where);
                        //use bookManager to send delete request
                        bookManager.BookExecuteDelete(Contract.CONTENT_URI(), where, null, AsyncDeleteCallback);
                        //asyncContentResolver.deleteAsync(DELETE_BOOK, AsyncDeleteCallback, Contract.CONTENT_URI(), where, null);
                    }
                    return true;
                case R.id.cab_cancel:
                    return true;
            }
            return false;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.contextual_action_bar_menu, menu);
            listView.setOnItemClickListener(null);
            listView.setOnItemLongClickListener(null);
            selectedState = new boolean[typedCursor.getCount()]; //have boolean array to store selected states
            return true;
        }

        public boolean onPrepareActionMode(ActionMode m, Menu u){
            return false;
        }

        public void onDestroyActionMode(ActionMode mode){
            BookStoreActivity.this.mode = null;
            listView.setOnItemClickListener(clickListener);
            listView.setOnItemLongClickListener(longListener);
        }
    };


    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent detailsIntent = new Intent(BookStoreActivity.this, DetailsBookActivity.class);
            int pos = i;
            typedCursor.moveToFirst();
            while(pos > 0){
                typedCursor.moveToNext();
                pos--;
            }
            long rowId = Contract.getBookId(typedCursor.getCursor());
            detailsIntent.putExtra(BOOKSDETAILS, rowId);
            startActivityForResult(detailsIntent, DETAIL_REQUEST);
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide ADD, DELETE and CHECKOUT options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		switch (item.getItemId()){
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                return true;
            case R.id.details:
                return true;
            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                return true;
        }
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.
        // Use SEARCH_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch (requestCode){
            case (ADD_REQUEST):
                // SEARCH: add the book that is returned to the shopping cart.
                if(resultCode == Activity.RESULT_OK){
                    bookManager.BookReexecuteQuery(Contract.CONTENT_URI(), null, null, null, queryListener);
                    System.out.println("add successfully");
                }
                else if(resultCode == Activity.RESULT_CANCELED){
                    System.out.println("no book selected this time");
                }
                break;
            case (CHECKOUT_REQUEST):
                // CHECKOUT: empty the shopping cart.
                if(resultCode == Activity.RESULT_OK){
                    bookManager.BookReexecuteQuery(Contract.CONTENT_URI(), null, null, null, queryListener);
                    System.out.println("checkout successfully");
                }
                else if(resultCode == Activity.RESULT_CANCELED){
                    System.out.println("haven't check out this time");
                }
                break;
            case(DETAIL_REQUEST):
                if(resultCode == Activity.RESULT_CANCELED){
                    System.out.println("cancel successfully");
                }
                break;
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
        super.onSaveInstanceState(savedInstanceState);
    }

    //call back for deletion and cursor loader listener
    IContinue<Integer> AsyncDeleteCallback = new IContinue<Integer>() {
        @Override
        public void kontinue(Integer value) {
            if(value == 1){
                //update UI
                bookManager.BookReexecuteQuery(Contract.CONTENT_URI(), null, null, null, queryListener);
                Log.i("WARNING: ", "deletion completed");
            }
            else{
                Log.i("WARNING: ", "no deletion happened");
            }
        }
    };

    //call back listener
    IEntityCreator <Book> bookIEntityCreator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    //listeners
    IQueryListener<Book> queryListener = new IQueryListener<Book>() {
        @Override
        public void handleResults(TypedCursor<Book> results) {
            typedCursor = results;
            renderAllBooks();
        }

        @Override
        public void closeResults() {

        }
    };

    public void renderAllBooks(){
        bookAdapter.swapCursor(typedCursor.getCursor());
        bookAdapter.notifyDataSetChanged();
    }

}