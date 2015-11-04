package edu.stevens.cs522.BookstoreWithEntityManager.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.BookstoreWithEntityManager.entities.Author;

/**
 * Created by nisha0634 on 2/16/15.
 */
public class BookProvider extends ContentProvider{

    //URI request ids
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    //URI matcher
    private static UriMatcher uriMatcher;


    //database variables
    private static final String DATABASE_NAME = "cart.db";
    private static final String BOOK_TABLE = "books";
    private static final String AUTHOR_TABLE = "authors";
    private static final int DATABASE_VERSION = 1;

    //database connection variables
    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper helper; //inner class DatabaseHelper


    //database manipulation commands: create/upgrade
    private static final String CREATE_BOOK_TABLE = "create table " + BOOK_TABLE + "(" + Contract._ID + " integer primary key, " + Contract.TITLE + " text not null, " + Contract.PRICE + " text not null, " + Contract.ISBN + " text not null "+ ")";
    private static final String CREATE_AUTHOR_TABLE = "create table " + AUTHOR_TABLE + "(" + Contract._ID + " integer primary key, " + Contract.FIRSTNAME + " text not null, " + Contract.LASTNAME + " text not null, " + Contract.MIDDLEINITIAL + " text not null, " + Contract.BOOK_FK + " integer not null, " + "FOREIGN KEY (" + Contract.BOOK_FK + ") REFERENCES " + BOOK_TABLE + "(" + Contract._ID +") ON DELETE CASCADE" +")";
    private static final String CREATE_INDEX = "CREATE INDEX AuthorsBookIndex ON " + AUTHOR_TABLE + "(" + Contract.BOOK_FK + ")";
    private static final String BOOK_TABLE_DROP = "DROP TABLE IF EXISTS " + BOOK_TABLE;
    private static final String AUTHOR_TABLE_DROP = "DROP TABLE IF EXISTS " + AUTHOR_TABLE;
    private static final String SELECT_ALL_BOOKS = "SELECT " + BOOK_TABLE + "." + Contract._ID + ", " + Contract.TITLE + ", " + Contract.PRICE + ", " + Contract.ISBN + ", GROUP_CONCAT(" + Contract.FIRSTNAME + ", '|') as author_names FROM " + BOOK_TABLE + " LEFT OUTER JOIN " + AUTHOR_TABLE + " ON " + BOOK_TABLE + "." + Contract._ID + " = " + Contract.BOOK_FK + " GROUP BY " + BOOK_TABLE + "." + Contract._ID + ", " + Contract.TITLE + ", " + Contract.PRICE + ", " + Contract.ISBN;





