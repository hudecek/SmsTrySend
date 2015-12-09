package com.example.gingerin.smstrysend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class KeyMgmt extends AppCompatActivity {

    RSA r = new RSA(this);

    private Button retrieve;
    private Button addKey;
    private EditText number;
    private EditText exponent;
    private EditText modulus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_mgmt);
        retrieve =  (Button) findViewById(R.id.retrieveKey);
        addKey = (Button) findViewById(R.id.addKey);
        number = (EditText) findViewById(R.id.number);
        exponent = (EditText) findViewById(R.id.exponent);
        modulus = (EditText) findViewById(R.id.modulus);


        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveKey();
            }
        });

        addKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addKey( number.getText().toString(), exponent.getText().toString(), modulus.getText().toString());
            }
        });


    }

    private void addKey(String contact, String exponent, String modulus) {

        BigInteger exp = new BigInteger(exponent);
        BigInteger mod = new BigInteger(modulus);

        String filename = contact + ".key";

        RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pub = factory.generatePublic(spec);
            File contactKeyFile = new File(this.getFilesDir(), filename);
            if (contactKeyFile.exists()){
                Toast.makeText(this, "Key already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (contactKeyFile.getParentFile() != null) {
                contactKeyFile.getParentFile().mkdirs();
            }
            contactKeyFile.createNewFile();
            Toast.makeText(this, "Key saved in " + contactKeyFile.toString(), Toast.LENGTH_SHORT).show();
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(contactKeyFile));
            publicKeyOS.writeObject(pub);
            publicKeyOS.close();


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }






    }


    protected void retrieveKey(){

      PublicKey myKey = r.readPublicKeyFromFile("public.key");

        Toast.makeText(this, "Your public key is " + myKey.toString(), Toast.LENGTH_LONG).show();

    }

}
