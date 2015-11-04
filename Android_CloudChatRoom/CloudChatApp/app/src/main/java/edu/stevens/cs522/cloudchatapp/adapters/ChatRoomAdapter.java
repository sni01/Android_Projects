package edu.stevens.cs522.cloudchatapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 4/23/15.
 */
public class ChatRoomAdapter extends ResourceCursorAdapter {
    protected final static int ROW_LAYOUT = R.layout.chat_room_list_row_layout;

    //constructor
    public ChatRoomAdapter(Context context, Cursor cursor){
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        TextView chatRoomName = (TextView)view.findViewById(R.id.chat_room_list_row_name);
        String chatRoomId = MessageContract.getChatRoomName(cursor);
        chatRoomName.setText("Chat Room: " + chatRoomId);
    }
}
