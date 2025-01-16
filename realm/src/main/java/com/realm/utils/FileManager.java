package com.realm.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FileManager {
    public static  Context context;
    public static String logTag = "FileManager";

    public static byte[] get_file_data(String data_name) {


        try {


            File file = new File(data_name);

            return org.apache.commons.io.FileUtils.readFileToByteArray(file);


        } catch (Exception ex) {
            Log.e("Data file retreival :", "Failed " + ex.getMessage());

        }


        return null;
    }

    public static byte[] appEncryptionKey(String password) {
//         password = "password";

        /* Store these things on disk used to derive key later: */
        int iterationCount = 1000;
        int saltLength = 32; // bytes; should be the same size   as the output (256 / 8 = 32)
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc
        byte[] salt = new byte[saltLength];

        /* When first creating the key, obtain a salt with this: */
        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);

        /* Use this to derive the key from the password: */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return key.getEncoded();
    }

    public static byte[] encryptBytes(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static byte[] decryptBytes(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

    public static String saveAppFileFromBase64(String base64_bytes) {
        byte[] outdata = Base64.decode(base64_bytes, Base64.DEFAULT);
        base64_bytes = null;

        return saveAppFileFromBytes(outdata);

    }
    public static String saveAppFileFromBytes(byte[] file_bytes) {
        try {
            return saveAppFileBytes(file_bytes, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveFileBytes(byte[] file_bytes, boolean encrypt, String full_folder_path) throws Exception {

        return encrypt ? saveFileBytes(encryptBytes(appEncryptionKey(""), file_bytes), full_folder_path) :
                saveFileBytes(file_bytes, full_folder_path);
    }

    public static String saveAppFileBytes(byte[] file_bytes, boolean encrypt) throws Exception {

        return encrypt ? saveFileBytes(encryptBytes(appEncryptionKey(""), file_bytes), new File(svars.current_app_config(context).appDataFolder).getAbsolutePath()) :
                saveFileBytes(file_bytes, new File(svars.current_app_config(context).appDataFolder).getAbsolutePath());
    }

    public static String saveFileBytes(byte[] outdata, String full_folder_path) {

        String img_name = "R" + System.currentTimeMillis() + "DT.DAT";

        File file = new File(full_folder_path);
        if (!file.exists()) {
            Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
        }
        file = new File(svars.current_app_config(context).appDataFolder, img_name);
        try (RandomAccessFile randomFile = new RandomAccessFile(file.getAbsolutePath(), "rw")) {
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(outdata, 0, outdata.length);
            return img_name;
        } catch (IOException ex) {

        }
        return null;
    }

    public static String retrieveFileBase64(String file_path) {

        return Base64.encodeToString(retrieveFileBytes(file_path), Base64.DEFAULT);
    }

    public static String retrieveAppFileBase64(String file_name) {

        try {
            return retrieveAppFileBase64(file_name, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String retrieveAppFileBase64(String file_name, boolean encrypted) throws Exception {

        return encrypted ? Base64.encodeToString(decryptBytes(appEncryptionKey("222222"), retrieveFileBytes(new File(svars.current_app_config(context).appDataFolder, file_name).getAbsolutePath())), Base64.DEFAULT) :
                Base64.encodeToString(retrieveFileBytes(new File(svars.current_app_config(context).appDataFolder, file_name).getAbsolutePath()), Base64.DEFAULT);
    }

    public static byte[] retrieveFileBytes(String full_file_path) {
        try {
            File file = new File(full_file_path);
            return org.apache.commons.io.FileUtils.readFileToByteArray(file);

        } catch (Exception ex) {
            Log.e("Data file retreival :", " " + ex.getMessage());

        }
        return null;
    }

    public static String save_doc(String base64_bytes) {
        try {
            base64_bytes = base64_bytes.replace("\\n", "").replace("\\", "");
//            Bitmap bmp = s_bitmap_handler.getImage();
            String img_name = "RE_DAT" + System.currentTimeMillis() + "_IMG.JPG";

            File file = new File(svars.current_app_config(context).appDataFolder);
            if (!file.exists()) {
                Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
            }
            file = new File(svars.current_app_config(context).appDataFolder, img_name);

            try (OutputStream fOutputStream = new FileOutputStream(file)) {


                fOutputStream.write(Base64.decode(base64_bytes, 0));
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                return null;
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }
            return img_name;

        } catch (Exception ex) {


        }


        return null;
    }

    public static String get_saved_doc_base64(String data_name) {
        String res = "";
        try {
            res = Base64.encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(new File(svars.current_app_config(context).appDataFolder, data_name)), 0);
            return res;
        } catch (Exception ex) {
            Log.e("Data file retreival :", " " + ex.getMessage());

        }


        return res;
    }

    public String save_doc_us(String base64_bytes) {
        byte[] file_bytes = Base64.decode(base64_bytes, 0);

        String img_name = "TA_DAT" + System.currentTimeMillis() + "JPG_IDC.JPG";
        //  String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        //  File file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/");
        File file = new File(svars.current_app_config(context).appDataFolder);
        if (!file.exists()) {
            Log.e("Creating data dir=>", "" + String.valueOf(file.mkdirs()));
        }
        //  file = new File(path + "/TimeAndAttendance/.RAW_EMPLOYEE_DATA/", img_name);
        file = new File(svars.current_app_config(context).appDataFolder, img_name);

        try {
            fOutputStream = new FileOutputStream(file);
            fOutputStream.write(file_bytes);

            //fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        }
        return img_name;
    }

}
