package com.example.gingerin.smstrysend;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReceiveSmsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static ReceiveSmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;

    public static ReceiveSmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_sms);
        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);


        if (ContextCompat.checkSelfPermission(this, //this?
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, //this?
                    Manifest.permission.RECEIVE_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, //this?
                    Manifest.permission.READ_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            refreshSmsInbox();
        }

    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        long timeMillis = smsInboxCursor.getColumnIndex("date");
        Date date = new Date(timeMillis);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.US);
        String dateText = format.format(date);

        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = smsInboxCursor.getString(indexAddress) +" at "+
                    "\n" + smsInboxCursor.getString(indexBody) +dateText+ "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
        smsInboxCursor.close();
    }

    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
