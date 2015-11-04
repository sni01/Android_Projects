package edu.stevens.cs522.cloudchatapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.stevens.cs522.cloudchatapp.Entities.ChatRoom;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.activities.Client;
import edu.stevens.cs522.cloudchatapp.adapters.ChatRoomAdapter;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.factory.IEntityCreator;
import edu.stevens.cs522.cloudchatapp.factory.IQueryListener;
import edu.stevens.cs522.cloudchatapp.managers.ChatRoomManager;
import edu.stevens.cs522.cloudchatapp.managers.TypedCursor;

/**
 * Created by nisha0634 on 4/23/15.
 */
public class ChatRoomListFragment extends Fragment {
    //variables between chatRoomList and client activity
    private ListView chatRoomList;
    private TypedCursor<ChatRoom> typedCursor;
    private ChatRoomAdapter chatRoomAdapter;
    private ChatRoom chatRoom; //chat room list define, and pass to message list


    //message manager variables
    private static ChatRoomManager chatRoomManager;
    private static final int loadID = 1;

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
        View layout = inflater.inflate(R.layout.chat_room_list_layout, container);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //get chatRoomList
        chatRoomList = (ListView)getActivity().findViewById(R.id.chat_room_list);

        //set click listener
        this.chatRoomList.setOnItemClickListener(clickListener);

        //apply adapter to list
        typedCursor = new TypedCursor<ChatRoom>(null, null);
        chatRoomAdapter = new ChatRoomAdapter(getActivity(), typedCursor.getCursor());
        chatRoomList.setAdapter(chatRoomAdapter);

        //start cursor loader
        chatRoomManager = new ChatRoomManager(getActivity(), helper, loadID);
        chatRoomManager.ChatRoomExecuteQuery(MessageContract.CONTENT_ALL_CHAT_ROOM_URI(), null, null, null, cursorLoaderListener);

    }

    public void ExecuteReQuery(){
        chatRoomManager.ChatRoomReexecuteQuery(MessageContract.CONTENT_ALL_CHAT_ROOM_URI(), null, null, null, cursorLoaderListener);
    }

    public void DisableClick(){
        //chatRoomList.setEnabled(false);
    }

    public void EnableClick(){
        //chatRoomList.setEnabled(true);
    }



    /*
     * helper for chat room
     */
    //IEntityCreator helper for IQueryBuilder
    IEntityCreator<ChatRoom> helper = new IEntityCreator<ChatRoom>() {
        @Override
        public ChatRoom create(Cursor cursor) {
            return new ChatRoom(cursor);
        }
    };

    /*
     * cursor loader for chat room list
     */
    /*
     * all callbacks: cursor loader QueryListener, insertListener
     *                service receiver callbacks
     */
    IQueryListener<ChatRoom> cursorLoaderListener = new IQueryListener<ChatRoom>() {
        @Override
        public void handleResults(TypedCursor<ChatRoom> results) {
            typedCursor = results;
            chatRoomAdapter.swapCursor(typedCursor.getCursor());
            chatRoomAdapter.notifyDataSetChanged();
            Log.i("CURSOR LOADER CALLBACK; ", "CALLBACK HAPPENED");
        }

        @Override
        public void closeResults() {
            chatRoomAdapter.swapCursor(null);
        }
    };


    /*
     * for chat room list fragment call to set chatroomlist with listener
     */
    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //when select a room, disable the chat room list click function here.
            DisableClick();

            //get chat room name
            if(typedCursor.getCursor() != null){
                int pos = position;
                typedCursor.moveToFirst();
                while(pos > 0){
                    typedCursor.moveToNext();
                    pos--;
                }
                chatRoom = new ChatRoom(typedCursor.getCursor());

                //call parent add message fragment method
                ((Client)getActivity()).addFragment(chatRoom);
            }
            else{
                Log.i("ChatRoomListFragment : ", "NO CURSOR EXISTS!");
            }
        }
    };

}
