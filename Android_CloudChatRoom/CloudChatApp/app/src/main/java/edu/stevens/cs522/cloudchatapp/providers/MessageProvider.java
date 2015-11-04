package edu.stevens.cs522.cloudchatapp.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.cloudchatapp.contracts.MessageContract;

/**
 * Created by nisha0634 on 3/12/15.
 */
public class MessageProvider extends ContentProvider{
    /*
     * database control variables
     */
    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper helper;

    /*
     * database content variables
     */
    private static final String DATABASE_NAME = "messages.db";
    private static final String MESSAGE_TABLE = "message";
    private static final String ACTIVE_CLIENTS_TABLE = "activeClient"; //for client online
    private static final String CHAT_ROOM_TABLE = "chatRoom"; //for chat room
    private static final int DATABASE_VERSION = 1;

    /*
     * query sql sentence
     */
    //table operations
    private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE " + MESSAGE_TABLE + " (" + MessageContract._ID + " INTEGER PRIMARY KEY, " + MessageContract.CONTENT + " TEXT NOT NULL, " + MessageContract.TIMESTAMP + " LONG NOT NULL, " + MessageContract.SEQUENCE_NUMBER + " LONG NOT NULL, " + MessageContract.SENDER_USERNAME + " TEXT NOT NULL, " + MessageContract.LATITUDE + " TEXT NOT NULL, " + MessageContract.LONGITUDE + " TEXT NOT NULL, " + MessageContract.CHAT_ROOM_FK + " INTEGER NOT NULL, "  + "FOREIGN KEY (" + MessageContract.CHAT_ROOM_FK + ") REFERENCES " + CHAT_ROOM_TABLE + "(" + MessageContract._ID +") ON DELETE CASCADE" +")";
    private static final String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS " + MESSAGE_TABLE;

    private static final String CREATE_ACTIVE_CLIENTS_TABLE = "CREATE TABLE " + ACTIVE_CLIENTS_TABLE + " (" + MessageContract._ID + " INTEGER PRIMARY KEY, " + MessageContract.SENDER_USERNAME + " TEXT NOT NULL, " + MessageContract.LATITUDE + " TEXT NOT NULL, " + MessageContract.LONGITUDE + " TEXT NOT NULL, " + MessageContract.ADDRESS + ")";
    private static final String DROP_ACTIVE_CLIENTS_TABLE = "DROP TABLE IF EXISTS " + ACTIVE_CLIENTS_TABLE;

    private static final String CREATE_CHAT_ROOM_TABLE = "CREATE TABLE " + CHAT_ROOM_TABLE + " (" + MessageContract._ID + " INTEGER PRIMARY KEY, " + MessageContract.CHAT_ROOM_NAME + " TEXT NOT NULL)";
    private static final String DROP_CHAT_ROOM_TABLE = "DROP TABLE IF EXISTS " + CHAT_ROOM_TABLE;


    //table join operation, raw query
    private static String FETCH_MESSAGES_JOIN = "SELECT " + CHAT_ROOM_TABLE + "." + MessageContract.CHAT_ROOM_NAME + ", " + MESSAGE_TABLE + "." + MessageContract._ID + ", " + MESSAGE_TABLE + "." + MessageContract.CONTENT + ", " + MESSAGE_TABLE + "." + MessageContract.TIMESTAMP + ", " + MESSAGE_TABLE + "." + MessageContract.SENDER_USERNAME + ", " + MESSAGE_TABLE + "." + MessageContract.SEQUENCE_NUMBER + ", " + MESSAGE_TABLE + "." + MessageContract.LATITUDE +  ", " + MESSAGE_TABLE + "." + MessageContract.LONGITUDE + " FROM " + CHAT_ROOM_TABLE + " JOIN " + MESSAGE_TABLE + " ON " + CHAT_ROOM_TABLE + "." + MessageContract._ID + " = " + MESSAGE_TABLE + "." + MessageContract.CHAT_ROOM_FK + " WHERE ";


    /*
     * content provider variables
     */
    private static UriMatcher uriMatcher;
    private static final int ALL_MESSAGES = 1;
    private static final int SINGLE_MESSAGE = 2;
    private static final int ALL_ACTIVE_CLIENTS = 3;
    private static final int MULTIPLE_MESSAGES = 4;
    private static final int MULTIPLE_MESSAGES_SENDER = 5;
    private static final int MULTIPLE_MESSAGES_CHATROOM = 6;
    private static final int SINGLE_ACTIVE_CLIENT = 7;
    private static final int CHAT_ROOM = 8; //all chat room fetch
    private static final int FIND_CHAT_ROOM = 9;

