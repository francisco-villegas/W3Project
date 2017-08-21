package com.example.francisco.w3project;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.example.francisco.w3project.broadcasts.DynamicBroadcast;
import com.example.francisco.w3project.models.AmazonBook;
import com.example.francisco.w3project.services.GetBooksIntentService;
import com.example.francisco.w3project.utils.PaginationScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_recycler)
    RecyclerView rv;

    @BindView(R.id.main_progress)
    ProgressBar progressBar;

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = -1; // -1 remove the limit of pages with -1 this should be > 0
    private int currentPage = PAGE_START;
    private int limit = 50;
    IntentFilter intentFilter;

    ArrayList<AmazonBook> amazonBook;
    DynamicBroadcast dynamicBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide toolbar
        getSupportActionBar().hide();

        ButterKnife.bind(this);

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

        rv.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disall   ow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        dynamicBroadcast = new DynamicBroadcast();

        loadFirstPage();
    }

    public void loadFirstPage(){
        Intent intent = new Intent(this, GetBooksIntentService.class);
        intent.setAction("loadFirstPage");
        intent.putExtra("currentPage",currentPage);
        intent.putExtra("limit",limit);
        intent.putExtra("TOTAL_PAGES",TOTAL_PAGES);
        startService(intent);
    }

    private void loadNextPage(){
        Intent intent = new Intent(this, GetBooksIntentService.class);
        intent.setAction("loadNextPage");
        intent.putExtra("currentPage",currentPage);
        intent.putExtra("limit",limit);
        intent.putExtra("TOTAL_PAGES",TOTAL_PAGES);
//        intent.putParcelableArrayListExtra("AmazonBook",amazonBook);
        startService(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        intentFilter = new IntentFilter();
        intentFilter.addAction("loadFirstPage");
        intentFilter.addAction("loadNextPage");
        intentFilter.addAction("AmazonBook");
        intentFilter.addAction("register");
        intentFilter.addAction("unregister");
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_APP_ERROR);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        intentFilter.addAction(Intent.ACTION_CAMERA_BUTTON);
        intentFilter.addAction(Intent.ACTION_USER_BACKGROUND);
        intentFilter.addAction(Intent.ACTION_SET_WALLPAPER);

        registerReceiver(dynamicBroadcast,intentFilter);

        EventBus.getDefault().register(this);

        Intent intent = new Intent();
        intent.setAction("register");
        sendBroadcast(intent);
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        Intent intent = new Intent();
        intent.setAction("unregister");
        sendBroadcast(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent msg) {
        amazonBook = msg.getAmazonBook();
        ArrayList<AmazonBook> amazonBookSub2 = msg.getAmazonBookSub();
        boolean pages_validation2 = msg.isPages_validation();

        switch(msg.getAction()){
            case "loadFirstPage":

                progressBar.setVisibility(View.GONE);
                adapter.addAll(amazonBookSub2);
                if (pages_validation2) adapter.addLoadingFooter();
                else isLastPage = true;

                break;
            case "loadNextPage":


                adapter.removeLoadingFooter();
                isLoading = false;
                adapter.addAll(amazonBookSub2);

                if (pages_validation2) adapter.addLoadingFooter();
                else isLastPage = true;

                break;
        }
    };

    @OnClick({R.id.btn})
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn:
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                break;
        }
    }
}