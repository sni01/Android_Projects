package edu.stevens.cs522.BookstoreWithEntityManager.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;
import edu.stevens.cs522.BookstoreWithEntityManager.entities.Author;
import edu.stevens.cs522.BookstoreWithEntityManager.entities.Book;

/**
 * Created by nisha0634 on 2/8/15.
 */
public class CartDbAdapter{
    private static final String DATABASE_NAME = "cart.db";
    private static final String BOOK_TABLE = "books";
    private static final String AUTHOR_TABLE = "authors";
    private static final int DATABASE_VERSION = 1;

    //columns strings for where
//    private static final String _ID = "_id";
//    private static final String TITLE = "title";
//    public static final String AUTHOR_NAMES = "author_names";
//    public static final String PRICE = "price";
//    public static final String ISBN = "isbn";

//    public static final String FIRSTNAME = "firstname";
//    public static final String LASTNAME = "lastname";
//    public static final String MIDDLEINITIAL = "middleinitial";

    //database variables
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

    public CartDbAdapter(Context _context){
        this.context = _context;
        this.helper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //open database
    public CartDbAdapter open() throws SQLException{
        db = helper.getWritableDatabase();
        return this;
    }

    //close database
    public void close(){
        db.close();
    }

    //fetch all books from db
    public Cursor fetchAllBooks(){
        //String[] projections = {BookContract.TITLE, BookContract.PRICE , BookContract.ISBN, BookContract.AUTHOR_NAMES};
        //return db.(BOOK_TABLE, projections, null, null, null, null, null);
        String[] selections = {};
        return db.rawQuery(SELECT_ALL_BOOKS, selections);
    }

    //fetch specific book from db, for shopping cart
    public Book fetchBook(long rowId){
        String[] projections = {Contract._ID, Contract.TITLE, Contract.PRICE , Contract.ISBN, Contract.AUTHOR_NAMES};
        String SELECT_BOOK = "SELECT " + BOOK_TABLE + "." + Contract._ID + ", " + Contract.TITLE + ", " + Contract.PRICE + ", " + Contract.ISBN + ", GROUP_CONCAT(" + Contract.FIRSTNAME + ", '|') as author_names FROM " + BOOK_TABLE + " LEFT OUTER JOIN " + AUTHOR_TABLE + " ON " + Contract._ID + " = " + Contract.BOOK_FK  +" GROUP BY " + Contract._ID + ", " + Contract.TITLE + ", " + Contract.PRICE + ", " + Contract.ISBN + " HAVING " + Contract._ID + " = " + rowId;
        //Cursor cursor = db.query(BOOK_TABLE, projections, where, null, null, null, null);
        Cursor cursor = db.rawQuery(SELECT_BOOK, null);
        if(cursor.getCount() == 0 || cursor.getCount() >= 2) return null;
        else{
            Book book = new Book(cursor);
            return book;
        }
    }

    //insert book into db, for adding book
    public void persist(Book book) throws SQLException{
        //insert book entry
        ContentValues bookValues = new ContentValues();
        book.writeToProvider(bookValues);
        long rowId = db.insert(BOOK_TABLE, null, bookValues);
        //insert each author entry
        Author[] authors = book.authors;
        for(int i = 0; i < authors.length; i++){
            //each author insert into database
            ContentValues authorValues = new ContentValues();
            authors[i].writeToProvider(authorValues);
            Contract.putBookFK(authorValues, rowId);
            db.insert(AUTHOR_TABLE, null, authorValues);
        }
    }

    //delete specific book, for deleting function
    public boolean delete(Book book){
        String where_books = Contract._ID + "=" + book.id; //but we should cascade delete authors
        String where_authors = Contract.BOOK_FK + " = " + book.id;
        return db.delete(BOOK_TABLE, where_books, null) > 0 && db.delete(AUTHOR_TABLE, where_authors, null) > 0;
    }

    //delete all book, for checkout
    public boolean deleteAll(){
        int deleteBookRes = db.delete(BOOK_TABLE, null, null);
        int deleteAuthorRes = db.delete(AUTHOR_TABLE, null, null);
        return deleteBookRes > 0 && deleteAuthorRes > 0;
    }




    //private class for db helper
    private class DatabaseHelper extends SQLiteOpenHelper{
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
