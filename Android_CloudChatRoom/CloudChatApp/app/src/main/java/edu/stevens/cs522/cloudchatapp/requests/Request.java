package edu.stevens.cs522.cloudchatapp.requests;

import android.net.Uri;
import android.os.Parcelable;
import android.util.JsonReader;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.cloudchatapp.responses.Response;

/**
 * Created by nisha0634 on 3/14/15.
 */
public abstract class Request implements Parcelable {
    public long clientID;
    public UUID registerationID;
    public double latitude;
    public double longitude;
    public abstract Map<String, String> getRequestHeaders();
    public abstract Uri getRequestUri();
//    public String getRequestEntity() throws IOException {
//
//    }
//
    public Response getResponse(HttpURLConnection connection, JsonReader rd){
        return new Response(rd);
    }

}
