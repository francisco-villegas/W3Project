
package com.example.francisco.w3project.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmazonBook implements Parcelable{

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public AmazonBook() {}

    protected AmazonBook(Parcel in) {
        title = in.readString();
        author = in.readString();
        imageURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(imageURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AmazonBook> CREATOR = new Creator<AmazonBook>() {
        @Override
        public AmazonBook createFromParcel(Parcel in) {
            return new AmazonBook(in);
        }

        @Override
        public AmazonBook[] newArray(int size) {
            return new AmazonBook[size];
        }
    };

}
