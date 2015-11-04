package edu.stevens.cs522.cloudchatapp.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.List;

import edu.stevens.cs522.cloudchatapp.Entities.ChatRoom;
import edu.stevens.cs522.cloudchatapp.Entities.Message;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.adapters.MessageAdapter;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.factory.IContinue;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IReceiver;
import edu.stevens.cs522.cloudchatapp.factory.ISendMessageListener;
import edu.stevens.cs522.cloudchatapp.factory.ISetChatRoomListener;
import edu.stevens.cs522.cloudchatapp.factory.ISimpleQueryListener;
import edu.stevens.cs522.cloudchatapp.fragments.ChatRoomListFragment;
import edu.stevens.cs522.cloudchatapp.fragments.CreateChatRoomDialog;
import edu.stevens.cs522.cloudchatapp.fragments.MessageListFragment;
import edu.stevens.cs522.cloudchatapp.fragments.SendMessageDialog;
import edu.stevens.cs522.cloudchatapp.managers.ChatRoomManager;
import edu.stevens.cs522.cloudchatapp.managers.MessageManager;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;
import edu.stevens.cs522.cloudchatapp.receivers.ServiceReceiver;
import edu.stevens.cs522.cloudchatapp.requests.RequestProcessor;
import edu.stevens.cs522.cloudchatapp.services.ServiceHelper;

//import android.os.Message;

/**
 * Created by nisha0634 on 3/11/15.
 */
