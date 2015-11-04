package edu.stevens.cs522.cloudchatapp.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.UUID;

import edu.stevens.cs522.cloudchatapp.activities.PreferenceActivity;
import edu.stevens.cs522.cloudchatapp.receivers.ServiceReceiver;
import edu.stevens.cs522.cloudchatapp.requests.RegisterRequest;
import edu.stevens.cs522.cloudchatapp.requests.SendMessageRequest;
import edu.stevens.cs522.cloudchatapp.requests.SynchronizeRequest;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class ServiceHelper {

    //client activity context to start services
    private Activity context;

    //several parameters
    private static long clientID;
    private static String clientName;
    private static UUID regId;
    private static long most_recent_sequence_number;

    private ServiceReceiver receiver; //all request use the same receiver generically
    private static Intent serviceIntent;

    public ServiceHelper(Activity context, ServiceReceiver receiver){
        this.context = context;
        this.receiver = receiver;


        SharedPreferences preferences = context.getSharedPreferences(PreferenceActivity.MY_PREFS, Context.MODE_PRIVATE);
        this.clientID = preferences.getLong(PreferenceActivity.CLIENT_ID, 0);
        this.clientName =  preferences.getString(PreferenceActivity.CLIENT_NAME, "DEFAULT CLIENT NAME");
        long least = preferences.getLong(PreferenceActivity.LEAST_SIGNIFICANT_BITS, 0);
        long most = preferences.getLong(PreferenceActivity.MOST_SIGNIFICANT_BITS, 0);
        this.regId = new UUID(most, least);
        String serverAddress = preferences.getString(PreferenceActivity.SERVER_ADDRESS, "_DEFAULT ADDRESS");
        this.most_recent_sequence_number = preferences.getLong(PreferenceActivity.SEQUENCE_NUMBER, 0);

        //common parameters, receiver and server address
        this.serviceIntent = new Intent(context, RequestService.class);
        serviceIntent.putExtra(ServiceReceiver.REQUEST_SERVICE_RECEIVER, this.receiver);
        serviceIntent.putExtra(PreferenceActivity.SERVER_ADDRESS, serverAddress);
    }


    public void setReceiver(ServiceReceiver receiver) {
        this.receiver = receiver;
        serviceIntent.putExtra(ServiceReceiver.REQUEST_SERVICE_RECEIVER, this.receiver);
    }

    /*
     * send message three parameters: chat room Identifier, message content, timestamp
     * get response with unique message sequence number
     */
    public void sendMessage(String chatRoomId, String message_content, long timestamp){
        SendMessageRequest sendMessageRequest = new SendMessageRequest(clientID, regId, chatRoomId, message_content, timestamp);
        serviceIntent.putExtra(SendMessageRequest.SEND_MESSAGE_REQUEST , sendMessageRequest);
        serviceIntent.setAction(RequestService.SEND_ACTION);
        Log.i("SERVICE HELPER: ", "START SERVICE SEND MESSAGE!");
        context.startService(serviceIntent);
    }

    /*
     * call request service and send register request to server
     */
    public void register(){
        //get location
        SharedPreferences preferences = context.getSharedPreferences(PreferenceActivity.MY_PREFS, Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(preferences.getString(PreferenceActivity.LATITUDE, "0"));
        double longitude = Double.parseDouble(preferences.getString(PreferenceActivity.LONGITUDE, "0"));

        //start service
        RegisterRequest registerRequest = new RegisterRequest(clientName, 0, regId, latitude, longitude);
        serviceIntent.putExtra(RegisterRequest.REGISTER_REQUEST, registerRequest);
        serviceIntent.setAction(RequestService.REGISTER_ACTION);
        Log.i("SERVICE HELPER: ", "START SERVICE REGISTER!");
        context.startService(serviceIntent);
    }

    /*
     * sync request
     */
    public void synchronize(){
        //set parameters
        setSynchronize();

        //start service
        Log.i("SERVICE HELPER: ", "START SERVICE SYNCHRONIZE");
        context.startService(serviceIntent);
    }

    public void setSynchronize(){
        SharedPreferences preferences = context.getSharedPreferences(PreferenceActivity.MY_PREFS, Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(preferences.getString(PreferenceActivity.LATITUDE, "0"));
        double longitude = Double.parseDouble(preferences.getString(PreferenceActivity.LONGITUDE, "0"));
        this.clientID = preferences.getLong(PreferenceActivity.CLIENT_ID, 0);
        this.most_recent_sequence_number = preferences.getLong(PreferenceActivity.SEQUENCE_NUMBER, 0);
        SynchronizeRequest request = new SynchronizeRequest(this.clientID, regId, this.most_recent_sequence_number, latitude, longitude);
        serviceIntent.putExtra(SynchronizeRequest.SYNCHRONIZE_REQUEST, request);
        serviceIntent.setAction(RequestService.SYNCHRONIZE_ACTION);
    }
}
