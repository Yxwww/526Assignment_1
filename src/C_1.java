/**
 * Created by Yuxibro on 15-02-20.
 */
//package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


// Reference: http://www.avajava.com/tutorials/lessons/how-do-i-encrypt-and-decrypt-files-using-des.html
public class C_1 {

    public static void main(String[] args) {
        try {
            String key = "yuxibruh"; // needs to be at least 8 characters for DES
            encryptAllFiles(key);   // encrypt all the files from original0 -- original10 with ECB, CBC, OFB, and CFB in DES
            //FileInputStream fis2 = new FileInputStream("encrypted.txt");
            //FileOutputStream fos2 = new FileOutputStream("decrypted.txt");
            //decrypt(key, fis2, fos2);
            System.out.println("DONE!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void encrypt(String key, InputStream is, OutputStream os, String encryptionType) throws Throwable {
        encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os, encryptionType);
    }

    public static void decrypt(String key, InputStream is, OutputStream os, String encryptionType) throws Throwable {
        encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os,encryptionType);
    }

    public static void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os,String encryptionType) throws Throwable {

        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(encryptionType); // DES/ECB/PKCS5Padding for SunJCE ECB, CBC, OFB, and CFB

        if (mode == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            doCopy(cis, os);
        } else if (mode == Cipher.DECRYPT_MODE) {
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            CipherOutputStream cos = new CipherOutputStream(os, cipher);
            doCopy(is, cos);
        }
    }

    public static void encryptAllFiles(String key){
        try {
            // creating File object with directory name
            File theDir = new File("encryptedFiles");

            // if the directory does not exist, create it
            if (!theDir.exists()) {
                System.out.println("creating directory: encryptedFiles");
                boolean result = false;

                try{
                    theDir.mkdir();
                    result = true;
                } catch(SecurityException se){
                    //handle it
                }
                if(result) {
                    System.out.println("DIR created");
                }
            }
            // TODO: how to encrypt using ECB, CBC, OFB, and CFB
            String[] encryptionType = {"DES/ECB/PKCS5Padding","DES/CBC/PKCS5Padding","DES/OFB/PKCS5Padding","DES/CFB/PKCS5Padding"};
            String currentType = "";
            for(int i =1 ;i < 11;i++){
                for(int j=0;j<encryptionType.length;j++){
                    FileInputStream fis = new FileInputStream("original"+i+".txt");
                    currentType = encryptionType[j].split("/")[1];
                    FileOutputStream fos = new FileOutputStream("encryptedFiles/encrypted"+currentType+"_"+i+".txt");
                    System.out.println("Encrypting"+encryptionType[j]+" on file: "+ "original"+i+".txt");
                    encrypt(key, fis, fos, encryptionType[j]);
                }

            }

        }catch(Throwable t)
        {
            t.printStackTrace();
        }
    }


    public static void doCopy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
        os.close();
        is.close();
    }

}