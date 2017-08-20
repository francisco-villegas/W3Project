package com.example.francisco.w3project;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.francisco.w3project.api.AmazonApi;
import com.example.francisco.w3project.api.AmazonCalls;
import com.example.francisco.w3project.models.AmazonBook;
import com.example.francisco.w3project.utils.PaginationScrollListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = -1; // -1 remove the limit of pages with -1 this should be > 0
    private int currentPage = PAGE_START;
    private int limit = 20;

    private AmazonCalls amazonCalls;

    ArrayList<AmazonBook> amazonBook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);

        adapter = new PaginationAdapter(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        amazonCalls = AmazonApi.getClient().create(AmazonCalls.class);

        loadFirstPage();

    }


    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        callAmazonApi().enqueue(new Callback<ArrayList<AmazonBook>>() {
            @Override
            public void onResponse(Call<ArrayList<AmazonBook>> call, Response<ArrayList<AmazonBook>> response) {
                // Got data. Send it to adapter

                amazonBook = fetchBooksFirst(response);
                ArrayList<AmazonBook> amazonBookSub = fetchBooksSub(amazonBook,currentPage,limit);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(amazonBookSub);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ArrayList<AmazonBook>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * @param response extracts ArrayList<{@link AmazonBook>} from response
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

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        adapter.removeLoadingFooter();
        isLoading = false;

        ArrayList<AmazonBook> amazonBookSub = fetchBooksSub(amazonBook,currentPage,limit);
        adapter.addAll(amazonBookSub);

        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }


    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<ArrayList<AmazonBook>> callAmazonApi() {
        return amazonCalls.getAmazonBook();
    }


}