public class Client extends Activity implements ISendMessageListener, ISetChatRoomListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    //specify queries ids, for insertion and updates
    private static final int insertId = 1;
    private static final int queryId = 2;

    //cursor adapter and listview variables
    private static MessageAdapter messageAdapter;
    private static TypedCursor<Message> typedCursor;
    private static final int loadID = 1;

    //message manager variables
    private static MessageManager messageManager;
    private static ChatRoomManager chatRoomManager;

    //request service receiver
    private static ServiceReceiver registerReceiver;
    private static ServiceReceiver sendMessageReceiver;
    private static ServiceReceiver syncReceiver;

    //preference
    SharedPreferences preferences;
    private static String serverAddress = "default address";
    private static ServiceHelper serviceHelper;

    //alarm parameters
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    //fragment manager variables for dynamically insert or delete fragments
    private FragmentManager fragmentManager;

    //get message from dialog
    private static String content;
    private static String newRoomName;


    //location variables
    private static final int REQUEST_CODE_RESOLVE_ERR = 1;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private boolean mRequestingLocationUpdates; //track if location request is turned on now.

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        setContentView(R.layout.client_layout);

        //all screen staff, including fragments, manager variables, activity variables, etc.
        initScreen();

        //location staff
        initLocation();
    }


    @Override
    public void onStart(){
        //connect google apiClient
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver.setReceiver(registerListener);
        sendMessageReceiver.setReceiver(sendMessageListener);
        syncReceiver.setReceiver(syncDeviceListener);

        //location service
        if(googleApiClient.isConnected() && !mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        //clear the fragmentManager when rotates the screen.
        fragmentManager.popBackStack();

        //clear preference chat room value and set back to default room
        preferences.edit().putString(PreferenceActivity.CHAT_ROOM_NAME, "_DEFAULT").commit();


        //stop location service when activity paused
        if(googleApiClient.isConnected()){
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop(){
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        registerReceiver.setReceiver(null);
        sendMessageReceiver.setReceiver(null);
        syncReceiver.setReceiver(null);
    }

    /*
     * item menu to navigate through activities
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO provide ADD, DELETE and CHECKOUT options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.client_options_menu, menu);
        return true;
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
            case R.id.client_registration_service:
                //register to server and start alarm
                register();
                return true;
            case R.id.client_send_service:
                SendMessageDialog sendMessageDialog = new SendMessageDialog();
                sendMessageDialog.launch(this, SendMessageDialog.TAG);
                return true;
            case R.id.client_setup_chatRoom:
                CreateChatRoomDialog createChatRoomDialog = new CreateChatRoomDialog();
                createChatRoomDialog.launch(this, CreateChatRoomDialog.TAG);
                return true;
            case android.R.id.home: //back button here
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

                    //just pop once to last fragment state
                    fragmentManager.popBackStack();

                    //disable back button if no more fragment exists.
                    MessageListFragment currentFragment = (MessageListFragment)fragmentManager.findFragmentById(R.id.chatRoomMessages);
                    if(currentFragment == null){
                        getActionBar().setDisplayHomeAsUpEnabled(false);
                    }

                    //clear preference chat room value and set back to default room
                    preferences.edit().putString(PreferenceActivity.CHAT_ROOM_NAME, "_DEFAULT").commit();

                    //restart chat room cursor loader, otherwise navigation panel will lost its cursor.
                    refreshChatRoomFragment();
                }

                return true;

        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            googleApiClient.connect();
        }
        else{
            Log.i("CLIENT :", "NOT SOLVED");
        }
    }


    //called by onCreate()
    private void initScreen(){
        //set preference
        preferences = getSharedPreferences(PreferenceActivity.MY_PREFS, Activity.MODE_PRIVATE);

        //set fragment variables
        fragmentManager = getFragmentManager();


        //initial message manager and send out cursor query
        messageManager = new MessageManager(this, messageHelper, loadID);
        chatRoomManager = new ChatRoomManager(this, chatRoomHelper, loadID);


        //initial request service receiver
        registerReceiver = new ServiceReceiver(new Handler()); //current thread
        sendMessageReceiver = new ServiceReceiver(new Handler());
        syncReceiver = new ServiceReceiver(new Handler());


        //set server address for all requests to use
        serverAddress = preferences.getString(PreferenceActivity.SERVER_ADDRESS, "DEFAULT ADDRESS"); //because this is the main activity, this will firstly go to default.
        serviceHelper = new ServiceHelper(this, null); //do not set receiver now;
        Log.i("ADDRESS oncreate", serverAddress);
    }

    //called by onCreate()
    private void initLocation(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //.addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mRequestingLocationUpdates = false;
    }

    /*
     * public register and send message calls
     */
    public void register(){
        Log.i("CLIENT ACTIVITY: ", "START REGISTER");

        serviceHelper.setReceiver(registerReceiver);
        serviceHelper.register();
    }

    public void sendMessage(String content){
        Log.i("CLIENT ACTIVITY: ", "START SENDING MESSAGE");
        //insert chat room and get chatRoomFK for message
        this.content = content;
        String chatRoomName = preferences.getString(PreferenceActivity.CHAT_ROOM_NAME, "_DEFAULT");
        ChatRoom chatRoom = new ChatRoom(chatRoomName);
        ContentValues values = new ContentValues();
        chatRoom.writeToProvider(values);
        chatRoomManager.ChatRoomExecuteInsert(MessageContract.CONTENT_ALL_CHAT_ROOM_URI(), values, chatRoomInsertCallback, insertId);
    }

    /*
     * called by fragments
     */
    public void addFragment(ChatRoom chatRoom){

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //then add new fragment to stack
            FragmentTransaction ft = fragmentManager.beginTransaction();
            MessageListFragment messageListFragment = new MessageListFragment();

            //check if any messageListFragment exist in fragmentLayout yet.
            MessageListFragment currentFragment = (MessageListFragment)fragmentManager.findFragmentById(R.id.chatRoomMessages);
            if(currentFragment == null){
                //no exist any messageListFragment
                //ft.replace(R.id.chatRoomMessages, messageListFragment, "messageListFragment");
                ft.add(R.id.chatRoomMessages, messageListFragment, MessageListFragment.TAG);
            }
            else{
                //exist a messageListFragment
                ft.remove(currentFragment);
                ft.add(R.id.chatRoomMessages, messageListFragment, MessageListFragment.TAG);
                //ft.replace(R.id.chatRoomMessages, messageListFragment, "messageListFragment");
            }


            //add this transaction to stack for "back" button to use.
            ft.addToBackStack(null); //append to the top of stack
            ft.commit();

            //set messageListFragment chatRoom info
            preferences.edit().putString(PreferenceActivity.CHAT_ROOM_NAME, chatRoom.chatRoomName).commit();

            //when list exist, set home button to true
            getActionBar().setDisplayHomeAsUpEnabled(true); //set a back button
        }
        else{
            //portrait, start another activity to show details
            Intent intent = new Intent(this, ChatRoomMessageActivity.class);
            intent.putExtra(PreferenceActivity.CHAT_ROOM_NAME, chatRoom.chatRoomName);
            startActivity(intent);
        }
    }

    public String getChatRoomName(){ return preferences.getString(PreferenceActivity.CHAT_ROOM_NAME, "_DEFAULT"); }

    /*
     * refresh UI
     */
    public void refreshChatRoomFragment(){
        ChatRoomListFragment cf = (ChatRoomListFragment)fragmentManager.findFragmentById(R.id.client_chat_room_list_fragment);
        cf.EnableClick();
        cf.ExecuteReQuery();
    }

    public void refreshMessageListFragment(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            MessageListFragment mf = (MessageListFragment)fragmentManager.findFragmentByTag("messageListFragment");
            mf.ExecuteReQuery();
        }
    }


    /*
     * interfaces for dialogs
     */
    @Override
    public void acknowledge(String content) {
        sendMessage(content);
    }

    @Override
    public void setChatRoom(String newRoomName){
        //check if the room exist
        this.newRoomName = newRoomName;
        String where = newRoomName;

        //fetch this chatRoom, see if it exists
        chatRoomManager.ChatRoomExecuteSimpleQuery(MessageContract.CONTENT_FIND_CHAT_ROOM_URI(), null, where, null, chatRoomISimpleQueryListener, queryId);
    }

    /*
     * interfaces for google client api
     * location services
     */
    @Override
    public void onConnected(Bundle connectionHint){
        if(!mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int cause){
        Log.i("CLINET ACTIVITY :", "SUSPENDED " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        Log.i("CLIENT ACTIVITY :", result.toString());
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            }
            catch (SendIntentException e){
                Log.i("CLIENT :", e.getMessage());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location){
        //logic to update client preference about location's latitude and longitude.
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceActivity.LATITUDE, String.valueOf(location.getLatitude()));
        editor.putString(PreferenceActivity.LONGITUDE, String.valueOf(location.getLongitude()));
        editor.commit();
        Log.i("CLIENT LOCATION: ", "receive location data!");
    }


    //methods for interface to call
    protected void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates(){
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        mRequestingLocationUpdates = true;
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        mRequestingLocationUpdates = false;
    }



    //insert callback
    IContinue<Uri> chatRoomInsertCallback = new IContinue<Uri>() {
        @Override
        public void kontinue(Uri value) {
            //get message info
            String clientUsername = preferences.getString(PreferenceActivity.CLIENT_NAME, "DEFAULT NAME");
            String messageContent = content;
            long timeStamp = (long)new Date().getTime();
            String longitude = preferences.getString(PreferenceActivity.LONGITUDE, "0");
            String latitude = preferences.getString(PreferenceActivity.LATITUDE, "0");


            String lastSeg = MessageContract.getId(value);
            long chatRoomId = Long.parseLong(lastSeg);
            Message insertedMessage = new Message(messageContent, 0, timeStamp, clientUsername, latitude, longitude, chatRoomId);

            ContentValues values = new ContentValues();
            insertedMessage.writeToProvider(values);
            messageManager.MessageExecuteInsert(MessageContract.CONTENT_MESSAGE_URI(), values, insertCallback, insertId);
        }
    };

    IContinue<Uri> insertCallback = new IContinue<Uri>() {
        @Override
        public void kontinue(Uri value) {
            Log.i("INSERTION: ", "HAPPENED SUCCESSFULLY!");
            serviceHelper.setReceiver(syncReceiver);
            serviceHelper.synchronize();
        }
    };


    //service receiver callback
    IReceiver registerListener = new IReceiver() {
        @Override
        public void onReceiveResult(int resultCode, Bundle result) {
            //get sequence number or client id, here is for updating this information and may be requery to refresh UI
            String idStr = result.getString(RequestProcessor.CLIENT_ID);
            Log.i("CLIENT :", idStr);
            //message manager to call update async request and update UI, update preference
            long id = Long.parseLong(idStr);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(PreferenceActivity.CLIENT_ID, id);
            editor.commit();

            String clientName = preferences.getString(PreferenceActivity.CLIENT_NAME, "DEFAULT_NAME");
            Toast.makeText(Client.this, clientName, Toast.LENGTH_SHORT).show();


            //here we sync local with remote server, fire off synchronize function when register.
            serviceHelper.setReceiver(syncReceiver);
            serviceHelper.synchronize();
        }
    };

    IReceiver sendMessageListener = new IReceiver() {
        @Override
        public void onReceiveResult(int resultCode, Bundle result) {
            serviceHelper.setReceiver(syncReceiver);
            serviceHelper.synchronize();
        }
    };

    IReceiver syncDeviceListener = new IReceiver() {
        @Override
        public void onReceiveResult(int resultCode, Bundle result) {
            //sync receiver handle two things: update most recently sequence number and update UI
            long most_sequence_number = result.getLong(PreferenceActivity.SEQUENCE_NUMBER);
            if(most_sequence_number >= 0){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(PreferenceActivity.SEQUENCE_NUMBER, most_sequence_number);
                editor.commit();
                //messageManager.MessageReexecuteQuery(MessageContract.CONTENT_MESSAGES_URI(), null, null, null, cursorLoaderListener);
                //refresh chat room list and message list fragment
                refreshChatRoomFragment();
                refreshMessageListFragment();
            }

            //once the registration has succeeded then set alarmManager to periodically pull messages from server.
            //register finished and immediate syncRequest finished, then set alarm, if it hasn't been set up before.
            if(alarmMgr == null){
                setAlarm();
            }
        }
    };

    /*
     * Simple Query Listener
     */
    ISimpleQueryListener<ChatRoom> chatRoomISimpleQueryListener = new ISimpleQueryListener<ChatRoom>() {
        @Override
        public void handleResults(List<ChatRoom> results) {
            if(results.size() > 0){
                Toast.makeText(Client.this, "CHAT ROOM EXISTS", Toast.LENGTH_LONG).show();
            }
            else{
                ContentValues values = new ContentValues();
                ChatRoom chatRoom = new ChatRoom(newRoomName);
                chatRoom.writeToProvider(values);
                chatRoomManager.ChatRoomExecuteInsert(MessageContract.CONTENT_ALL_CHAT_ROOM_URI(), values, newChatRoomInsertCallback, insertId);
            }
        }
    };

    IContinue<Uri> newChatRoomInsertCallback = new IContinue<Uri>() {
        @Override
        public void kontinue(Uri value) {
            refreshChatRoomFragment();
        }
    };


    /*
     * helper
     */
    //IEntityCreator helper for IQueryBuilder
    IEntityCreator<Message> messageHelper = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };

    IEntityCreator<ChatRoom> chatRoomHelper = new IEntityCreator<ChatRoom>() {
        @Override
        public ChatRoom create(Cursor cursor) {
            return new ChatRoom(cursor);
        }
    };

    /*
     * alarm settings
     */

    public void setAlarm(){

        Intent intent = new Intent(this, AlarmReceiver.class);

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //set alarm here
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //start alarm
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, alarmIntent);
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        public AlarmReceiver(){
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast here
            if(serviceHelper != null){
                serviceHelper.setReceiver(syncReceiver);
                serviceHelper.synchronize();
            }
        }
    }

}
