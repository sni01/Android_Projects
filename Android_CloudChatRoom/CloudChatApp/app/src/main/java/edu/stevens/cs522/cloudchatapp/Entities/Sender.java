package edu.stevens.cs522.cloudchatapp.Entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 4/11/15.
 */
public class Sender implements Parcelable {
    public long id;
    public String username;
    public String longitude;
    public String latitude;
    public String address;

    public Sender(String username, String latitude, String longitude){
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = "";
    }

    public Sender(String username, String latitude, String longitude, String address){
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }


    public Sender(Cursor cursor){
        this.username = MessageContract.getSenderUsername(cursor);
        this.longitude = MessageContract.getLongitude(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
        this.address = MessageContract.getAddress(cursor);
    }

    public void writeToProvider(ContentValues values){
        values.put(MessageContract.SENDER_USERNAME, this.username);
        values.put(MessageContract.LONGITUDE, this.longitude);
        values.put(MessageContract.LATITUDE, this.latitude);
        values.put(MessageContract.ADDRESS, this.address);
    }


    //parcelable
    //parcelable functions
    public int describeContents(){ return 0; }

    private Sender(Parcel in){
        this.id = in.readLong();
        this.username = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.address = in.readString();
    }

    public static final Creator<Sender> CREATOR = new Creator<Sender>() {
        @Override
        public Sender createFromParcel(Parcel source) {
            return new Sender(source);
        }

        @Override
        public Sender[] newArray(int size) {
            return new Sender[size];
        }
    };

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(id);
        out.writeString(username);
        out.writeString(latitude);
        out.writeString(longitude);
        out.writeString(address);
    }
}
