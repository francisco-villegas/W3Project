package com.example.francisco.w3project.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.francisco.w3project.MessageEvent;
import com.example.francisco.w3project.models.AmazonBook;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by FRANCISCO on 15/08/2017.
 */

public class DynamicBroadcast extends BroadcastReceiver{

    TextView etchanged;
    RecyclerView rvRandomsList;

    private static final String TAG = "DynamicBroadcast";

    public DynamicBroadcast() {}

    public DynamicBroadcast(TextView etchanged, RecyclerView rvRandomsList) {
        this.etchanged = etchanged;
        this.rvRandomsList = rvRandomsList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "I am a dynamic receiver", Toast.LENGTH_SHORT).show();
        try{etchanged.setText(intent.getAction().toString());}catch(Exception ex){}
        switch (intent.getAction()) {
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                Toast.makeText(context, "Airplane changed", Toast.LENGTH_LONG).show();
                //etchanged.setText("Airplane changed");
                break;
            case Intent.ACTION_POWER_CONNECTED:
                Toast.makeText(context, "Power connected", Toast.LENGTH_LONG).show();
//                etchanged.setText("Power connected");
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                Toast.makeText(context, "Power disconnected", Toast.LENGTH_LONG).show();
//                etchanged.setText("Power disconnected");
                break;
            case Intent.ACTION_SCREEN_ON:
                Toast.makeText(context, "Screen on", Toast.LENGTH_LONG).show();
//                etchanged.setText("Screen on");
                break;
            case Intent.ACTION_SCREEN_OFF:
                Toast.makeText(context, "Screen off", Toast.LENGTH_LONG).show();
//                etchanged.setText("Screen off");
                break;
            case Intent.ACTION_SET_WALLPAPER:
                Toast.makeText(context, "Set wallpaper", Toast.LENGTH_LONG).show();
//                etchanged.setText("Set wallpaper");
                break;
            case "loadFirstPage":
                Log.d(TAG, "onReceive: loadFirstPage");

                ArrayList<AmazonBook> amazonBook = intent.getParcelableArrayListExtra("AmazonBook");
                ArrayList<AmazonBook> amazonBookSub = intent.getParcelableArrayListExtra("AmazonBookSub");
                boolean pages_validation = intent.getBooleanExtra("pages_validation",true);

                EventBus.getDefault().post(new MessageEvent("loadFirstPage",amazonBook, amazonBookSub, pages_validation));

                break;

            case "loadNextPage":
                Log.d(TAG, "onReceive: loadNextPage");

                ArrayList<AmazonBook> amazonBook2 = intent.getParcelableArrayListExtra("AmazonBook");
                ArrayList<AmazonBook> amazonBookSub2 = intent.getParcelableArrayListExtra("AmazonBookSub");
                boolean pages_validation2 = intent.getBooleanExtra("pages_validation",true);

                EventBus.getDefault().post(new MessageEvent("loadNextPage",amazonBook2, amazonBookSub2, pages_validation2));

                break;

            case "register":
//                EventBus.getDefault().register(this);
                break;
            case "unregister":
//                EventBus.getDefault().unregister(this);
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MessageEvent msg) {
//
//    }
}
