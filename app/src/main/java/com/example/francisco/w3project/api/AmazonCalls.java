package com.example.francisco.w3project.api;


import com.example.francisco.w3project.models.AmazonBook;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AmazonCalls {
    public static final String PATH = "books.json";

    @GET(PATH)
    Call<ArrayList<AmazonBook>> getAmazonBook(
    );

}
