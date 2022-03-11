package com.myctca.common.fingerprintauth;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by tomackb on 9/18/17.
 */

@TargetApi(Build.VERSION_CODES.M)
public class Decryptor {

    private KeyStore keyStore;

    Decryptor() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(KeystoreHandler.ANDROID_KEY_STORE);
        keyStore.load(null);
    }


    public String decryptData(final String alias, final byte[] encryptedData, final byte[] encryptionIv)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        Log.d("DECRYPTOR", "alias: " + alias);

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        SecretKey secretKey = getSecretKey(alias);
        if (secretKey != null) {
            Log.d("DECRYPTOR", "secretKey: " + secretKey);
            Log.d("DECRYPTOR", "spec: " + spec);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            final byte[] decodedData = cipher.doFinal(encryptedData);

            return new String(decodedData, StandardCharsets.UTF_8);
        }
        return null;
    }

    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        Log.d("DECRYPTOR", "getSecretKey alias: " + alias);
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}
