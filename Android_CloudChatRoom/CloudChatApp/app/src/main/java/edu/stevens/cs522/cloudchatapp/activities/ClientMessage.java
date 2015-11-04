package edu.stevens.cs522.cloudchatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import edu.stevens.cs522.cloudchatapp.Entities.Message;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.adapters.MessageAdapter;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.managers.MessageManager;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;

public class ClientMessage extends Activity {

    private static MessageManager messageManager;

    private static ListView listView;

    private static MessageAdapter messageAdapter;

    private static TypedCursor<Message> typedCursor;

    private static final int loadID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_message);

        //get intent info here
        Intent intent = getIntent();
        String username = intent.getStringExtra(ActiveClientActivity.USERNAME);
        //set where clause here
        String where = username;

        //set listView and adapter
        listView = (ListView)findViewById(R.id.client_message_listView);

        //bind cursor adapter for message list view
        typedCursor = new TypedCursor<Message>(null, null);
        messageAdapter = new MessageAdapter(this, typedCursor.getCursor());
        listView.setAdapter(messageAdapter);

        //initial message manager and send out cursor query
        messageManager = new MessageManager(this, helper, loadID);
        messageManager.MessageExecuteQuery(MessageContract.CONTENT_MULTIPLE_MESSAGES_SENDER_URI(), null, where, null, cursorLoaderListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.active_client_activity) {
            Intent intent = new Intent(this, ActiveClientActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * cursor loader callback
     */
    IQueryListener<Message> cursorLoaderListener = new IQueryListener<Message>() {
        @Override
        public void handleResults(TypedCursor<Message> results) {
            typedCursor = results;
            messageAdapter.swapCursor(typedCursor.getCursor());
            messageAdapter.notifyDataSetChanged();
            Log.i("ClientMessage: ", "cursor load call back!");
            return;
        }

        @Override
        public void closeResults() {

        }
    };

    IEntityCreator<Message> helper = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };
}
