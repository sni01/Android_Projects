package edu.stevens.cs522.BookstoreWithEntityManager.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nisha0634 on 2/8/15.
 */
public class Contract {
    //same columns names
    public static final String _ID = "_id";

    //column names for book
    public static final String TITLE = "title";
    public static final String AUTHOR_NAMES = "author_names";
    public static final String PRICE = "price";
    public static final String ISBN = "isbn";

    //colums name for author
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String MIDDLEINITIAL = "middleinitial";
    public static final String BOOK_FK = "book_fk";

    //separator for inserting and deleting authors information
    private static final char SEPARATOR_CHAR = '|';
    private static final Pattern SEPARATOR = Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);
    public static String[] readStringArray(String in){
        return SEPARATOR.split(in);
    }


    //static variables for content providers
    public static final String AUTHORITY = "edu.stevens.cs522.bookstore"; //is this the app name space for this app?
    public static final String book_path = "books";
    //Uri for all five operations
    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI());
    public static final String CONTENT_ITEM_PATH = CONTENT_PATH(CONTENT_URI("#"));


    //build Uri
    public static Uri CONTENT_URI(){
        return new Uri.Builder().scheme("content").authority(AUTHORITY).path(book_path).build();
    }

    public static Uri CONTENT_URI(String id){
        List<String> paths = new LinkedList<String>();
        paths.add(id);
        return withExtendedPath(CONTENT_URI(), paths);
    }

    //extend Uri
    public static Uri withExtendedPath(Uri uri, List<String> paths){
        Uri.Builder builder = uri.buildUpon();
        for(String s : paths){
            builder.appendPath(s);
        }
        return builder.build();
    }

    //get id from Uri
    public static String getId(Uri uri){
        return uri.getLastPathSegment();
    }

    //get content from uri
    public static String CONTENT_PATH(Uri uri){
        //check what you need here, call by CONTENT_PATH(CONTENT_URI);
        return uri.getPath().substring(1);
    }

    //get types
    public static String ContentType(String content){
        return "vnd.android.cursor/vnd." + AUTHORITY + "." + content + "s";
    }

    public static String ContentItemType(String content){
        return "vnd.android.cursor/vnd." + AUTHORITY + "." + content;
    }


    //construct book contract class
    public Contract(){

    }

    public static String getTitle(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
    }

    public static void putTitle(ContentValues values, String title){
        values.put(TITLE, title);
    }

    public static int getBookId(Cursor cursor){
        return cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
    }

    public static void putBookId(ContentValues values, int bookId){
        values.put(_ID, bookId);
    }

    public static String getAuthorNames(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(AUTHOR_NAMES));
    }

    public static void putAuthorName(ContentValues values, String authorName){
        values.put(AUTHOR_NAMES, authorName);
    }

    public static String getPrice(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(PRICE));
    }

    public static void putPrice(ContentValues values, String price){
        values.put(PRICE, price);
    }

    public static String getIsbn(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(ISBN));
    }

    public static void putIsbn(ContentValues values, String isbn){
        values.put(ISBN, isbn);
    }



    //manipulation methods: get put for author attribute
    public static int getId(Cursor cursor){
        return cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
    }

    public static void putId(ContentValues values, int id){
        values.put(_ID, id);
    }


    public static String getFirstname(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(FIRSTNAME));
    }

    public static void putFirstName(ContentValues values, String firstname){
        values.put(FIRSTNAME, firstname);
    }

    public static String getLastname(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(LASTNAME));
    }

    public static void putLASTNAME(ContentValues values, String lastname){
        values.put(LASTNAME, lastname);
    }

    public static String getMiddleinitial(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(MIDDLEINITIAL));
    }

    public static void putMiddelInitial(ContentValues values, String middleInitial){
        values.put(MIDDLEINITIAL, middleInitial);
    }

    public static long getBookFk(Cursor cursor){
        return cursor.getLong(cursor.getColumnIndexOrThrow(BOOK_FK));
    }

    public static void putBookFK(ContentValues values, long book_fk){
        values.put(BOOK_FK, book_fk);
    }

}
