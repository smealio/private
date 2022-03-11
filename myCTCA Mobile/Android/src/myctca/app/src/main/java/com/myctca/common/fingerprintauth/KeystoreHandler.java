package com.myctca.common.fingerprintauth;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This is currently only good for min SDK of 23. If earlier versions are allowed
 * to use Keystore, then we need to modify for KeyPairGeneratorSpec API.
 *
 * See https://medium.com/@ericfu/securely-storing-secrets-in-an-android-application-501f030ae5a3
 *
 * Created by tomackb on 9/19/17.
 */

public class KeystoreHandler {

    private static final String TAG = "CTCA-Keystore";

    public static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    // Volatile prevents another thread from seeing a half-initialized state of mInstance
    private static volatile KeystoreHandler mInstance;

    private Encryptor encryptor;
    private Decryptor decryptor;

    //Private constructor
    private KeystoreHandler() {
        //Protect from the reflection api.
        if (mInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        } else {
            encryptor = new Encryptor();

            try {
                decryptor = new Decryptor();
            } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("KeystoreHandler", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG,"error "+e.getMessage());
            }
        }
    }

    public static KeystoreHandler getInstance() {
        if (mInstance == null) {
            synchronized (FingerprintHandler.class) {
                if (mInstance == null) mInstance = new KeystoreHandler();
            }
        }
        return mInstance;
    }

    //Protect singleton from serialize and deserialize operation.
    protected KeystoreHandler readResolve() {
        return getInstance();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public String decryptPassword(String username) {
        String deText = null;

        try {
            Context context = MyCTCA.getAppContext();
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String encodedText = sharedPref.getString(username, "");
            String encodedIV = sharedPref.getString(getIVKey(username), "");

            byte[] encryption = Base64.decode(encodedText, Base64.DEFAULT);
            byte[] encryptionIV = Base64.decode(encodedIV, Base64.DEFAULT);

//            Log.d(TAG, "encryption: " + encryption + ", IV: " + encryptionIV);
            deText = decryptor.decryptData(username, encryption, encryptionIV);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("KeystoreHandler:decryptPassword", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "decryptString() called with: " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("KeystoreHandler:decryptPassword", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG,"error "+e.getMessage());
        }
        return deText;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void encryptPassword(String username, String password) {

        Log.d(TAG, "ENCRYPTOR: " + encryptor + ", keyName: " + username + ", password: " + password);
        try {
            final byte[] encryptedText = encryptor.encryptText(username, password);
            String base64EncodedText = Base64.encodeToString(encryptedText, Base64.DEFAULT);

            final byte[] encryptedIV = encryptor.getIv();
            String base64EncodedIV = Base64.encodeToString(encryptedIV, Base64.DEFAULT);

            Context context = MyCTCA.getAppContext();
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(username, base64EncodedText);
            editor.putString(getIVKey(username), base64EncodedIV);
            //editor.
            editor.apply();
            Log.d(TAG, "ENCRYPTOR: " + base64EncodedText);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("KeystoreHandler:encryptPassword", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "encryptString() called with: " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | SignatureException | IllegalBlockSizeException | BadPaddingException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("KeystoreHandler:encryptPassword", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG,"error "+e.getMessage());
        }
    }

    private String getIVKey(String username) {
        return username + "_" + "iv";
    }

    public void resetKeystorePreference() {
        Context context = MyCTCA.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);

        // Get last successful username
        String previousUsername = sharedPref.getString(context.getString(R.string.pref_last_ctca_id), "");

        Log.d(TAG, "resetKeystorePreference previousUsername: " + previousUsername);

        if (!previousUsername.equals("")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(previousUsername);
            editor.remove(getIVKey(previousUsername));
            editor.apply();
        }
    }
}
