package edu.stevens.cs522.cloudchatapp.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.stevens.cs522.cloudchatapp.Entities.Message;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.activities.Client;
import edu.stevens.cs522.cloudchatapp.adapters.MessageAdapter;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.managers.MessageManager;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;

/**
 * Created by nisha0634 on 4/23/15.
 */
public class MessageListFragment extends Fragment {

    //public tag
    public static String TAG = "message list fragment";

    //variables between chatRoomList and client activity
    private static ListView messageListView;
    private static TypedCursor<Message> typedCursor;
    private static MessageAdapter messageAdapter;
    private static String chatRoomName;

    //message manager variables
    private static MessageManager messageManager;
    private static final int loadID = 2;

    //life cycle functions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //get layout for this fragment, it is a layout with a listView
        View layout = inflater.inflate(R.layout.message_list_layout, container, false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //get chatRoomList
        messageListView = (ListView)getActivity().findViewById(R.id.client_message_list);

        //apply adapter to list
        typedCursor = new TypedCursor<Message>(null, null);
        messageAdapter = new MessageAdapter(getActivity(), typedCursor.getCursor());
        messageListView.setAdapter(messageAdapter);

        //get chatRoomName
        chatRoomName = ((Client)getActivity()).getChatRoomName();

        //initial message manager and send out cursor query
        messageManager = new MessageManager(getActivity(), helper, loadID);
        String where = chatRoomName;

        //must use re-execute here, since activity has only one cursoe loader, chat room list and this use the same one
        messageManager.MessageReexecuteQuery(MessageContract.CONTENT_MULTIPLE_MESSAGES_CHATROOM_URI(), null, where, null, cursorLoaderListener);
    }


    public void ExecuteReQuery(){
        String where = chatRoomName;
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
