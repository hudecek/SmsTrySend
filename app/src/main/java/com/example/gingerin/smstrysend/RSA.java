package com.example.gingerin.smstrysend;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
            final KeyPair kp = keyGen.genKeyPair();

            Key publicKey = kp.getPublic();
            Key privateKey = kp.getPrivate();

            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                    RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                    RSAPrivateKeySpec.class);

            saveToFile(PUBLIC_KEY_FILE, pub.getModulus(),
                    pub.getPublicExponent());
            saveToFile(PRIVATE_KEY_FILE, priv.getModulus(),
                    priv.getPrivateExponent());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveToFile(String fileName,
                           BigInteger mod, BigInteger exp) throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        ObjectOutputStream oout = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)));
        try {
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    public boolean areKeysPresent() {

        File privateKey = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
        File publicKey = new File(context.getFilesDir(), PUBLIC_KEY_FILE);

        return privateKey.exists() && publicKey.exists();
    }

    public PrivateKey getPrivateKey() {


        //File publicKey = new File(context.getFilesDir(), PUBLIC_KEY_FILE);
        PrivateKey privKey = null;
        try {
            File privateKey = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
            if(privateKey.exists()) {
                ObjectInputStream inputStream = new ObjectInputStream(
                        new FileInputStream(privateKey));
                privKey = (PrivateKey) inputStream.readObject();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return privKey;

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

    public PublicKey readPublicKeyFromFile(String keyFileName) {
        File pubKeyFile = new File(context.getFilesDir(), keyFileName);

        ObjectInputStream oin = null;
        try {
             oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pubKeyFile)));
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            if (oin != null) {
                try {
                    oin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    PrivateKey readPrivateKey() {
        File privKeyFile = new File(context.getFilesDir(), PRIVATE_KEY_FILE);
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(privKeyFile)));
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privKey = fact.generatePrivate(keySpec);
            return privKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            if (oin != null) {
                try {
                    oin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] rsaEncrypt(byte[] data, String keyName) {
        PublicKey pubKey = readPublicKeyFromFile(keyName);
        byte[] cipherData = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            cipherData = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return cipherData;
    }

    public byte[] rsaDecrypt(byte[] data){
        PrivateKey privateKey = readPrivateKey();
        byte[] cipherData = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            cipherData = cipher.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return cipherData;
    }

}
