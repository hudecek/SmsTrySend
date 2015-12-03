package com.example.gingerin.smstrysend;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;


import javax.crypto.Cipher;

public class RSA {
    private static Context context;
    public RSA(Context c) {
        context = c;
    }

    public static final String PRIVATE_KEY_FILE = "private.key";
    public static final String PUBLIC_KEY_FILE = "public.key";


    public void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
            File publicKeyFile = new File(context.getFilesDir(), PUBLIC_KEY_FILE);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean areKeysPresent() {

        File privateKey = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
        File publicKey = new File(context.getFilesDir(), PUBLIC_KEY_FILE);

        return privateKey.exists() && publicKey.exists();
    }


    public byte[] encrypt(byte[] text, PublicKey key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }


    public byte[] decrypt(byte[] text) {
        System.out.println("Text to decrypt: " + text);
        byte[] decryptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");

            File privateKeyFile = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(privateKeyFile));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedText = cipher.doFinal(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decryptedText;
    }

    public PublicKey getRecipientKey(String recipient) {
        PublicKey recKey = null;
        try {
            File recPublicKeyFile = new File(context.getFilesDir(), recipient);
            if(recPublicKeyFile.exists()) {
                ObjectInputStream inputStream = new ObjectInputStream(
                        new FileInputStream(recPublicKeyFile));
                recKey = (PublicKey) inputStream.readObject();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return recKey;
    }

}
