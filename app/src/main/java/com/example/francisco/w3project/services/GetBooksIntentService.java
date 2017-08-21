package com.example.francisco.w3project.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.francisco.w3project.api.AmazonApi;
import com.example.francisco.w3project.api.AmazonCalls;
import com.example.francisco.w3project.models.AmazonBook;
import com.example.francisco.w3project.utils.PaginationScrollListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetBooksIntentService extends IntentService {

    private static final String TAG = "GetBooksIntentService";

    public GetBooksIntentService() {
        super("GetBooksIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int currentPage = intent.getIntExtra("currentPage",1);
        int limit = intent.getIntExtra("limit",1);
        int TOTAL_PAGES = intent.getIntExtra("TOTAL_PAGES",1);
        switch(intent.getAction()){
            case "loadFirstPage":
                loadFirstPage(currentPage, limit, TOTAL_PAGES);
                break;
            case "loadNextPage":
//                ArrayList<AmazonBook> amazonBook = intent.getParcelableExtra("AmazonBook");
                loadNextPage(currentPage, limit, TOTAL_PAGES);
                break;
        }

    }

    public void loadFirstPage(final int currentPage, final int limit, final int TOTAL_PAGES){
        Log.d(TAG, "loadFirstPage: ");

        //init service and load data
        AmazonCalls amazonCalls = AmazonApi.getClient().create(AmazonCalls.class);

        callAmazonApi(amazonCalls).enqueue(new Callback<ArrayList<AmazonBook>>() {
            @Override
            public void onResponse(Call<ArrayList<AmazonBook>> call, Response<ArrayList<AmazonBook>> response) {
                // Got data. Send it to adapter

                ArrayList<AmazonBook> amazonBook = fetchBooksFirst(response);

                //Save file in sharedPreferences we cant send the entire arraylist u.u 1mb allowed only
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();

                String json = gson.toJson(amazonBook);

                editor.putString("AmazonBookSub", json);
                editor.commit();


                ArrayList<AmazonBook> amazonBookSub = fetchBooksSub(amazonBook,currentPage,limit);

                Intent intent = new Intent();
                intent.setAction("loadFirstPage");
                intent.putParcelableArrayListExtra("AmazonBookSub",amazonBookSub);
                intent.putExtra("pages_validation",(currentPage != TOTAL_PAGES));
                sendBroadcast(intent);

            }

            @Override
            public void onFailure(Call<ArrayList<AmazonBook>> call, Throwable t) {
                //t.printStackTrace();
                try {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Gson gson = new Gson();
                    String json = sharedPrefs.getString("AmazonBookSub", null);
                    Type type = new TypeToken<ArrayList<AmazonBook>>() {
                    }.getType();
                    ArrayList<AmazonBook> amazonBook = gson.fromJson(json, type);

                    ArrayList<AmazonBook> amazonBookSub = fetchBooksSub(amazonBook,currentPage,limit);

                    Intent intent = new Intent();
                    intent.setAction("loadFirstPage");
                    intent.putParcelableArrayListExtra("AmazonBookSub",amazonBookSub);
                    intent.putExtra("pages_validation",(currentPage != TOTAL_PAGES));
                    sendBroadcast(intent);
                }catch(Exception ex){}
            }
        });

    }

    private void loadNextPage(int currentPage, final int limit, final int TOTAL_PAGES) {
        Log.d(TAG, "loadNextPage: " + currentPage);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("AmazonBookSub", null);
        Type type = new TypeToken<ArrayList<AmazonBook>>() {}.getType();
        ArrayList<AmazonBook> amazonBook = gson.fromJson(json, type);

        ArrayList<AmazonBook> amazonBookSub = fetchBooksSub(amazonBook,currentPage,limit);

        Intent intent = new Intent();
        intent.setAction("loadNextPage");
        intent.putParcelableArrayListExtra("AmazonBookSub",amazonBookSub);
        intent.putExtra("pages_validation",(currentPage != TOTAL_PAGES));
        sendBroadcast(intent);
    }


    /**
     * @param response extracts ArrayList<{@link AmazonBook >} from response
     * @return
     */
    private ArrayList<AmazonBook> fetchBooksFirst(Response<ArrayList<AmazonBook>> response) {
        ArrayList<AmazonBook> amazonBook = response.body();
        return amazonBook;
    }

    private ArrayList<AmazonBook> fetchBooksSub(ArrayList<AmazonBook> amazonBook, int currentPage, int limit) {
        ArrayList<AmazonBook> amazonBooksub = new ArrayList<AmazonBook> (amazonBook.subList((currentPage-1)*limit,currentPage*limit));
        return amazonBooksub;
    }

    /**
     * Performs a Retrofit call to the amazon books API.
     * Same API call for Pagination.
     * As {@link} currentPage will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<ArrayList<AmazonBook>> callAmazonApi(AmazonCalls amazonCalls) {
        return amazonCalls.getAmazonBook();
    }
}
