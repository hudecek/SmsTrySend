package com.example.gingerin.smstrysend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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

        File publicKey = new File(this.getFilesDir(), r.PUBLIC_KEY_FILE);
        ObjectInputStream inputStream = null;

        File privateKey = new File(this.getFilesDir(), r.PRIVATE_KEY_FILE);

        PrivateKey privateKey1 = null;
        PublicKey publicKey1 = null;
        // Encrypt the string using the public key
        try {
            inputStream = new ObjectInputStream(new FileInputStream(publicKey));
            publicKey1 = (PublicKey) inputStream.readObject();
            inputStream = new ObjectInputStream(new FileInputStream(privateKey));
            privateKey1 = (PrivateKey) inputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Your public key is " + publicKey1.toString(), Toast.LENGTH_LONG).show();
        System.out.println(privateKey1.toString());
        System.out.println(publicKey1.toString());

    }

}
