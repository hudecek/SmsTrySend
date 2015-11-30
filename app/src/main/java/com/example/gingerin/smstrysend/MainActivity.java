package com.example.gingerin.smstrysend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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