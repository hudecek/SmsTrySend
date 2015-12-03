package com.example.gingerin.smstrysend;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Base64;

import java.security.PublicKey;

public class SendSmsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    Button sendSMSBtn;
    EditText toPhoneNumberET;
    EditText smsMessageET;
    String encryptedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        sendSMSBtn = (Button) findViewById(R.id.sendSMSBtn);
        toPhoneNumberET = (EditText) findViewById(R.id.toPhoneNumberET);
        smsMessageET = (EditText) findViewById(R.id.smsMessageET);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, //this?
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (
                    ActivityCompat.shouldShowRequestPermissionRationale(this, //this?
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.



            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            sendSMSBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMS();
                    Toast.makeText(getApplicationContext(), "SMS Clicked", Toast.LENGTH_LONG).show();
                }
            });
        }






    }

    protected void sendSMS() {
        String toPhoneNumber = toPhoneNumberET.getText().toString();
        String smsMessage = smsMessageET.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    sendSMSBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendSMS();
                            Toast.makeText(getApplicationContext(), "SMS Clicked", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }



    public void encrypt(View view) {
        RSA r = new RSA(getApplicationContext());

        String unEncrypted = smsMessageET.getText().toString();
        String recipient = toPhoneNumberET.getText().toString();

        PublicKey key = r.getRecipientKey(recipient + ".key");
        if(key == null) {
            Toast.makeText(this, "No public key specified for number " + recipient + ". Add key in Key Management.", Toast.LENGTH_LONG).show();
        } else {
            byte[] encByte = r.encrypt(unEncrypted.getBytes(), key);
            try {

                byte[] encoded = Base64.encode(encByte, Base64.DEFAULT);

                String textMessage = "";
                textMessage += new String(encoded);

                smsMessageET.setText(textMessage);
                System.out.println(textMessage);

            /* Works
                String something = "Text";
                byte[] encryptedSth = r.encrypt(something.getBytes(), key);
                byte[] decryptedSth = r.decrypt(encryptedSth);
                System.out.println("Before: " + something + " After: " + new String(decryptedSth));
            */

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
