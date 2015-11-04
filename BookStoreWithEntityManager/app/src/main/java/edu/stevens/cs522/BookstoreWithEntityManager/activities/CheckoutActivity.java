package edu.stevens.cs522.BookstoreWithEntityManager.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.BookstoreWithEntityManager.factory.IContinue;
import edu.stevens.cs522.BookstoreWithEntityManager.managers.BookManager;
import edu.stevens.cs522.bookstore.R;

public class CheckoutActivity extends Activity {

    static final private String CHECKOUT_STATE = "checked out";

    //BookManager here for async requests
    private BookManager bookManager;

    //async call id
    private static final int DELETE_ALL_BOOKS = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);

        //initial BookManager here, no IEntityCreator object here
        bookManager = new BookManager(this, null, DELETE_ALL_BOOKS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO display ORDER and CANCEL options.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkout_mean,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
        switch (item.getItemId()) {
            case R.id.checkout:
                // ORDER: display a toast message of how many books have been ordered and return
                clearBookChart();
                return true;
            case R.id.cancel:
                // CANCEL: just return with REQUEST_CANCELED as the result code
                setResult(RESULT_CANCELED, null);
                finish();
                return true;
        }
		return false;
	}

    public void clearBookChart(){
        //send async delete request
        bookManager.BookExecuteDelete(Contract.CONTENT_URI(), null, null, AsyncDeleteCallBack);
    }

    IContinue<Integer> AsyncDeleteCallBack = new IContinue<Integer>() {
        @Override
        public void kontinue(Integer value) {
            if(value == 1){
                System.out.println("deletion happened");
            }
            else{
                System.out.println("no deletion happened");
            }
            setResult(RESULT_OK, null);
            finish();
        }
    };
	
}