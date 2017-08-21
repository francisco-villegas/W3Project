package com.example.francisco.w3project.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.francisco.w3project.models.SMS;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class BroadcastSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            ArrayList<SMS> smsList = new ArrayList<>();

            for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String phone = "SMS From: " + smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody();

                smsList.add(new SMS(phone,message));

                Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();
            }
            EventBus.getDefault().post(smsList);
        }
    }

}