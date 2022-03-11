package com.myctca.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.azure.android.communication.calling.CallAgent;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.view.CustomDialogTopBottom;
import com.myctca.fragment.TeleHealthCommunicationFragment;
import com.myctca.fragment.TelehealthCallSettingFragment;
import com.myctca.fragment.TelehealthSpeakerBottomDialog;

public class TelehealthCommunicationActivity extends MyCTCAActivity implements TelehealthSpeakerBottomDialog.TelehealthSpeakerBottomDialogListener, CustomDialogTopBottom.CustomDialogListener {
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    onButtonClick(1);
                }
                if (state == BluetoothAdapter.STATE_CONNECTED) {
                    onButtonClick(3);
                }
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                if (!isBluetoothHeadsetConnected()) {
                    onButtonClick(1);
                }
            }
        }
    };

    private boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telehealth_communication);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new TelehealthCallSettingFragment();
        fragment.setArguments(getIntent().getExtras());
        fm.beginTransaction()
                .add(R.id.telehealth_call_container, fragment)
                .commit();

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void addFragment(Fragment fragment, boolean isVideoOn, boolean isMicOn, int speakerType, boolean isCameraFront, CallAgent agent) {
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = getIntent().getExtras();
        bundle.putBoolean("IS_VIDEO_ON", isVideoOn);
        bundle.putBoolean("IS_MIC_ON", isMicOn);
        bundle.putInt("SPEAKER_TYPE", speakerType);
        bundle.putBoolean("IS_CAMERA_FRONT", isCameraFront);
        fragment.setArguments(bundle);
        ((TeleHealthCommunicationFragment) fragment).setCallAgent(agent);
        fm.beginTransaction()
                .add(R.id.telehealth_call_container, fragment)
                .commit();
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register for broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    public void onButtonClick(int speakerType) {
        if (selectedFragment instanceof TelehealthCallSettingFragment) {
            ((TelehealthCallSettingFragment) selectedFragment).setSpeakerType(speakerType);
        } else {
            ((TeleHealthCommunicationFragment) selectedFragment).setSpeakerType(speakerType);
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment instanceof TeleHealthCommunicationFragment)
            showDialogBox();
        else super.onBackPressed();
    }

    private void showDialogBox() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog(this, this, "", getString(R.string.leaving_telehealth_dialog_message), getString(R.string.resume_telehealth), getString(R.string.leaving_telehealth));
        if (!isFinishing())
            dialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public void setCallHold(boolean isHold) {
        if (selectedFragment instanceof TeleHealthCommunicationFragment)
            ((TeleHealthCommunicationFragment) selectedFragment).setCallHold(isHold);
    }

    @Override
    public void negativeButtonAction() {
        ((TeleHealthCommunicationFragment) selectedFragment).leaveMeeting();
    }
}