package edu.stevens.cs522.BookstoreWithEntityManager.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;


public class Book implements Parcelable{
	
	// TODO Modify this to implement the Parcelable interface.
	
	// TODO redefine toString() to display book title and price (why?).

	public int id;

	public String title;
	
	public Author[] authors;

    public String author_names; //for rendering authors without parsing back to author objects
	
	public String isbn;
	
	public String price;

	public Book(String title, Author[] author, String isbn, String price) {
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

    public int describeContents() {
        return 0;
    }


    public static final Creator<Book> CREATOR = new Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        Object[] tmp = in.readArray(Author.class.getClassLoader());
        authors = new Author[tmp.length];
        for(int i = 0; i < tmp.length; i++){
            authors[i] = (Author)tmp[i];
        }
        author_names = in.readString();
        isbn = in.readString();
        price = in.readString();
    }


    public void writeToParcel(Parcel out, int flags) {
        // TODO Auto-generated method stub
        out.writeInt(id);
        out.writeString(title);
        out.writeArray(authors);
        out.writeString(author_names);
        out.writeString(isbn);
        out.writeString(price);
    }

    public String toString(){
        return "title: " + title + " price: " + price;
    }


    //for database methods
    public Book(Cursor cursor){
        this.title = Contract.getTitle(cursor);
        this.author_names = Contract.getAuthorNames(cursor);
        this.price = Contract.getPrice(cursor);
        this.isbn = Contract.getIsbn(cursor);
        this.id = Contract.getBookId(cursor);
        this.authors = new Author[1];
    }

    public void writeToProvider(ContentValues values){
        Contract.putTitle(values, this.title);
        Contract.putIsbn(values, this.isbn);
        Contract.putPrice(values, this.price);
    }

}