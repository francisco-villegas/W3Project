package com.example.francisco.w3project;

import com.example.francisco.w3project.models.AmazonBook;

import java.util.ArrayList;

/**
 * Created by FRANCISCO on 08/08/2017.
 */

public class MessageEvent {
    String action;
    ArrayList<AmazonBook> amazonBook, amazonBookSub;
    boolean pages_validation;

    public MessageEvent(String action, ArrayList<AmazonBook> amazonBook, ArrayList<AmazonBook> amazonBookSub, boolean pages_validation) {
        this.action = action;
        this.amazonBook = amazonBook;
        this.amazonBookSub = amazonBookSub;
        this.pages_validation = pages_validation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ArrayList<AmazonBook> getAmazonBook() {
        return amazonBook;
    }

    public void setAmazonBook(ArrayList<AmazonBook> amazonBook) {
        this.amazonBook = amazonBook;
    }

    public ArrayList<AmazonBook> getAmazonBookSub() {
        return amazonBookSub;
    }

    public void setAmazonBookSub(ArrayList<AmazonBook> amazonBookSub) {
        this.amazonBookSub = amazonBookSub;
    }

    public boolean isPages_validation() {
        return pages_validation;
    }

    public void setPages_validation(boolean pages_validation) {
        this.pages_validation = pages_validation;
    }
}
