package edu.stevens.cs522.cloudchatapp.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by nisha0634 on 3/12/15.
 */
public class MessageContract {
    /*
     * column names for message
     */
    public static final String _ID = "_id";
    public static final String CONTENT = "content";
    public static final String TIMESTAMP = "timestamp";
    public static final String SEQUENCE_NUMBER = "sequence_number";
    public static final String SENDER_USERNAME = "sender_username";
    public static final String CHAT_ROOM_FK = "chat_room_fk";

    /*
     * columns for senders
     */
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ADDRESS = "address";

    /*
     * columns for chat room
     */
    public static final String CHAT_ROOM_NAME = "chat_room_name";

    /*
     * column get and put methods
     */
    //used by both message and client entity
    public static long getId(Cursor cursor){
        return cursor.getLong(cursor.getColumnIndex(_ID));
    }

    //this will not be called, since SQL will generate id itself
    public static void putId(ContentValues values, long id){
        values.put(_ID, id);
    }

    public static String getContent(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(CONTENT));
    }

    public static void putContent(ContentValues values, String content){
        values.put(CONTENT, content);
    }

    public static long getTimestamp(Cursor cursor){
        return cursor.getLong(cursor.getColumnIndex(TIMESTAMP));
    }

    public static void putTimestamp(ContentValues values, long timestamp){
        values.put(CONTENT, timestamp);
    }

    public static long getSequence(Cursor cursor){
        return cursor.getLong(cursor.getColumnIndex(SEQUENCE_NUMBER));
    }

    public static void putSequence(ContentValues values, long sequence_number){
        values.put(SEQUENCE_NUMBER, sequence_number);
    }

    public static String getSenderUsername(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(SENDER_USERNAME));
    }

    public static void putSenderUsername(ContentValues values, String username){
        values.put(SENDER_USERNAME, username);
    }

    public static long getChatRoomFK(Cursor cursor){
        return cursor.getLong(cursor.getColumnIndex(CHAT_ROOM_FK));
    }

    public static void putChatRoomFK(ContentValues values, long chatRoomFK){
        values.put(CHAT_ROOM_FK, chatRoomFK);
    }

    public static String getChatRoomName(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(CHAT_ROOM_NAME));
    }

    public static void setChatRoomName(ContentValues values, String chatRoomName){
        values.put(CHAT_ROOM_NAME, chatRoomName);
    }

    public static String getLatitude(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(LATITUDE));
    }

    public static void setLatitude(ContentValues values, String latitude){
        values.put(LATITUDE, latitude);
    }

    public static String getLongitude(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(LONGITUDE));
    }

    public static void setLongitude(ContentValues values, String longitude){
        values.put(LONGITUDE, longitude);
    }

    public static String getAddress(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex(ADDRESS));
    }

    public static void setAddress(ContentValues values, String address){
        values.put(ADDRESS, address);
    }


    /*
     * URI variables
     */
    public static final String AUTHORITY = "edu.stevens.cs522.cloudchatapp";
    public static final String MESSAGES_PATH = "messages";
    public static final String MESSAGE_PATH = "message";
    public static final String MULTIPLE_MESSAGES_PATH = "some_of_messages";
    public static final String MULTIPLE_MESSAGES_SENDER_PATH = "messages_sender";
    public static final String MULTIPLE_MESSAGES_CHATROOM_PATH = "messages_chatRoom";
    public static final String ACTIVE_CLIENTS_PATH = "active_clients";
    public static final String ACTIVE_CLIENT_PATH = "active_client";
    public static final String CHAT_ROOM_PATH = "chat_room";
    public static final String FIND_CHAT_ROOM_PATH = "find_chat_room";

    public static final String CONTENT_MESSAGES_PATH = CONTENT_PATH(CONTENT_MESSAGES_URI());
    public static final String CONTENT_MESSAGE_PATH = CONTENT_PATH(CONTENT_MESSAGE_URI());
    public static final String CONTENT_MULTIPLE_MESSAGES_PATH = CONTENT_PATH(CONTENT_MULTIPLE_MESSAGES_URI());
    public static final String CONTENT_MULTIPLE_MESSAGES_SENDER_PATH = CONTENT_PATH(CONTENT_MULTIPLE_MESSAGES_SENDER_URI());
    public static final String CONTENT_MULTIPLE_MESSAGES_CHATROOM_PATH = CONTENT_PATH(CONTENT_MULTIPLE_MESSAGES_CHATROOM_URI());
    public static final String CONTENT_ACTIVE_CLIENTS_PATH = CONTENT_PATH(CONTENT_ACTIVE_CLIENTS_URI());
    public static final String CONTENT_ACTIVE_CLIENT_PATH = CONTENT_PATH(CONTENT_ACTIVE_CLIENT_URI());
    public static final String CONTENT_ALL_CHAT_ROOM_PATH = CONTENT_PATH(CONTENT_ALL_CHAT_ROOM_URI());
    public static final String CONTENT_FIND_CHAT_ROOM_PATH = CONTENT_PATH(CONTENT_FIND_CHAT_ROOM_URI());

    //build Uri
    public static Uri CONTENT_MESSAGES_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(MESSAGES_PATH).build();
    }

    public static Uri CONTENT_MULTIPLE_MESSAGES_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(MULTIPLE_MESSAGES_PATH).build();
    }

    public static Uri CONTENT_MESSAGE_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(MESSAGE_PATH).build();
    }

    public static Uri CONTENT_ACTIVE_CLIENTS_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(ACTIVE_CLIENTS_PATH).build();
    }

    public static Uri CONTENT_ACTIVE_CLIENT_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(ACTIVE_CLIENT_PATH).build();
    }

    public static Uri CONTENT_ALL_CHAT_ROOM_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(CHAT_ROOM_PATH).build();
    }

    public static Uri CONTENT_MULTIPLE_MESSAGES_SENDER_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(MULTIPLE_MESSAGES_SENDER_PATH).build();
    }

    public static Uri CONTENT_MULTIPLE_MESSAGES_CHATROOM_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(MULTIPLE_MESSAGES_CHATROOM_PATH).build();
    }

    public static Uri CONTENT_FIND_CHAT_ROOM_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(FIND_CHAT_ROOM_PATH).build();
    }

    public static Uri CONTENT_MESSAGE_URI(long id){
        return withExtendedPath(CONTENT_MESSAGES_URI(), id);
    }

    public static Uri CONTENT_ACTIVE_CLIENT_URI(long id){
        return withExtendedPath(CONTENT_ACTIVE_CLIENTS_URI(), id);
    }

    public static Uri CONTENT_CHAT_ROOM_URI(long id){
        return withExtendedPath(CONTENT_ALL_CHAT_ROOM_URI(), id);
    }


    public static String CONTENT_PATH(Uri uri){
        return uri.getPath().substring(1);
    }

    public static Uri withExtendedPath(Uri uri, long id){
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(id + "");
        return builder.build();
    }

    public static String ContentType(String content){
        return "vnd.android.cursor/vnd." + AUTHORITY + "." + content + "s";
    }

    public static String getId(Uri uri){
        return uri.getLastPathSegment();
    }

}
