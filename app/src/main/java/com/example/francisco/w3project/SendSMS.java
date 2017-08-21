package com.example.francisco.w3project;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 8/13/2017.
 */

public class SendSMS extends DialogFragment {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final String SENT = "Message Sent!!!!";
    private static final String DELIVERED = "Message Delivered";

    @Nullable
    @BindView(R.id.etMessage)
    EditText etMessage;

    @Nullable
    @BindView(R.id.etNumber)
    EditText etNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_sm, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ButterKnife.bind(this, view);

        getDialog().setTitle("Send Message");

        return view;
    }

    @OnClick({R.id.btnSMS})
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSMS:
                sendMessage();
                dismiss();
                etMessage.setText("");
                showNotification();
        }
    }

    private void sendMessage() {
        // Get the default instance of the SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(etNumber.getText().toString(), null, etMessage.getText().toString(), null, null);
        Toast.makeText(getActivity(), "Your sms was successfully sent, also a notification was created, go an check it!",
                Toast.LENGTH_LONG).show();

    }


    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon(R.drawable.ic_message);
        builder.setContentTitle("Message sent");
        builder.setContentText("A message has being sent");
//        Intent intent = new Intent(getActivity(), ActivityAfterMSG.class);
//        intent.putExtra("key1",etMessage.getText().toString());
//        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,intent,0);
//        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
        Toast.makeText(getActivity(), " Alert.. Notification Sent!!!", Toast.LENGTH_SHORT).show();
    }
}
