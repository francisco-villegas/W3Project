package com.example.francisco.w3project;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.francisco.w3project.models.SMS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecondActivity extends AppCompatActivity {

    private static final String BREAD_CRUMB = "Breadcrumb";
    private static final String TAG = "SecondActivity";

    @BindView(R.id.rSMS)
    RecyclerView rSMS;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    ArrayList<SMS> smsList = new ArrayList<>();
    SMSListAdapter randomsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //Hide toolbar
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        final PackageManager pm = this.getPackageManager();

        final SendSMS sendSms = new SendSMS();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                        !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
                    Toast.makeText(getApplicationContext(), "We are sorry, but your device cannot send/receive messages", Toast.LENGTH_LONG).show();
                }
                else
                    sendSms.show(fragmentManager,"sendSms");
            }
        });

        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();
        rSMS.setLayoutManager(layoutManager);
        rSMS.setItemAnimator(itemAnimator);


        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
            Toast.makeText(this, "We are sorry, but your device cannot send/receive messages", Toast.LENGTH_LONG).show();
        }
        else {
            GettingMessages();

            randomsListAdapter = new SMSListAdapter(smsList);
            rSMS.setAdapter(randomsListAdapter);
            randomsListAdapter.notifyDataSetChanged();
        }

        rSMS.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disall   ow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public void GettingMessages(){
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(SecondActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
            smsList.clear();
        do {
            smsList.add(new SMS("SMS From: " + smsInboxCursor.getString(indexAddress), smsInboxCursor.getString(indexBody)));
        } while (smsInboxCursor.moveToNext());
    }


    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayList<SMS> smsList2) {
        for (int i = 0; i < smsList.size(); i++) {
            smsList.add(smsList2.get(i));
            randomsListAdapter.UpdateData(smsList2.get(i));
        }
    };


    //Ordered broadcasts is used to execute the task of every broadcast with the same action depending on the priority that they have (broadcasts with more priority are executed first), I put the priority in the file AndroidManifest.xml and that's all
    //In this example I put the same action for 2 events but OrderedBroadcast1 is executed first then OrderedBroadcast2 and finally because OrderedBroadcast1 has a priority of 2 and OrderedBroadcast2 only a priority of 1
    //I made the callback of the method sendOrderedBroadcast calling a dynamic broadcast with the final string
    @OnClick({R.id.btnOrderedBroadcasts})
    public void onClick(View view) {
        Intent intent=new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction("com.br1");

        sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle=getResultExtras(true);
                String breadcrumb=bundle.getString(BREAD_CRUMB);
                breadcrumb=breadcrumb+"->"+TAG;
                Toast.makeText(context, "On Receive: "+breadcrumb, Toast.LENGTH_LONG).show();
            }
        }, null, SecondActivity.RESULT_OK,null,null);
    }
}
