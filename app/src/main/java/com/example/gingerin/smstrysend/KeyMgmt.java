package com.example.gingerin.smstrysend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;

public class KeyMgmt extends AppCompatActivity {

    RSA r = new RSA(this);

    private Button retrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_mgmt);
        retrieve =  (Button) findViewById(R.id.retrieveKey);

        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveKey();
            }
        });


    }



    protected void retrieveKey(){
        r.generateKey();
        File publicKey = new File(this.getFilesDir(), r.PUBLIC_KEY_FILE);
        ObjectInputStream inputStream = null;


        PublicKey publicKey1 = null;
        // Encrypt the string using the public key
        try {
            inputStream = new ObjectInputStream(new FileInputStream(publicKey));
            publicKey1 = (PublicKey) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Your public key is " + publicKey1.toString(), Toast.LENGTH_LONG).show();

    }

}