    //call back
    public boolean onCreate(){
        //connect to database
        this.context = getContext(); //can I get activity context here?
        this.helper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = helper.getWritableDatabase();

        //add all book uri and single book uri matcher
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_ITEM_PATH, SINGLE_ROW);
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort){
        switch(uriMatcher.match(uri)){
            case ALL_ROWS:
                //fetch all books
                String[] selections = {};
                return db.rawQuery(SELECT_ALL_BOOKS, selections);
            case SINGLE_ROW:
                //fetch one book
                String rowId = Contract.getId(uri);
                String[] projections = {Contract._ID, Contract.TITLE, Contract.PRICE , Contract.ISBN, Contract.AUTHOR_NAMES};
                String SELECT_BOOK = "SELECT " + BOOK_TABLE + "." + Contract._ID + ", " + Contract.TITLE + ", " + Contract.PRICE + ", " + Contract.ISBN + ", GROUP_CONCAT(" + Contract.FIRSTNAME + ", '|') as author_names FROM " + BOOK_TABLE + " LEFT OUTER JOIN " + AUTHOR_TABLE + " ON " + BOOK_TABLE + "." + Contract._ID + " = " + Contract.BOOK_FK  +" GROUP BY " + BOOK_TABLE + "." +  Contract._ID + ", " + Contract.TITLE + ", " + Contract.PRICE + ", " + Contract.ISBN + " HAVING " + BOOK_TABLE + "." +  Contract._ID + " = " + rowId;
                return db.rawQuery(SELECT_BOOK, null);
            default:
                return null;
        }
    }

    public Uri insert(Uri uri, ContentValues values){
        //add book to database
        String title = values.getAsString(Contract.TITLE);
        String isbn = values.getAsString(Contract.ISBN);
        String price = values.getAsString(Contract.PRICE);

        //insert book
        ContentValues book_values = new ContentValues();
        Contract.putTitle(book_values, title);
        Contract.putPrice(book_values, price);
        Contract.putIsbn(book_values, isbn);
        long rowId = db.insert(BOOK_TABLE, null, book_values);


        //insert authors
        String authors_names = values.getAsString(Contract.AUTHOR_NAMES);
        String[] names = Contract.readStringArray(authors_names); //separate names
        for(int i = 0; i < names.length; i++){
            ContentValues author_values = new ContentValues();
            Author author = new Author(names[i]);
            Contract.putFirstName(author_values, author.firstName);
            Contract.putMiddelInitial(author_values, author.middleInitial);
            Contract.putLASTNAME(author_values, author.lastName);
            Contract.putBookFK(author_values, rowId);
            db.insert(AUTHOR_TABLE, null, author_values);
        }
        //build uri to inform content resolver
        return Contract.CONTENT_URI(rowId + "");
    }

    public int delete(Uri uri, String where, String[] selectionArgs){
        switch(uriMatcher.match(uri)){
            case ALL_ROWS:
                //delete all rows
                if(where == null || where.length() == 0){
                    int deleteBookRes = db.delete(BOOK_TABLE, null, null);
                    int deleteAuthorRes = db.delete(AUTHOR_TABLE, null, null);
                    return (deleteBookRes > 0 && deleteAuthorRes > 0) ? 1 : 0;
                }
                else{
                    String[] ids = where.split("%");
                    String where_books = "";
                    String where_authors_fk = "";
                    for(int i = 0; i < ids.length; i++){
                        where_books += BOOK_TABLE + "." + Contract._ID + " = " + ids[i] + " OR ";
                        where_authors_fk += AUTHOR_TABLE + "." + Contract.BOOK_FK + " = " + ids[i] + " OR ";
                    }
                    if(where_books.length() > 0 && where_authors_fk.length() > 0){
                        where_books = where_books.substring(0, where_books.length() - 4);
                        where_authors_fk = where_authors_fk.substring(0, where_authors_fk.length() - 4);
                    }
                    return (db.delete(BOOK_TABLE, where_books, null) > 0 && db.delete(AUTHOR_TABLE, where_authors_fk, null) > 0) ? 1 : 0;
                }
            case SINGLE_ROW:
                //delete only one row
                String rowId = Contract.getId(uri);
                String where_book = Contract._ID + "=" + rowId; //but we should cascade delete authors
                String where_authors = Contract.BOOK_FK + " = " + rowId;
                return (db.delete(BOOK_TABLE, where_book, null) > 0 && db.delete(AUTHOR_TABLE, where_authors, null) > 0) ? 1 : 0;
            default:
                System.out.println("arrived wrong");
                return -1;
        }
    }

    //this should be abstract method.
    public int update(Uri uri, ContentValues values, String content, String[] contents){
        return 0;
    }

    //get type methods, required by content provide
    public String getType(Uri _uri){
        switch(uriMatcher.match(_uri)){
            case ALL_ROWS:
                return Contract.ContentType("book");
            case SINGLE_ROW:
                return Contract.ContentItemType("book");
            default:
                throw new IllegalArgumentException("Unsupported URI: " + _uri);
        }
    }



    //private class for db helper
    private class DatabaseHelper extends SQLiteOpenHelper {
        //constructor
        public DatabaseHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version){
            super(context, dbName, factory, version);
        }

        //create db
        public void onCreate(SQLiteDatabase _db){
            //_db.execSQL(BOOK_TABLE_DROP);
            //_db.execSQL(AUTHOR_TABLE_DROP);
            _db.execSQL(CREATE_BOOK_TABLE);
            _db.execSQL(CREATE_AUTHOR_TABLE);
            _db.execSQL(CREATE_INDEX); //create index for fetch improvement?
        }

        //upgrade db
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion){
            _db.execSQL(BOOK_TABLE_DROP);
            _db.execSQL(AUTHOR_TABLE_DROP);
            onCreate(_db);
        }
    }

}