    /*
     * content provider methods
     */
    @Override
    public boolean onCreate(){

        //get writable database
        this.context = getContext();
        this.helper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = helper.getWritableDatabase();


        //add uri patterns to uri matcher
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_MESSAGES_PATH, ALL_MESSAGES);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_ACTIVE_CLIENTS_PATH, ALL_ACTIVE_CLIENTS);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_ACTIVE_CLIENT_PATH, SINGLE_ACTIVE_CLIENT);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_MESSAGE_PATH, SINGLE_MESSAGE);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_MULTIPLE_MESSAGES_PATH, MULTIPLE_MESSAGES);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_MULTIPLE_MESSAGES_SENDER_PATH, MULTIPLE_MESSAGES_SENDER);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_MULTIPLE_MESSAGES_CHATROOM_PATH, MULTIPLE_MESSAGES_CHATROOM);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_ALL_CHAT_ROOM_PATH, CHAT_ROOM);
        uriMatcher.addURI(MessageContract.AUTHORITY, MessageContract.CONTENT_FIND_CHAT_ROOM_PATH, FIND_CHAT_ROOM);

        //return boolean
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort){
        switch(uriMatcher.match(uri)){
            case ALL_MESSAGES:
                return db.query(MESSAGE_TABLE, null, null, null, null, null, null);
            case MULTIPLE_MESSAGES_CHATROOM: //for messages of chat room
                String where = CHAT_ROOM_TABLE + "." + MessageContract.CHAT_ROOM_NAME + " = '" + selection + "'";
                String QUERY = FETCH_MESSAGES_JOIN + where;
                return db.rawQuery(QUERY, null);
            case MULTIPLE_MESSAGES_SENDER: //for messages of sender
                //return db.query(MESSAGE_TABLE, null, selection, null, null, null, null);
                where = MESSAGE_TABLE + "." + MessageContract.SENDER_USERNAME + " = '" + selection + "'";
                QUERY = FETCH_MESSAGES_JOIN + where; //selection is only the value here, construct clause
                return db.rawQuery(QUERY, null);
            case MULTIPLE_MESSAGES:
                //for fetching squence number = 0 messages, selection == "sequence_number = '0'"
                where = MESSAGE_TABLE + "." + MessageContract.SEQUENCE_NUMBER + " = " + selection;
                QUERY = FETCH_MESSAGES_JOIN + where;
                return db.rawQuery(QUERY, null);
            case ALL_ACTIVE_CLIENTS:
                return db.query(ACTIVE_CLIENTS_TABLE, null, null, null, null, null, null);
            case CHAT_ROOM:
                return db.query(CHAT_ROOM_TABLE, null, null, null, null, null, null);
            case FIND_CHAT_ROOM:
                where = CHAT_ROOM_TABLE + "." + MessageContract.CHAT_ROOM_NAME + " = '" + selection + "'";
                return db.query(CHAT_ROOM_TABLE, null, where, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        switch(uriMatcher.match(uri)){
            case SINGLE_MESSAGE:
                long insertRow = db.insert(MESSAGE_TABLE, null, values);
                return MessageContract.CONTENT_MESSAGE_URI(insertRow);
            case SINGLE_ACTIVE_CLIENT:
                insertRow = db.insert(ACTIVE_CLIENTS_TABLE, null, values);
                return MessageContract.CONTENT_ACTIVE_CLIENT_URI(insertRow);
            case CHAT_ROOM:
                String chatRoomName = values.getAsString(MessageContract.CHAT_ROOM_NAME);
                String where = MessageContract.CHAT_ROOM_NAME + " = '" + chatRoomName + "'";
                Cursor cursor = db.query(CHAT_ROOM_TABLE, null, where, null, null, null, null);
                if(cursor.getCount() <= 0){
                    insertRow = db.insert(CHAT_ROOM_TABLE, null, values);
                }
                else{
                    cursor.moveToFirst();
                    insertRow = MessageContract.getId(cursor);
                }
                return MessageContract.CONTENT_CHAT_ROOM_URI(insertRow);
            default:
                return null;
        }

    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs){
        switch(uriMatcher.match(uri)){
            case MULTIPLE_MESSAGES: //remove messages with sequence number = 0
                return db.delete(MESSAGE_TABLE, where, null);
            case ALL_ACTIVE_CLIENTS: //remove all active clients
                return db.delete(ACTIVE_CLIENTS_TABLE, null, null);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String content, String[] contents){
        switch (uriMatcher.match(uri)){
            case SINGLE_MESSAGE:
                return db.update(MESSAGE_TABLE, values, content, contents);
            case SINGLE_ACTIVE_CLIENT:
                return db.update(ACTIVE_CLIENTS_TABLE, values, content, contents);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public String getType(Uri _uri){
        switch (uriMatcher.match(_uri)){
            case ALL_MESSAGES:
                return MessageContract.ContentType("message");
            default:
                throw new IllegalArgumentException("Unsupported URI: " + _uri);
        }
    }




    /*
     * private DatabaseHelper class
     */
    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory cursor, int version){
            super(context, databaseName, cursor, version);
        }

        public void onCreate(SQLiteDatabase _db){
            _db.execSQL(CREATE_MESSAGE_TABLE);
            _db.execSQL(CREATE_ACTIVE_CLIENTS_TABLE);
            _db.execSQL(CREATE_CHAT_ROOM_TABLE);
        }

        public void onUpgrade(SQLiteDatabase _db, int old_version, int new_version){
            _db.execSQL(DROP_MESSAGE_TABLE);
            _db.execSQL(DROP_ACTIVE_CLIENTS_TABLE);
            _db.execSQL(DROP_CHAT_ROOM_TABLE);
            onCreate(_db);
        }
    }
}
