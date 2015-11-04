package edu.stevens.cs522.cloudchatapp.requests;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 4/15/15.
 */
public class StreamingOutput {

    private ContentResolver contentResolver;

    private HttpURLConnection connection;

    public StreamingOutput(Context context){
        this.contentResolver = context.getContentResolver();
    }

    public void writeToConn(HttpURLConnection connection) throws IOException{
        this.connection = connection;
        fetchSendMessages(); //fetch firstly
    }

    public void fetchSendMessages() throws IOException{
        //get all messages that sequence number equals 0;
        String where = "0";
        Cursor cursor = contentResolver.query(MessageContract.CONTENT_MULTIPLE_MESSAGES_URI(), null, where, null, null);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        JsonWriter writer = new JsonWriter(out);
        writeToRequest(cursor, writer);
    }

    /*
     * write to jsonWriter and send to server
     */
    public void writeToRequest(Cursor cursor, JsonWriter writer) throws IOException{
        writer.beginArray();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            writer.beginObject();
            writer.name(SendMessageRequest.CHATROOM);
            writer.value(MessageContract.getChatRoomName(cursor));
            writer.name(SendMessageRequest.TIMESTAMP);
            writer.value(MessageContract.getTimestamp(cursor));
            writer.name(SendMessageRequest.LATITUDE);
            String latitude = MessageContract.getLatitude(cursor);
            writer.value(Double.parseDouble(latitude));
            writer.name(SendMessageRequest.LONGITUDE);
            String longitude = MessageContract.getLongitude(cursor);
            writer.value(Double.parseDouble(longitude));
            writer.name(SendMessageRequest.TEXT);
            writer.value(MessageContract.getContent(cursor));
            writer.endObject();
            cursor.moveToNext();
        }
        //end of json array
        writer.endArray();
        writer.flush();
        writer.close();
    }

}
