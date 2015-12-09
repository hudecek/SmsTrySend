package com.example.gingerin.smstrysend;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.PublicKey;

public class SendSmsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    Button sendSMSBtn;
    Button encryptBtn;
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
        encryptBtn = (Button) findViewById(R.id.encryptBtn);


        encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encrypt(v);
            }
        });

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

//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            if (smsMessage.length() > 160){
//                ArrayList<String> messages = smsManager.divideMessage(smsMessage);
//                smsManager.sendMultipartTextMessage(toPhoneNumber, null, messages, null, null); //TODO THis is NEW. Maybe rejoin upon receipt?
//            }else {
//            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
//            Toast.makeText(getApplicationContext(), "SMS Sent", Toast.LENGTH_LONG).show();}
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "ERROR, SMS Not Sent", Toast.LENGTH_LONG).show();
//
//        }
        byte[] decodeTest = Base64.decode(smsMessage, Base64.DEFAULT);

        short SMS_PORT = 8901; //you can use a different port if you'd like. I believe it just has to be an int value.
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(toPhoneNumber, null, SMS_PORT, decodeTest, null, null);
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

        PublicKey key = r.readPublicKeyFromFile(recipient + ".key"); //Use our own public key for testing
        //PublicKey key = r.readPublicKeyFromFile("public.key"); //TODO Clean this up
        if(key == null) {
            Toast.makeText(this, "No public key specified for number " + recipient + ". Add key in Key Management.", Toast.LENGTH_LONG).show();
        } else {
            byte[] encByte = r.rsaEncrypt(unEncrypted.getBytes(), recipient + ".key");
            try {

                String encoded = Base64.encodeToString(encByte, Base64.DEFAULT);


                String textMessage = "";
                textMessage += encoded;  //Change from encoded to encByte
                //Toast.makeText(this, "Encrypted text byte length is " + encByte.length, Toast.LENGTH_LONG).show();
                smsMessageET.setText(textMessage);
                //System.out.println(textMessage);
                byte[] decodeTest = Base64.decode(textMessage, Base64.DEFAULT);
                //Toast.makeText(this, "Decoded text byte length is " + decodeTest.length, Toast.LENGTH_LONG).show();
                //String decodeStringTest = Base64.encodeToString(decodeTest, Base64.DEFAULT);
                //Toast.makeText(this, decodeStringTest, Toast.LENGTH_LONG).show();
                //Now test decryption


              // byte[] decryptTest =  r.decrypt(decodeTest);
               // String decryptedText = new String(decryptTest);
//                byte[] decryptTest2 = r.rsaDecrypt(decodeTest);
//                String decryptedText2 = new String(decryptTest2);

                //Toast.makeText(this, decryptedText, Toast.LENGTH_LONG).show();
                //Toast.makeText(this, decryptedText2, Toast.LENGTH_LONG).show();
//                if (unEncrypted.getBytes() == decryptTest2){
//                    Toast.makeText(this, "unEncrypted and decrypted are equal", Toast.LENGTH_LONG).show();
//
//                }else {
//                    Toast.makeText(this, "unEncrypted and decrypted are unequal", Toast.LENGTH_LONG).show();
//                }








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
