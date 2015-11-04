package edu.stevens.cs522.cloudchatapp.requests;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;

import edu.stevens.cs522.cloudchatapp.Entities.ChatRoom;
import edu.stevens.cs522.cloudchatapp.Entities.Message;
import edu.stevens.cs522.cloudchatapp.Entities.Sender;
import edu.stevens.cs522.cloudchatapp.activities.PreferenceActivity;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.responses.Response;
import edu.stevens.cs522.cloudchatapp.responses.StreamingResponse;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class RequestProcessor {

    public static final int resultCode = 1;


    public static final String CLIENT_ID = "client id result";
    public static final String SEQUENCE = "message sequence number";

    private ResultReceiver resultReceiver;
    private RestMethod restMethod;
    private Context context;
    private StreamingResponse streamingResponse;

    private static ContentResolver contentResolver;

    public RequestProcessor(Context context, ResultReceiver resultReceiver, String serverAddress){
        this.resultReceiver = resultReceiver;
        this.context = context;
        //initial message manager and send out cursor query
        contentResolver = context.getContentResolver();
        restMethod = new RestMethod(serverAddress);
    }

    public void perform(RegisterRequest request){
        Response registerResponse = restMethod.perform(request);
        Bundle bundle = new Bundle();
        Map<String, String> results = registerResponse.getResult();
        bundle.putString(CLIENT_ID, results.get("id"));
        resultReceiver.send(resultCode, bundle);
    }

    public void perform(SendMessageRequest request){
        Response sendMessageResponse = restMethod.perform(request);
        Bundle bundle = new Bundle();
        Map<String, String> results = sendMessageResponse.getResult();
        bundle.putString(SEQUENCE, results.get("id"));
        resultReceiver.send(resultCode, bundle);
    }

    public void perform(SynchronizeRequest request) throws IOException{

        //initial StreamOutput and fetch messages with sequence number = 0.
        StreamingOutput streamingOutput = new StreamingOutput(this.context);

        //perform request
        streamingResponse = this.restMethod.perform(request, streamingOutput);

        //delete sequence number 0 messages and all active clients
        //String where = MessageContract.SEQUENCE_NUMBER + " = 0"; //sequence number == 0
        String where = MessageContract.SEQUENCE_NUMBER + " = 0";
        contentResolver.delete(MessageContract.CONTENT_ACTIVE_CLIENTS_URI(), null, null);
        contentResolver.delete(MessageContract.CONTENT_MULTIPLE_MESSAGES_URI(), where, null);

        //get responses
        InputStream inputStream = streamingResponse.getInputStream();

        //read and persist
        readFromInputStream(inputStream);
    }

    /*
     * send to jsonWriter and send to server
     */
    public void readFromInputStream(InputStream inputStream){
        try{
            InputStreamReader reader = new InputStreamReader(inputStream);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(reader);
            String read = br.readLine();
            while(read != null) {
                sb.append(read);
                read = br.readLine();
            }
            StringReader stringReader = new StringReader(sb.toString());
            JsonReader jsonReader = new JsonReader(stringReader);
            readFromReader(jsonReader);

            //disconnect
            streamingResponse.closeConnection();
        }
        catch (IOException e){
            Log.i("RequestProcessor:", "I/O Exception");
        }

    }

    /*
     * read from jsonReader and store into database
     */
    public void readFromReader(JsonReader jsonReader) throws IOException{
        jsonReader.beginObject();
        while(jsonReader.peek() != JsonToken.END_OBJECT){
            String label = jsonReader.nextName();
            if(label.equals("clients")){
                jsonReader.beginArray();
                while(jsonReader.hasNext()){
                    //every client send while reading
                    Sender current_sender = readSender(jsonReader);
                    ContentValues values = new ContentValues();
                    current_sender.writeToProvider(values);
                    Uri uri = MessageContract.CONTENT_ACTIVE_CLIENT_URI();
                    contentResolver.insert(uri, values);
                }
                jsonReader.endArray();
            }
            label = jsonReader.nextName();
            if(label.equals("messages")){
                //record most recently sequence number
                long most_sequence_number = -1;

                //start reading from json
                jsonReader.beginArray();
                while(jsonReader.hasNext()){
                    Message message = readMessage(jsonReader); //read message by message and add to list
                    ContentValues values = new ContentValues();
                    message.writeToProvider(values);
                    Uri uri = MessageContract.CONTENT_MESSAGE_URI();
                    contentResolver.insert(uri, values);
                    long sequence_number = values.getAsLong(MessageContract.SEQUENCE_NUMBER);
                    if(most_sequence_number < sequence_number) most_sequence_number = sequence_number;
                }
                jsonReader.endArray();

                //set most recently sequence number to SharedPreference
                Bundle bundle = new Bundle();
                bundle.putLong(PreferenceActivity.SEQUENCE_NUMBER, most_sequence_number);
                resultReceiver.send(resultCode, bundle);
            }
            else{
                Log.i("Processor:", "wrong incoming message formats");
            }
        }
        jsonReader.endObject();
    }

    public Sender readSender(JsonReader jsonReader) throws IOException{
        String username = "";
        String latitude = "";
        String longitude = "";
        jsonReader.beginObject();
        while(jsonReader.hasNext()){
            String key = jsonReader.nextName();
            if(key.equals("sender")){
                username = jsonReader.nextString();
            }
            else if(key.equals("X-latitude")){
                latitude = jsonReader.nextString();
            }
            else if(key.equals("X-longitude")){
                longitude = jsonReader.nextString();
            }
            else{
                Log.i("RequestProcessor: ", "wrong response format");
            }
        }
        jsonReader.endObject();
        return new Sender(username, latitude, longitude);
    }

    public Message readMessage(JsonReader jsonReader) throws IOException{
        long timestamp = -1;
        long sequence_number = -1;
        String sender = "";
        String text = "";
        String chatroom = ""; //now it just a default room id
        String latitude = "";
        String longitude = "";
        //every message
        jsonReader.beginObject();
        while(jsonReader.hasNext()){
            String key = jsonReader.nextName();
            if(key.equals("timestamp")){
                timestamp = jsonReader.nextLong();
            }
            else if(key.equals("seqnum")){
                sequence_number = jsonReader.nextLong();
            }
            else if(key.equals("sender")){
                sender = jsonReader.nextString();
            }
            else if(key.equals("text")){
                text = jsonReader.nextString();
            }
            else if(key.equals("chatroom")){
                chatroom = jsonReader.nextString();
            }
            else if(key.equals("X-longitude")){
                longitude = jsonReader.nextString();
            }
            else if(key.equals("X-latitude")){
                latitude = jsonReader.nextString();
            }
            else{
                Log.i("Processor: ", "wrong incoming message format");
            }
        }
        jsonReader.endObject();

        //insert chatRoom firstly and get foreign key and then insert message
        ChatRoom chatRoom = new ChatRoom(chatroom);
        ContentValues values = new ContentValues();
        chatRoom.writeToProvider(values);
        Uri uri = contentResolver.insert(MessageContract.CONTENT_ALL_CHAT_ROOM_URI(), values);
        String lastSegment = MessageContract.getId(uri);
        long chatRoomFK = Long.parseLong(lastSegment);
        return new Message(text, sequence_number, timestamp, sender, latitude, longitude, chatRoomFK);
    }

}
