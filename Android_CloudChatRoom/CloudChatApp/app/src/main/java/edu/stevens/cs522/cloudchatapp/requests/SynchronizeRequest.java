package edu.stevens.cs522.cloudchatapp.requests;

import android.net.Uri;
import android.os.Parcel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nisha0634 on 4/9/15.
 */
public class SynchronizeRequest extends Request{

    public static long most_sequence_number;

    private static Map<String, String> headers;

    public static final String SYNCHRONIZE_REQUEST = "synchronize request";

    public SynchronizeRequest(long clientID, UUID registerationID, long most_sequence_number, double latitude, double longitude){
        //generic parameters
        this.clientID = clientID;
        this.registerationID = registerationID;
        this.latitude = latitude;
        this.longitude = longitude;

        //sepecific parameters.
        this.most_sequence_number = most_sequence_number;

        //set headers
        this.headers = new HashMap<String, String>();
        //just fixed input a latitude and longitude here
        headers.put("X-latitude", Double.toString(latitude));
        headers.put("X-longitude", Double.toString(longitude));
    }

    public Map<String, String> getRequestHeaders(){
        return this.headers;
    }

    //later implementation
    public Uri getRequestUri(){
        return null;
    }

    //parcelable
    public int describeContents(){ return 0; }

    private SynchronizeRequest(Parcel in){
        this.clientID = in.readLong();
        long least = in.readLong();
        long most = in.readLong();
        registerationID = new UUID(least, most);
        this.most_sequence_number = in.readLong();
        this.headers = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<SynchronizeRequest> CREATOR = new Creator<SynchronizeRequest>() {
        @Override
        public SynchronizeRequest createFromParcel(Parcel source) {
            return new SynchronizeRequest(source);
        }

        @Override
        public SynchronizeRequest[] newArray(int size) {
            return new SynchronizeRequest[size];
        }
    };

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(this.clientID);
        out.writeLong(this.registerationID.getLeastSignificantBits());
        out.writeLong(this.registerationID.getMostSignificantBits());
        out.writeLong(this.most_sequence_number);
        out.writeMap(headers);
    }
}
