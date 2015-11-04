package edu.stevens.cs522.cloudchatapp.requests;

import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class RegisterRequest extends Request {

    public static final String REGISTER_REQUEST = "register request";

    public static String clientName;
    private static Map<String, String> headers;

    public RegisterRequest(String clientName, long clientID, UUID registerID, double latitude, double longitude){
        this.clientID =clientID;
        this.registerationID = registerID;
        this.latitude = latitude;
        this.longitude = longitude;

        this.clientName = clientName;
        this.headers = new HashMap<String, String>();

        headers.put("X-latitude", Double.toString(latitude));
        headers.put("X-longitude", Double.toString(longitude));
    }

    public Map<String, String> getRequestHeaders(){
        return headers;
    }

    public Uri getRequestUri(){
        try{
            String parameters = "username=" + URLEncoder.encode(clientName, "UTF-8") + "&regid=" + URLEncoder.encode(registerationID.toString(), "UTF-8");
            return new Uri.Builder().scheme("http").authority("host-155-246-163-26.dhcp.stevens-tech.edu:8080/chat?").appendPath(parameters).build();
        }
        catch (UnsupportedEncodingException e){
            Log.i("REGISTER REQUEST: ", e.getMessage());
            return null;
        }
    }

    //parcelable functions
    public int describeContents(){ return 0; }

    private RegisterRequest(Parcel in){
        this.clientID = in.readLong();
        long least = in.readLong();
        long most = in.readLong();
        registerationID = new UUID(least, most);
        this.clientName = in.readString();
        this.headers = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel source) {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(this.clientID);
        out.writeLong(this.registerationID.getLeastSignificantBits());
        out.writeLong(this.registerationID.getMostSignificantBits());
        out.writeString(this.clientName);
        out.writeMap(headers);
    }
}
