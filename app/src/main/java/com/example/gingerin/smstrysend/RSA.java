package com.example.gingerin.smstrysend;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class RSA {
    private static Context context;
    public RSA(Context c) {
        context = c;
    }

    /**
     * String to hold name of the encryption algorithm.
     */

    /**
     * String to hold the name of the private key file.
     */
    public static final String PRIVATE_KEY_FILE = "private.key";

    /**
     * String to hold name of the public key file.
     */
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


    public byte[] encrypt(String text) {
        byte[] cipherText = null;
        try {
            File publicKeyFile = new File(context.getFilesDir(), PUBLIC_KEY_FILE);
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(publicKeyFile));
            final PublicKey key = (PublicKey) inputStream.readObject();

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");

            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes("ISO-8859-1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }


    public byte[] decrypt(String text) {
        byte[] decryptedText = null;
        try {
            byte[] textByte = text.getBytes("ISO-8859-1");
            //String str = new String(decryptedText, StandardCharsets.UTF_8);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");

            File privateKeyFile = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(privateKeyFile));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedText = cipher.doFinal(textByte);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decryptedText;
    }

    /**
     * Test the EncryptionUtil
     */
    /*
    public static void main(String[] args) {

        try {

            // Check if the pair of keys are present else generate those.
            if (!areKeysPresent()) {
                // Method generates a pair of keys using the RSA algorithm and stores it
                // in their respective files
                generateKey();
            }

            final String originalText = "Text to be encrypted ";
            ObjectInputStream inputStream = null;

            // Encrypt the string using the public key
            inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            final PublicKey publicKey = (PublicKey) inputStream.readObject();
            final byte[] cipherText = encrypt(originalText, publicKey);

            // Decrypt the cipher text using the private key.
            inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            final String plainText = decrypt(cipherText, privateKey);

            // Printing the Original, Encrypted and Decrypted Text
            System.out.println("Original: " + originalText);
            System.out.println("Encrypted: " +cipherText.toString());
            System.out.println("Decrypted: " + plainText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
