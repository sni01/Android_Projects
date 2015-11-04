package edu.stevens.cs522.cloudchatapp.requests;

import android.net.Uri;
import android.os.Parcel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nisha0634 on 3/14/15.
 */
public class SendMessageRequest extends Request {

    public static final String SEND_MESSAGE_REQUEST = "send message request";

    public static final String CHATROOM = "chatroom";
    public static final String TIMESTAMP = "timestamp";
    public static final String TEXT = "text";
    public static final String LATITUDE = "X-latitude";
    public static final String LONGITUDE = "X-longitude";

    public static String chatRoomId;
    public static String messageContent;
    public static long timeStamp;

    private Map<String, String> headers = new HashMap<String, String>();

    public SendMessageRequest(long clientID, UUID registerationID, String chatRoomId, String messageContent, long timeStamp){
        this.clientID = clientID;
        this.registerationID = registerationID;

        this.chatRoomId = chatRoomId;
        this.messageContent = messageContent;
        this.timeStamp = timeStamp;

        this.headers = new HashMap<String, String>();
        //just fixed input a latitude and longitude here
        headers.put("X-latitude", "40.0000000");
        headers.put("X-longitude", "74.0000000");
    }

    public Map<String, String> getRequestHeaders(){
        return this.headers;
    }
    public Uri getRequestUri(){
        return null;
    }
    public String getRequestEntity() throws IOException {
        return null;
    }


    //make this object parcelable
    public int describeContents(){ return 0; }

    private SendMessageRequest(Parcel in){
        this.clientID = in.readLong();
        long least = in.readLong();
        long most = in.readLong();
        registerationID = new UUID(least, most);

        this.chatRoomId = in.readString();
        this.messageContent = in.readString();
        this.timeStamp = in.readLong();

        this.headers = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<SendMessageRequest> CREATOR = new Creator<SendMessageRequest>() {
        @Override
        public SendMessageRequest createFromParcel(Parcel source) {
            return new SendMessageRequest(source);
        }

        @Override
        public SendMessageRequest[] newArray(int size) {
            return new SendMessageRequest[size];
        }
    };

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(this.clientID);
        out.writeLong(this.registerationID.getLeastSignificantBits());
        out.writeLong(this.registerationID.getMostSignificantBits());

        out.writeString(chatRoomId);
        out.writeString(messageContent);
        out.writeLong(timeStamp);

        out.writeMap(headers);
    }
}
