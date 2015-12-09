package com.example.gingerin.smstrysend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsListener extends BroadcastReceiver {

    RSA r = new RSA();

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        String recMsgString = "";
        String fromAddress = "";
        String smsBody = "";
        SmsMessage recMsg = null;
        byte[] data = null;
        if (bundle != null) {
            Object[] pdu = (Object[]) bundle.get(SMS_BUNDLE);
            for (int i = 0; i < pdu.length; ++i) {
                //SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                recMsg = SmsMessage.createFromPdu((byte[]) pdu[i]);
                //String smsBody = smsMessage.getMessageBody();
                try {
                    data = recMsg.getUserData();
                } catch (Exception e){

                }
                if (data != null){
                    byte[] sixfour = Base64.decode(data, Base64.DEFAULT);
                   byte[] decoded = r.decrypt(sixfour);
                    smsBody = new String(decoded);

                } else {
                    Toast.makeText(ReceiveSmsActivity.instance().getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }


            }

            String address = recMsg.getOriginatingAddress();
            long timeMillis =recMsg.getTimestampMillis();

            Date date = new Date(timeMillis);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.US);
            String dateText = format.format(date);


            String smsMessageStr = "";

            smsMessageStr += address +" at "+"\t"+ dateText + "\n";
            smsMessageStr += smsBody + "\n";

            //this will update the UI with message
            ReceiveSmsActivity inst = ReceiveSmsActivity.instance();
            inst.updateList(smsBody); //change from smsMessageStr to smsBody
        }
    }
}