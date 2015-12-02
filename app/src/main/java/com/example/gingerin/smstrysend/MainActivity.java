package com.example.gingerin.smstrysend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RSA r = new RSA(getApplicationContext());
        if (!r.areKeysPresent()) {
            r.generateKey();
            Toast.makeText(this, "Keys generated", Toast.LENGTH_LONG).show();
        }
    }

    public void goToInbox(View view) {
        Intent intent = new Intent(this, ReceiveSmsActivity.class);
        startActivity(intent);
    }

    public void goToCompose(View view) {
        Intent intent = new Intent(this, SendSmsActivity.class);
        startActivity(intent);
    }

    public void goToKeyMgmt(View view) {
        Intent intent = new Intent(this, KeyMgmt.class);
        startActivity(intent);
    }



}