package com.myctca.fragment.appointmment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.common.fingerprintauth.KeystoreHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFingerprintAuthFragment extends Fragment {

    private static final String TAG = "CTCA-AboutFingerAuth";

    private Switch sEnableFingerAuth;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_fingerprint_auth, container, false);

        TextView tvAboutFingerAuthText = view.findViewById(R.id.about_finger_auth_text_view);
        tvAboutFingerAuthText.setMovementMethod(LinkMovementMethod.getInstance());

        sEnableFingerAuth = view.findViewById(R.id.about_finger_auth_enable_switch);

        final SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        boolean isFingerAuthEnabled = sharedPref.getBoolean(context.getString(R.string.pref_finger_auth_enabled), false);
        Log.d(TAG, "isFingerAuthEnabled: " + isFingerAuthEnabled);
        sEnableFingerAuth.setChecked(isFingerAuthEnabled);
        setTextForSwitch(isFingerAuthEnabled);
        sEnableFingerAuth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "CHECK CHANGED: " + isChecked);
            setTextForSwitch(isChecked);
            sharedPref.edit().putBoolean(context.getString(R.string.pref_finger_auth_enabled), isChecked).apply();
            sharedPref.edit().putBoolean(context.getString(R.string.pref_finger_auth_pref_set), true).apply();
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FingerprintHandler.getInstance().enableFingerprintAuthorization();
                }
                storeUsernamePassword();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FingerprintHandler.getInstance().resetFingerprintAuthorization();
                }
                KeystoreHandler.getInstance().resetKeystorePreference();
            }
        });
        return view;
    }

    private void storeUsernamePassword() {
        String username = AppSessionManager.getInstance().getIdentityUser().getEmail();
        String password = AppSessionManager.getInstance().getIdentityUser().getPword();
        KeystoreHandler.getInstance().encryptPassword(username, password);
    }

    private void setTextForSwitch(boolean isChecked) {
        if (isChecked) {
            sEnableFingerAuth.setText(context.getString(R.string.about_finger_auth_enable_text));
        } else {
            sEnableFingerAuth.setText(context.getString(R.string.about_finger_auth_disable_text));
        }
    }

}
