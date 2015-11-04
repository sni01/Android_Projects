package edu.stevens.cs522.cloudchatapp.responses;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by nisha0634 on 4/7/15.
 */
public class StreamingResponse {

    //connection
    private HttpURLConnection connection;

    public StreamingResponse(HttpURLConnection connection){
        this.connection = connection;
    }


    public void closeConnection(){
        this.connection.disconnect();
    }

    public Response getResponse(){
        return null;
    }

    public InputStream getInputStream() throws IOException{
        //get input stream
        int httpStatus = connection.getResponseCode();
        Log.i("RECEIVER SERVICE :", "HTTPURLCONNECTION STATUS" + httpStatus);

        return new BufferedInputStream(connection.getInputStream());
    }
}
