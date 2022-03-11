package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.appointmment.AboutFingerprintAuthFragment;

public class AboutFingerprintAuthActivity extends MyCTCAActivity {

    private static final String TAG = "CTCA-AboutFingerAuth";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AboutFingerprintAuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_fingerprint_auth);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.about_finger_auth_fragment_container);

        if (fragment == null) {
            fragment = new AboutFingerprintAuthFragment();
            fm.beginTransaction()
                    .add(R.id.about_finger_auth_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about_finger_auth, menu);

        setToolBar(getString(R.string.about_finger_auth_title));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }
}
