package edu.stevens.cs522.cloudchatapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import edu.stevens.cs522.cloudchatapp.Entities.Sender;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.activities.ActiveClientActivity;
import edu.stevens.cs522.cloudchatapp.adapters.ActiveClientAdapter;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.factory.IReceiver;
import edu.stevens.cs522.cloudchatapp.managers.ClientManager;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;
import edu.stevens.cs522.cloudchatapp.receivers.ServiceReceiver;
import edu.stevens.cs522.cloudchatapp.services.FetchAddressIntentService;

/**
* Created by nisha0634 on 4/26/15.
*/
public class ActiveClientFragment extends Fragment implements IReceiver{
    //variables between chatRoomList and client activity
    private ListView activeClientList;
    private TypedCursor<Sender> typedCursor;
    private ActiveClientAdapter activeClientAdapter;
    private Sender sender; //chat room list define, and pass to message list

    //message manager variables
    private static ClientManager clientManager;
    private static final int loadID = 1;

    //fetch address service
    private static ServiceReceiver fetchAddressReceiver;

    public void onAttach (Activity activity){
        super.onAttach(activity);
    }

    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    //life cycle functions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //get layout for this fragment, it is a layout with a listView
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.active_client_list_layout, container);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //set service receiver
        fetchAddressReceiver = new ServiceReceiver(new Handler());
        fetchAddressReceiver.setReceiver(this);

        //get chatRoomList
        activeClientList = (ListView)getActivity().findViewById(R.id.active_client_listView);

        //set click listener
        this.activeClientList.setOnItemClickListener(clickListener);

        //apply adapter to list
        typedCursor = new TypedCursor<Sender>(null, null);
        activeClientAdapter = new ActiveClientAdapter(getActivity(), typedCursor.getCursor());
        activeClientList.setAdapter(activeClientAdapter);

        //start cursor loader
        clientManager = new ClientManager(getActivity(), helper, loadID);
        clientManager.MessageReexecuteQuery(MessageContract.CONTENT_ACTIVE_CLIENTS_URI(), null, null, null, cursorLoaderListener);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        fetchAddressReceiver.setReceiver(null);
    }

    public void ExecuteReQueryAfterFetchingAddresses(){
        clientManager.MessageReexecuteQuery(MessageContract.CONTENT_ACTIVE_CLIENTS_URI(), null, null, null, fetchCursorLoaderListener);
    }



    /*
     * helper for chat room
     */
    //IEntityCreator helper for IQueryBuilder
    IEntityCreator<Sender> helper = new IEntityCreator<Sender>() {
        @Override
        public Sender create(Cursor cursor) {
            return new Sender(cursor);
        }
    };

    /*
     * cursor loader for chat room list
     */
    /*
     * all callbacks: cursor loader QueryListener, insertListener
     *                service receiver callbacks
     */
    IQueryListener<Sender> cursorLoaderListener = new IQueryListener<Sender>() {
        @Override
        public void handleResults(TypedCursor<Sender> results) {
            typedCursor = results;
            activeClientAdapter.swapCursor(typedCursor.getCursor());
            activeClientAdapter.notifyDataSetChanged();
            Log.i("CURSOR LOADER CALLBACK; ", "CALLBACK HAPPENED");

            //start background service to fetch addresses
            fetchAddresses();
        }

        @Override
        public void closeResults() {

        }
    };

    IQueryListener<Sender> fetchCursorLoaderListener = new IQueryListener<Sender>() {
        @Override
        public void handleResults(TypedCursor<Sender> results) {
            typedCursor = results;
            activeClientAdapter.swapCursor(typedCursor.getCursor());
            activeClientAdapter.notifyDataSetChanged();
            Log.i("CURSOR LOADER CALLBACK; ", "CALLBACK HAPPENED");
        }

        @Override
        public void closeResults() {

        }
    };


    /*
     * for chat room list fragment call to set chatroomlist with listener
     */
    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //get chat room name
            if(typedCursor.getCursor() != null){
                int pos = position;
                typedCursor.moveToFirst();
                while(pos > 0){
                    typedCursor.moveToNext();
                    pos--;
                }
                sender = new Sender(typedCursor.getCursor());

                //call parent add message fragment method
                ((ActiveClientActivity)getActivity()).addFragment(sender);
            }
            else{
                Log.i("ChatRoomListFragment : ", "NO CURSOR EXISTS!");
            }
        }
    };

    /*
     * fetch addresses
     */
    private void fetchAddresses(){
        typedCursor.moveToFirst();
        ArrayList<Sender> senders = new ArrayList<>();
        while(!typedCursor.isAfterLast()){
            Sender sender = new Sender(typedCursor.getCursor());
            senders.add(sender);
            typedCursor.moveToNext();
        }
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putParcelableArrayListExtra(FetchAddressIntentService.PARCELABLE_SENDER_LIST, senders);
        intent.putExtra(ServiceReceiver.FETCH_ADDRESSES_SERVICE_RECEIVER, fetchAddressReceiver);
        getActivity().startService(intent);
    }

    /*
     * fetch addresses service call back
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle result) {
        //updated already in backend, just re-query
        ExecuteReQueryAfterFetchingAddresses();
    }
}
