package edu.stevens.cs522.cloudchatapp.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import edu.stevens.cs522.cloudchatapp.factory.IReceiver;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class ServiceReceiver extends ResultReceiver{
    public static final String REQUEST_SERVICE_RECEIVER = "request service receiver";
    public static final String FETCH_ADDRESSES_SERVICE_RECEIVER = "fetch addresses service receiver";

    private IReceiver receiver;

    //constructor
    public ServiceReceiver(Handler handler){
        super(handler);
    }

    //set iReceiver
    public void setReceiver(IReceiver receiver){
        this.receiver = receiver;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle result){
        if(receiver != null){
            receiver.onReceiveResult(resultCode, result);
        }
        else{
            Log.i("NO RECEIVER: ", "ACTIVITY HAS BEEN STOPPED.");
        }
    }
}
