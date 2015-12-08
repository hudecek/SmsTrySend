package com.example.gingerin.smstrysend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsListener extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] pdu = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            SmsMessage[] messages = new SmsMessage[pdu.length];
            String smsBody = "";
            for (int i = 0; i < pdu.length; ++i) {
                //SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                messages[i] = SmsMessage.createFromPdu((byte[]) pdu[i]);
                //String smsBody = smsMessage.getMessageBody();



            }
            SmsMessage sms = messages[0];
            try {
                if (messages.length == 1 || sms.isReplace()) {
                    smsBody = sms.getDisplayMessageBody();
                } else {
                    StringBuilder bodyText = new StringBuilder();
                    for (int i = 0; i < messages.length; i++) {
                        bodyText.append(messages[i].getMessageBody());
                    }
                    smsBody = bodyText.toString();
                }
            } catch (Exception e) {

            }
            String address = messages[0].getOriginatingAddress();
            long timeMillis = messages[0].getTimestampMillis();

            Date date = new Date(timeMillis);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.US);
            String dateText = format.format(date);
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            smsMessageStr += address +" at "+"\t"+ dateText + "\n";
            smsMessageStr += smsBody + "\n";

            //this will update the UI with message
            ReceiveSmsActivity inst = ReceiveSmsActivity.instance();
            inst.updateList(smsMessageStr);
        }
    }
}