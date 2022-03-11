package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MailSentFragment;
import com.myctca.model.Mail;

public class MailSentActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MAILSENT";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MailSentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sent);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        FragmentManager fm = getSupportFragmentManager();
        MailSentFragment fragment = new MailSentFragment();
        fm.beginTransaction()
                .add(R.id.sent_mail_fragment_container, fragment)
                .commit();
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mail_sent, menu);
        String toolbarTitle = getString(R.string.mail_sent_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "BACK BUTTON PRESSED");
            this.onBackPressed();
        } else {
            Log.d(TAG, "DEFAULT");
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode: " + requestCode);
        if (requestCode == MailDetailActivity.MAIL_DETAIL_REQUEST) {

            if (resultCode == Mail.DO_REFRESH) {
                //Update List
                Log.d(TAG, "DO REFRESH");
                FragmentManager fm = getSupportFragmentManager();
                MailSentFragment fragment = (MailSentFragment) fm.findFragmentById(R.id.sent_mail_fragment_container);
                assert fragment != null;
                fragment.refreshItems();
            }
            if (resultCode == Mail.CANCEL_REFRESH) {
                //Do nothing
                Log.d(TAG, "DON'T DO REFRESH");
            }
        }
    }
}
