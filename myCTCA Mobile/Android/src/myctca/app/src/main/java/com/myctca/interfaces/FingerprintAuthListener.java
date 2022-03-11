package com.myctca.interfaces;

/**
 * Created by tomackb on 9/15/17.
 */

public interface FingerprintAuthListener {

    void fingerprintAuthSucceeded();
    void fingerprintAuthFailed();

    void fingerprintAuthError(int errMsgId, CharSequence errString);

    void fingerprintAuthHelp(int helpMsgId, String helpString);

}
