package com.myctca.common.fingerprintauth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.myctca.R;
import com.myctca.interfaces.FingerprintAuthListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static com.myctca.MyCTCA.getAppContext;

/**
 * Created by tomackb on 9/14/17.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TAG = "MyCTCA-Finger";
    private static final String KEY_NAME = "android_myctca";
    // Volatile prevents another thread from seeing a half-initialized state of mInstance
    private static volatile FingerprintHandler mInstance;

    // Use the CancellationSignal method whenever app can no longer process user input, for example when your app goes
    // into the background. If not, then the touch sensor is locked!
    private CancellationSignal cancellationSignal;
    public Cipher cipher;
    // Initializing both Android Keyguard Manager and Fingerprint Manager
    private KeyguardManager keyguardManager = (KeyguardManager) getAppContext().getSystemService(KEYGUARD_SERVICE);
    private FingerprintManager fingerprintManager = (FingerprintManager) getAppContext().getSystemService(FINGERPRINT_SERVICE);
    private KeyStore keyStore;
    private FingerprintAuthListener faListener;

    //Private constructor
    private FingerprintHandler() {
        //Protect from the reflection api.
        if (mInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        } else {
            // Nothing to do just yet
        }
    }

    public static FingerprintHandler getInstance() {
        if (mInstance == null) {
            synchronized (FingerprintHandler.class) {
                if (mInstance == null) mInstance = new FingerprintHandler();
                Log.d(TAG, "mInstance was null: " + mInstance);
            }
        }
        Log.d(TAG, "mInstance: " + mInstance);
        return mInstance;
    }

    //Protect singleton from serialize and deserialize operation.
    protected FingerprintHandler readResolve() {
        return getInstance();
    }

    // Check if fingerprint scanner capable
    public boolean isCapable() {
        // Only versions of Marshmallow and higher have fingerprint authentication
        Log.d(TAG, "FingerprintHandler Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            // Check whether the device has a Fingerprint sensor.
            if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                Log.d(TAG, "FingerprintHandler fingerprintManager.isHardwareDetected(): " + fingerprintManager.isHardwareDetected());
                Log.d(TAG, "FingerprintHandler PERMISSION USE_FINGERPRINT: " + ContextCompat.checkSelfPermission(getAppContext(), android.Manifest.permission.USE_FINGERPRINT));
                Log.d(TAG, "FingerprintHandler PERMISSION GRANTED: " + PackageManager.PERMISSION_GRANTED);
                // Checks whether fingerprint permission is set on manifest
                if (ContextCompat.checkSelfPermission(getAppContext(), android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "FingerprintHandler fingerprintManager.hasEnrolledFingerprints(): " + fingerprintManager.hasEnrolledFingerprints());
                    // Check whether at least one fingerprint is registered
                    if (fingerprintManager.hasEnrolledFingerprints()) {
                        Log.d(TAG, "FingerprintHandler keyguardManager.isKeyguardSecure(): " + keyguardManager.isKeyguardSecure());
                        // Checks whether lock screen security is enabled or not
                        return keyguardManager.isKeyguardSecure();
                    }
                }
            }
        }
        return false;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(getAppContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void stopAuth(){
        if(cancellationSignal != null && !cancellationSignal.isCanceled()){
            cancellationSignal.cancel();
        }
    }
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//
    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        Log.e(TAG, "Authentication error\n" + errString);
        faListener.fingerprintAuthError(errMsgId, errString);
    }

    //onAuthenticationFailed is called when the fingerprint does not match with any of the fingerprints registered on the device//
    @Override
    public void onAuthenticationFailed() {
        Log.e(TAG, "Authentication failed");
        faListener.fingerprintAuthFailed();
    }

    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.e(TAG, "Authentication help\n" + helpString);
    }

    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        faListener.fingerprintAuthSucceeded();
    }

    public void beginAuthorization(FingerprintAuthListener listener) {

        faListener = listener;

        generateKey();

        if (cipherInit()) {
            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
            startAuth(fingerprintManager, cryptoObject);
        }
    }

    public void enableFingerprintAuthorization() {

        SharedPreferences sharedPref = getAppContext().getSharedPreferences(getAppContext().getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_enabled), true).apply();
        sharedPref.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_pref_set), true).apply();
    }

    public void disableFingerprintAuthorization() {

        SharedPreferences sharedPref = getAppContext().getSharedPreferences(getAppContext().getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_enabled), false).apply();
        sharedPref.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_pref_set), true).apply();
    }

    public void resetFingerprintAuthorization() {
        SharedPreferences sharedPref = getAppContext().getSharedPreferences(getAppContext().getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_enabled), false).apply();
        sharedPref.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_pref_set), false).apply();
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("FingerprintHandler:generateKey", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("FingerprintHandler:generateKey", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("FingerprintHandler:generateKey", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("FingerprintHandler:cipherInit", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("FingerprintHandler:cipherInit", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("FingerprintHandler:cipherInit", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}
