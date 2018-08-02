package com.nsplay.vip;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

public class Saveaccountandpassword {
    //帳號
    private static final String successaccount = "account";
    //密碼
    private static final String successpassword = "password";
    //Uid
    private static final String successUid = "UserUid";

    private static final String NPVIPDATE = "NPVIPDATE";

    public static boolean ok;

    //存傳進來字串
    public static void saveaccountpassword(String account, String password, Activity act) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        Editor editor = preferences.edit();

        //key,value
        try {
            //存入資料
            String encacc = encrypt(account, "9@a8i7Az");
            String enpwd = encrypt(password, "9@a8i7Az");
            editor.putString(successaccount, URLEncoder.encode(encacc, "utf-8"));
            editor.putString(successpassword, URLEncoder.encode(enpwd, "utf-8"));
            Settings.System.putString(act.getContentResolver(), successaccount, URLEncoder.encode(encacc, "utf-8"));
            Settings.System.putString(act.getContentResolver(), successpassword, URLEncoder.encode(enpwd, "utf-8"));

        } catch (Exception e) {
        }
        editor.commit();

    }

    // 存Uid
    public static void saveUserUid(String uid, Activity act) {
        if (uid.equalsIgnoreCase("") || uid.equalsIgnoreCase("0") || uid == null) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        Editor editor = preferences.edit();
        System.setProperty(successUid, uid);

        try {
            String enUid = encrypt(uid, "9@a8i7Az");
            editor.commit();
            editor.putString(successUid, URLEncoder.encode(enUid, "utf-8"));
            Settings.System.putString(act.getContentResolver(), successUid, URLEncoder.encode(enUid, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveVIPDate(String vipData, Activity act) {
        if (vipData.equalsIgnoreCase("") || vipData.equalsIgnoreCase("0") || vipData == null) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        Editor editor = preferences.edit();
        System.setProperty(NPVIPDATE, vipData);

        try {
            String envipdata = encrypt(vipData, "9@a8i7Az");
            editor.putString(NPVIPDATE, URLEncoder.encode(envipdata, "utf-8"));
            editor.commit();
            Settings.System.putString(act.getContentResolver(), NPVIPDATE, URLEncoder.encode(envipdata, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //取出帳號
    public static String GetaccountString(Activity act) {
        //取得資料
        String getaccountString;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        getaccountString = preferences.getString(successaccount, "");//settings.getInt(所對應的key,如果抓不到對應的值要給什麼預設值)

        try {
            if (getaccountString.compareTo("") == 0) {
                getaccountString = Settings.System.getString(act.getContentResolver(), successaccount);
            }
            if (getaccountString == null) {
                getaccountString = "";
            }
            getaccountString = URLDecoder.decode(getaccountString, "utf-8");
            String decacc = decrypt(getaccountString, "9@a8i7Az");
            return decacc;
        } catch (Exception e) {
            String decacc;
            try {
                decacc = decrypt(getaccountString, "9@a8i7Az");
                return decacc;
            } catch (Exception e1) {
                return getaccountString;
            }

        }

    }

    //取出密碼
    public static String GetpasswordString(Activity act) {
        //取得資料
        String getpasswordString;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        getpasswordString = preferences.getString(successpassword, "");//settings.getInt(所對應的key,如果抓不到對應的值要給什麼預設值)
        try {
            if (getpasswordString.compareTo("") == 0) {
                getpasswordString = Settings.System.getString(act.getContentResolver(), successpassword);
            }
            if (getpasswordString == null) {
                getpasswordString = "";
            }
            getpasswordString = URLDecoder.decode(getpasswordString, "utf-8");
            String decpwd = decrypt(getpasswordString, "9@a8i7Az");
            return decpwd;
        } catch (Exception e) {
            try {
                String decpwd = decrypt(getpasswordString, "9@a8i7Az");
                return decpwd;
            } catch (Exception e1) {
                return getpasswordString;
            }

        }

    }

    // 取出Uid
    public static String getUserUid(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userUid = preferences.getString(successUid, "0");
        Log.d("tag" , "userUid = "+userUid);

        try {
            if (userUid.compareTo("") == 0) {
                userUid = Settings.System.getString(context.getContentResolver(), successUid);
            }
            if (userUid == null) {
                userUid = "0";
            }
            userUid = URLDecoder.decode(userUid, "utf-8");
            String realUid = decrypt(userUid, "9@a8i7Az");
            return realUid;
        } catch (Exception e) {
            try {
                String realUid = decrypt(userUid, "9@a8i7Az");
                return realUid;
            } catch (Exception e1) {
                return userUid;
            }

        }
    }


    public static String getVIPDate(Activity act) {

        //取得資料
        String getvipString;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);         //取得預設的偏好設定
        getvipString = preferences.getString(NPVIPDATE, "");        //settings.getInt(所對應的key,如果抓不到對應的值要給什麼預設值)

        try {
            if (getvipString.compareTo("") == 0) {
                getvipString = Settings.System.getString(act.getContentResolver(), NPVIPDATE);
            }
            if (getvipString == null) {
                getvipString = "";
            }
            getvipString = URLDecoder.decode(getvipString, "utf-8");
            String decvip = decrypt(getvipString, "9@a8i7Az");
            return decvip;
        } catch (Exception e) {
            String decvip;
            try {
                decvip = decrypt(getvipString, "9@a8i7Az");
                return decvip;
            } catch (Exception e1) {
                return getvipString;
            }

        }
    }

    public static void clearVIPDate(Activity act) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(act);

        settings.edit().remove(NPVIPDATE).commit();

    }


    private final static String characterEncoding = "UTF-8";
    private final static String cipherTransformation = "AES/CBC/PKCS5Padding";
    private final static String aesEncryptionAlgorithm = "AES";

    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    public static byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }

    private static byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes = key.getBytes(characterEncoding);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }

    /// <summary>
    /// Encrypts plaintext using AES 128bit key and a Chain Block Cipher and returns a base64 encoded string
    /// </summary>
    /// <param name="plainText">Plain text to encrypt</param>
    /// <param name="key">Secret key</param>
    /// <returns>Base64 encoded string</returns>
    private static String encrypt(String plainText, String key) throws Exception {
        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] keyBytes = getKeyBytes(key);
        return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.DEFAULT);
    }

    /// <summary>
    /// Decrypts a base64 encoded string using the given key (AES 128bit key and a Chain Block Cipher)
    /// </summary>
    /// <param name="encryptedText">Base64 Encoded String</param>
    /// <param name="key">Secret Key</param>
    /// <returns>Decrypted String</returns>
    private static String decrypt(String encryptedText, String key) throws Exception {
        byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] keyBytes = getKeyBytes(key);
        return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
    }

    //doing saving string
    public static String DoChangeAPKencrypt(Activity act, String newpackagename) {
        String acc = GetaccountString(act);
        String pwd = GetpasswordString(act);
        String laststr = "";
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("account", acc);
            jObj.put("pwd", pwd);
            laststr = jObj.toString();
            laststr = encrypt(laststr, newpackagename);
            writeToFile(laststr, act, newpackagename);
        } catch (Exception e) {

        }
        return laststr;
    }

    //GET saving string
    public static List<String> DoChangeAPKdecrypt(Activity act) {
        List<String> acc = new ArrayList<String>();
        try {
            String str = readFromFile(act);
            if (str.compareTo("") == 0) {
                return acc;
            }
            str = decrypt(str, act.getPackageName());
            JSONObject jObj = new JSONObject(str);
            String account = jObj.getString("account");
            String pwd = jObj.getString("pwd");
            acc.add(account);
            acc.add(pwd);
        } catch (Exception e) {

        }
        return acc;
    }

    private static void writeToFile(String data, Activity context, String newpackname) {
        try {
            String filepath = Environment.getExternalStorageDirectory().toString();
            File writefile = new File(filepath, newpackname);
            FileOutputStream fos = new FileOutputStream(writefile);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(Activity context) {
        String ret = "";
        try {
            String filepath = Environment.getExternalStorageDirectory().toString();
            File readfile = new File(filepath, context.getPackageName());
            if (!readfile.exists()) {
                return ret;
            }
            FileInputStream fis = new FileInputStream(readfile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                ret = ret + strLine;
            }
            in.close();
            readfile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

}
