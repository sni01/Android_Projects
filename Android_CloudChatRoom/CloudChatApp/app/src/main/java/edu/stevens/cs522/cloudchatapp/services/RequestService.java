package edu.stevens.cs522.cloudchatapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;

import edu.stevens.cs522.cloudchatapp.activities.PreferenceActivity;
import edu.stevens.cs522.cloudchatapp.receivers.ServiceReceiver;
import edu.stevens.cs522.cloudchatapp.requests.RegisterRequest;
import edu.stevens.cs522.cloudchatapp.requests.RequestProcessor;
import edu.stevens.cs522.cloudchatapp.requests.SendMessageRequest;
import edu.stevens.cs522.cloudchatapp.requests.SynchronizeRequest;


/**
 * Created by nisha0634 on 3/14/15.
 */
public class RequestService extends IntentService {
    //static variables
    public static final String REGISTER_ACTION = "edu.stevens.cs522.cloudchatapp.message.register";
    public static final String SEND_ACTION = "edu.stevens.cs522.cloudchatapp.message.send";
    public static final String SYNCHRONIZE_ACTION = "edu.stevens.cs522.cloudchatapp.message.synchronize";

    //receiver
    private static ResultReceiver resultReceiver;

    //request processor
    private static RequestProcessor requestProcessor;

    //constructor
    public RequestService(){
        super("REQUEST SERVICE");
    }

    //handle intent info from front end and send out request
    public void onHandleIntent(Intent request){
        String action = request.getAction();
        String serverAddress = request.getStringExtra(PreferenceActivity.SERVER_ADDRESS);
        resultReceiver = request.getParcelableExtra(ServiceReceiver.REQUEST_SERVICE_RECEIVER); //get receiver from activity
        requestProcessor = new RequestProcessor(this.getApplicationContext(), resultReceiver, serverAddress); //here use ResultReceiver instead of RequestServiceReceiver
        Log.i("REQUEST SERVICE: ", "START SERVICE!");
        if(action.equals(REGISTER_ACTION)){
            RegisterRequest registerRequest = request.getParcelableExtra(RegisterRequest.REGISTER_REQUEST);
            requestProcessor.perform(registerRequest);
        }
        else if(action.equals(SEND_ACTION)){
            SendMessageRequest sendMessageRequest = request.getParcelableExtra(SendMessageRequest.SEND_MESSAGE_REQUEST);
            requestProcessor.perform(sendMessageRequest);
        }
        else if(action.equals(SYNCHRONIZE_ACTION)){
            SynchronizeRequest synchronizeRequest = request.getParcelableExtra(SynchronizeRequest.SYNCHRONIZE_REQUEST);
            try{
                requestProcessor.perform(synchronizeRequest);
            }
            catch (IOException e){
                Log.i("RequestProcessor :", "I/O Exception !");
            }
        }
        else{
            Log.i("REQUEST WRONG: ", "ACTION IS NOT SUPPORTED");
        }
    }

}
