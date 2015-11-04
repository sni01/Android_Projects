package edu.stevens.cs522.cloudchatapp.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.stevens.cs522.cloudchatapp.Entities.Sender;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.adapters.ActiveClientAdapter;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.fragments.ClientMessageFragment;
import edu.stevens.cs522.cloudchatapp.managers.ClientManager;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;

public class ActiveClientActivity extends Activity{

    private static ActiveClientAdapter activeClientAdapter;

    private static ListView client_listView;

    private static TypedCursor<Sender> typedCursor;

    private static ClientManager clientManager; //we use messageManager to get access to client information

    private static final int loadID = 1;

    public static final String USERNAME = "client username";

    private static FragmentManager fragmentManager;

    private String senderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_client);

        fragmentManager = getFragmentManager();

        clientManager = new ClientManager(this, helper, loadID);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_active_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.client_activity) {
            Intent intent = new Intent(this, Client.class);
            startActivity(intent);
            return true;
        }
        else if(id == android.R.id.home){
            fragmentManager.popBackStack();

            this.senderName = "_DEFAULT";

            getActionBar().setDisplayHomeAsUpEnabled(false);
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * called by fragments
     */
    public void addFragment(Sender sender){

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //then add new fragment to stack
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ClientMessageFragment clientMessageFragment = new ClientMessageFragment();
            ft.replace(R.id.active_client_framelayout, clientMessageFragment, "messageClientFragment");
            ft.addToBackStack(null); //append to the top of stack
            ft.commit();

            //set current sender name
            this.senderName = sender.username;

            //when list exist, set home button to true
            getActionBar().setDisplayHomeAsUpEnabled(true); //set a back button
        }
        else{
            //portrait, start another activity to show details
            Intent intent = new Intent(this, ClientMessage.class);
            intent.putExtra(ActiveClientActivity.USERNAME, sender.username);
            startActivity(intent);
        }
    }

    public String getSenderName(){ return this.senderName; }


    //IEntityCreator helper for IQueryBuilder
    IEntityCreator<Sender> helper = new IEntityCreator<Sender>() {
        @Override
        public Sender create(Cursor cursor) {
            return new Sender(cursor);
        }
    };

    //click listener
    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent detailsIntent = new Intent(ActiveClientActivity.this, ClientMessage.class);
            int pos = i;
            typedCursor.moveToFirst();
            while(pos > 0){
                typedCursor.moveToNext();
                pos--;
            }
            String username = MessageContract.getSenderUsername(typedCursor.getCursor());
            detailsIntent.putExtra(USERNAME, username);
            startActivity(detailsIntent);
        }
    };
}
