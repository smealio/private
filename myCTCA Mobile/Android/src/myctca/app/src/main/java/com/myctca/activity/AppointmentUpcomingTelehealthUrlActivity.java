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
import com.myctca.fragment.appointmment.AppointmentUpcomingTelehealthUrlFragment;

public class AppointmentUpcomingTelehealthUrlActivity extends MyCTCAActivity {

    private static final String TELEHEALTH_URL = "telehealthUrl";
    private static final String TELEHEALTH_BTN_NAME = "telehealthBtnName";
    private static final String TAG = "myCTCA-TELEHEALTH_URL";
    private String telehealthUrl;
    private String telehealthBtnName;

    public static Intent newIntent(Context packageContext, String url, String btnName) {
        Intent intent = new Intent(packageContext, AppointmentUpcomingTelehealthUrlActivity.class);
        intent.putExtra(TELEHEALTH_URL, url);
        intent.putExtra(TELEHEALTH_BTN_NAME, btnName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_upcoming_telehealth_url);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.appts_upcoming_telehealth_url_fragment_container);

        if (fragment == null) {
            fragment = new AppointmentUpcomingTelehealthUrlFragment();
            fm.beginTransaction()
                    .add(R.id.appts_upcoming_telehealth_url_fragment_container, fragment)
                    .commit();
        }

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        Intent i = getIntent();
        this.telehealthUrl = i.getStringExtra(TELEHEALTH_URL);
        this.telehealthBtnName = i.getStringExtra(TELEHEALTH_BTN_NAME);
    }

    public String getTelehealthUrl() {
        return this.telehealthUrl;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appt_upcoming_telehealth, menu);
        setToolBar(this.telehealthBtnName);
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

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
