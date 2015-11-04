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
 * Created by nisha0634 on 4/18/15.
 */
public class ActiveClientAdapter extends ResourceCursorAdapter {
    protected final static int ROW_LAYOUT = R.layout.active_client_layout;

    //constructor
    public ActiveClientAdapter(Context context, Cursor cursor){
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        TextView username = (TextView)view.findViewById(R.id.active_client_username);
        TextView latitude = (TextView)view.findViewById(R.id.active_client_latitude);
        TextView longitude = (TextView)view.findViewById(R.id.active_client_longitude);
        TextView address = (TextView)view.findViewById(R.id.active_client_address);
        String active_client_username = MessageContract.getSenderUsername(cursor);
        String active_client_latitude = MessageContract.getLatitude(cursor);
        String active_client_longitude = MessageContract.getLongitude(cursor);
        String active_client_address = MessageContract.getAddress(cursor);
        username.setText("client name: " + active_client_username);
        latitude.setText("client latitude: " + active_client_latitude);
        longitude.setText("client longitude: " + active_client_longitude);
        address.setText("client address: "+ active_client_address);
    }
}
