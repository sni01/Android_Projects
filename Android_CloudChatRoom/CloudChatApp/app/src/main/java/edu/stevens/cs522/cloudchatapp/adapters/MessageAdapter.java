package edu.stevens.cs522.cloudchatapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.Date;

import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 3/11/15.
 */
public class MessageAdapter extends ResourceCursorAdapter {
    protected final static int ROW_LAYOUT = R.layout.client_message_list_row_layout;

    //constructor
    public MessageAdapter(Context context, Cursor cursor){
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        TextView messageContent = (TextView)view.findViewById(R.id.message_context);
        TextView messageTimestamp = (TextView)view.findViewById(R.id.message_timestamp);
        TextView messageSender = (TextView)view.findViewById(R.id.message_sender_identity);
        TextView messageSequence = (TextView)view.findViewById(R.id.message_sequence_number);
        TextView messageChatRoomId = (TextView)view.findViewById(R.id.message_chat_room_id);
        TextView messageLatitude = (TextView)view.findViewById(R.id.message_latitude);
        TextView messageLongitude = (TextView)view.findViewById(R.id.message_longitude);

        String content = MessageContract.getContent(cursor);
        long timestamp = MessageContract.getTimestamp(cursor);
        String senderUsername = MessageContract.getSenderUsername(cursor);
        long sequence = MessageContract.getSequence(cursor);
        String chatRoomName = MessageContract.getChatRoomName(cursor);
        String latitude = MessageContract.getLatitude(cursor);
        String longitude = MessageContract.getLongitude(cursor);

        messageContent.setText(content);
        Date date = new Date(timestamp);
        messageTimestamp.setText(date.toString());
        messageSender.setText("sender username :" + senderUsername);
        messageSequence.setText("sequence number : " + sequence);
        messageChatRoomId.setText("chatRoomID: " + chatRoomName);
        messageLatitude.setText("message latitude :" + latitude);
        messageLongitude.setText("message longitude :" + longitude);
    }
}
