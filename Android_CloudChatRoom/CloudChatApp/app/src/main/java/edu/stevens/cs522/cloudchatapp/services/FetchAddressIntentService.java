package edu.stevens.cs522.cloudchatapp.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.stevens.cs522.cloudchatapp.Entities.Sender;
import edu.stevens.cs522.cloudchatapp.R;
import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;
import edu.stevens.cs522.cloudchatapp.receivers.ServiceReceiver;

/**
 * Created by nisha0634 on 4/30/15.
 */
public class FetchAddressIntentService extends IntentService {

    //for debugging
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "edu.stevens.cs522.services.fetchAddressIntentService";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String TAG = "FetchAddressIntentService";
    public static final String PARCELABLE_SENDER_LIST = "parcelable senders list";

    //receiver
    private static ResultReceiver receiver;
    String errorMessage = "";

    //content resolver
    private static ContentResolver contentResolver;

    //constructor
    public FetchAddressIntentService(){
        super("FETCH ADDRESS SERVICE");
    }

    //handle intent info from front end and send out request
    public void onHandleIntent(Intent intent){
        ArrayList<Sender> senders = intent.getParcelableArrayListExtra(PARCELABLE_SENDER_LIST);

        receiver = intent.getParcelableExtra(ServiceReceiver.FETCH_ADDRESSES_SERVICE_RECEIVER);

        contentResolver = this.getContentResolver();

        for(int i = 0; i < senders.size(); i++){
            FetchAddressForSender(senders.get(i));
        }

        int resultCode;
        if(errorMessage.length() > 0) resultCode = FAILURE_RESULT;
        else resultCode = SUCCESS_RESULT;

        deliverResultToReceiver(resultCode, errorMessage);
    }

    public void FetchAddressForSender(Sender sender){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(Double.parseDouble(sender.latitude), Double.parseDouble(sender.longitude), 1);
        }
        catch (IOException e){
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, e);
        }
        catch (IllegalArgumentException e){
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " + "Latitude = " + sender.latitude + ", Longitude = " + sender.longitude, e);
        }

        if(addresses == null || addresses.size() == 0){
            if(addresses.isEmpty()){
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
        }
        else{
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            //deliverResultToReceiver(SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments));
            String address_to_string = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            sender.address = address_to_string;
            updateAddress(sender);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }

    private void updateAddress(Sender sender){
        String where = MessageContract.SENDER_USERNAME + " = '" + sender.username + "'";
        ContentValues values = new ContentValues();
        sender.writeToProvider(values);
        contentResolver.update(MessageContract.CONTENT_ACTIVE_CLIENT_URI(), values, where, null);
    }
}
