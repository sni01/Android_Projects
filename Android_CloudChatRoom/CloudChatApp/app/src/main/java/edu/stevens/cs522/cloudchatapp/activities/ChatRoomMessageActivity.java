package edu.stevens.cs522.cloudchatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Created by nisha0634 on 4/26/15.
 */
public class ChatRoomMessageActivity extends Activity {
    //variables between chatRoomList and client activity
    private static ListView messageListView;
    private static TypedCursor<Message> typedCursor;
    private static MessageAdapter messageAdapter;
    private static String chatRoomName;

    //message manager variables
    private static MessageManager messageManager;
    private static final int loadID = 1;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_list_layout);

        Intent intent = getIntent();
        this.chatRoomName = intent.getStringExtra(PreferenceActivity.CHAT_ROOM_NAME);


        //get chatRoomList
        messageListView = (ListView)findViewById(R.id.client_message_list);

        //apply adapter to list
        typedCursor = new TypedCursor<Message>(null, null);
        messageAdapter = new MessageAdapter(this, typedCursor.getCursor());
        messageListView.setAdapter(messageAdapter);


        //initial message manager and send out cursor query
        messageManager = new MessageManager(this, helper, loadID);
        String where = chatRoomName;
        messageManager.MessageReexecuteQuery(MessageContract.CONTENT_MULTIPLE_MESSAGES_CHATROOM_URI(), null, where, null, cursorLoaderListener);

        //enable back button
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // TODO
        switch (item.getItemId()){
            case R.id.client_to_preference_activity:
                Intent preferenceIntent = new Intent(this, PreferenceActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.client_to_active_client_activity:
                Intent activeClientActivity = new Intent(this, ActiveClientActivity.class);
                startActivity(activeClientActivity);
                return true;
            case android.R.id.home:
                getActionBar().setDisplayHomeAsUpEnabled(false); //close home button before going back to client
                Intent intent = new Intent(this, Client.class);
                startActivity(intent);
                return true;

        }
        return false;
    }



    public void ExecuteReQuery(String where){
        messageManager.MessageReexecuteQuery(MessageContract.CONTENT_MULTIPLE_MESSAGES_CHATROOM_URI(), null, where, null, cursorLoaderListener);
    }


    /*
     * all callbacks: cursor loader QueryListener, insertListener
     *                service receiver callbacks
     */
    IQueryListener<Message> cursorLoaderListener = new IQueryListener<Message>() {
        @Override
        public void handleResults(TypedCursor<Message> results) {
            typedCursor = results;
            messageAdapter.swapCursor(typedCursor.getCursor());
            messageAdapter.notifyDataSetChanged();
            Log.i("CURSOR LOADER CALLBACK; ", "CALLBACK HAPPENED");
        }

        @Override
        public void closeResults() {

        }
    };

    //IEntityCreator helper for IQueryBuilder
    IEntityCreator<Message> helper = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };
}
