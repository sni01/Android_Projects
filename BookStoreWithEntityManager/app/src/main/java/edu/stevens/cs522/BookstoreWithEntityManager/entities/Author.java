package edu.stevens.cs522.BookstoreWithEntityManager.entities;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.BookstoreWithEntityManager.contracts.Contract;

public class Author implements Parcelable{
	
	// TODO Modify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!
	public String firstName;
	
	public String middleInitial;
	
	public String lastName;

    public int describeContents() {
        return 0;
    }

    public Author(String firstName, String middleInitial, String lastName){
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
    }

    //for content provider useness
    public Author(String name){
        String[] parts = name.split(" ");
        if(parts.length == 1){
            lastName = parts[0];
            firstName = "";
            middleInitial = "";
        }

        if(parts.length == 2){
            firstName = parts[0];
            lastName = parts[1];
            middleInitial = "";
        }
        if(parts.length == 3){
            firstName = parts[0];
            middleInitial = parts[1];
            lastName = parts[2];
        }
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    private Author(Parcel in) {
        firstName = in.readString();
        middleInitial = in.readString();
        lastName = in.readString();
    }


    public void writeToParcel(Parcel out, int flags) {
        // TODO Auto-generated method stub
        out.writeString(firstName);
        out.writeString(middleInitial);
        out.writeString(lastName);
    }

    public void writeToProvider(ContentValues values){
        Contract.putFirstName(values, this.firstName);
        Contract.putLASTNAME(values, this.lastName);
        Contract.putMiddelInitial(values, this.middleInitial);
    }

}
