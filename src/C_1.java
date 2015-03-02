/**
 * Created by Yuxibro on 15-02-20.
 */

import java.io.*;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

// Reference: http://www.avajava.com/tutorials/lessons/how-do-i-encrypt-and-decrypt-files-using-des.html
public class C_1 {

    public static LinkedList<EncryptionEncapsulate> EncList= new LinkedList<EncryptionEncapsulate>(); // Stores info of list of ECB Files
    private static byte[] ivBytes = new byte[]{0x03, 0x02, 0x01, 0x04, 0x05, 0x07, 0x08, 0x08}; // to Ensure randomness
    public static void main(String[] args) {
        try {
            //String key = "yuxibruh"; // needs to be at least 8 characters for DES
            for (String s: args) {
                System.out.println(s);
            }
            if(args.length<=0){
                System.out.println("No args ... running auto encrypt");
            }else{
                if(args.length==3){
                    if(args[0].equalsIgnoreCase("encrypt")){
                        encryptFile(args[1],args[2]);
                    }else if(args[0].equalsIgnoreCase("decrypt")){
                        encryptFile(args[1],args[2]);
                    }
                }else{
                    System.out.println("Wrong number of keys please do: <encrypt/decrypt> <Filename> <Password>");
                    System.exit(-1);
                }
            }

            //autoEncrypt(key);   // encrypt all the files from original0 -- original10 with ECB, CBC, OFB, and CFB in DES
            //FileInputStream fis2 = new FileInputStream("encrypted.txt");
            //FileOutputStream fos2 = new FileOutputStream("decrypted.txt");
            //decrypt(key, fis2, fos2);
            for(int i = 0; i<EncList.size(); i++){
                System.out.println(EncList.get(i).toString());
            }
            System.out.println("DONE with "+EncList.size()+" encryption/decryption process.");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void encrypt(String originalFileName,String key, InputStream is, OutputStream os, String encryptionType) throws Throwable {
        // Create ciper instance using DES with input and output stream
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(encryptionType); // DES/ECB/PKCS5Padding for SunJCE ECB, CBC, OFB, and CFB

        //cipher.init(Cipher.ENCRYPT_MODE, desKey);

        if(!encryptionType.equalsIgnoreCase("DES/ECB/PKCS5Padding")){
            IvParameterSpec ivVector = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, desKey,ivVector);
        }else{
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
        }
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] bytes = new byte[64];
        byte[] tmpBytes = new byte[64];
        int numBytes;
        //if(encryptionType.equalsIgnoreCase("DES/ECB/PKCS5Padding")){
        EncryptionEncapsulate ECBEnc = new EncryptionEncapsulate();
        ECBEnc.originalFileName = originalFileName;
        ECBEnc.encryptionType = encryptionType;
        while ((numBytes = cis.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
            tmpBytes = bytes.clone();                   // HAVE to clone each time, otherwise bytes array won't change!!
            ECBEnc.blockList.add(tmpBytes);
        }
        // Add ECB Encapsulate into the array for compare later
        EncList.push(ECBEnc);
        os.flush();
        os.close();
        cis.close();
    }

    public static void decrypt(String key, InputStream is, OutputStream os, String encryptionType) throws Throwable {
        //System.out.println(key);
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(encryptionType); // DES/ECB/PKCS5Padding for SunJCE ECB, CBC, OFB, and CFB

        if(!encryptionType.equalsIgnoreCase("DES/ECB/PKCS5Padding")){
            // Add iv parameter for any the type other than ECB
            IvParameterSpec ivVector = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, desKey,ivVector);
        }else{
            cipher.init(Cipher.DECRYPT_MODE, desKey);
        }

        CipherOutputStream cos = new CipherOutputStream(os, cipher);

        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            cos.write(bytes, 0, numBytes);
        }
        cos.flush();
        cos.close();
        is.close();
    }

    public static void encryptFile(String FileName, String CipherKey) throws Throwable{
        File theDir = new File("encryptedFiles");
        File decDir = new File("decryptedFiles");
        // if the encrypt decrypt directories does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: encryptedFiles");
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
        if (!decDir.exists()) {
            System.out.println("creating directory: decryptedFiles");
            boolean result = false;
            try {
                decDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

        String[] encryptionType = {"DES/ECB/PKCS5Padding","DES/CBC/PKCS5Padding","DES/OFB/PKCS5Padding","DES/CFB/PKCS5Padding"};
        String currentType = "";
        try{

            for(int j=0;j<encryptionType.length;j++){
                    String originalFileName = FileName;
                    currentType = encryptionType[j].split("/")[1];
                    String encryptFileName = "encryptedFiles/encrypted"+currentType+"_"+originalFileName+".txt";
                    FileInputStream fis = new FileInputStream(originalFileName);
                    FileOutputStream fos = new FileOutputStream(encryptFileName);

                    encrypt(originalFileName,CipherKey, fis, fos, encryptionType[j]);
                    System.out.println("Encrypted"+encryptionType[j]+" on file: "+originalFileName+".txt");

                    String decryptFileName = "decryptedFiles/decrypted"+currentType+"_"+originalFileName+".txt";
                    FileInputStream decFis = new FileInputStream(encryptFileName);
                    FileOutputStream decFos = new FileOutputStream(decryptFileName);
                    decrypt(CipherKey, decFis, decFos, encryptionType[j]);
                    System.out.println("Decrypted File "+ originalFileName);

            }

        }catch(IOException e){
            System.out.println("File not found exception: "+e);
        }

    }

    public static void autoEncrypt(String CipherKey){
        try {
            // creating File object with directory name
            File theDir = new File("encryptedFiles");

            // if the directory does not exist, create it
            if (!theDir.exists()) {
                System.out.println("creating directory: encryptedFiles");
                boolean result = false;
                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    //handle it
                }
                if (result) {
                    System.out.println("DIR created");
                }
            }
            String[] encryptionType = {"DES/ECB/PKCS5Padding","DES/CBC/PKCS5Padding","DES/OFB/PKCS5Padding","DES/CFB/PKCS5Padding"};
            String currentType = "";
            for(int i =1 ;i < 11;i++){
                for(int j=0;j<encryptionType.length;j++){
                    if(i==1){
                        String originalFileName = "original"+i+".txt";
                        currentType = encryptionType[j].split("/")[1];
                        String encryptFileName = "encryptedFiles/encrypted"+currentType+"_"+i+".txt";
                        FileInputStream fis = new FileInputStream(originalFileName);
                        FileOutputStream fos = new FileOutputStream(encryptFileName);


                        encrypt(originalFileName,CipherKey, fis, fos, encryptionType[j]);
                        System.out.println("Encrypted"+encryptionType[j]+" on file: "+ "original"+i+".txt");

                        String decryptFileName = "encryptedFiles/decrypted"+currentType+"_"+i+".txt";
                        FileInputStream decFis = new FileInputStream(encryptFileName);
                        FileOutputStream decFos = new FileOutputStream(decryptFileName);
                        decrypt(CipherKey, decFis, decFos, encryptionType[j]);
                        System.out.println("Decrypted File "+ originalFileName);
                    }
                }
            }



        }catch(Throwable t)
        {
            t.printStackTrace();
        }

    }



}