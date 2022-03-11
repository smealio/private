package com.myctca.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.myctca.R;

public class TelehealthSpeakerBottomDialog extends BottomSheetDialogFragment {

    private static final String TAG = TelehealthSpeakerBottomDialog.class.getSimpleName();
    private TelehealthSpeakerBottomDialogListener listener;
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    //as soon as bluetooth turns off, set the device speaker
                    listener.onButtonClick(1);
                    dismiss();
                }
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                dismiss();
            }

            if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                if (!isBluetoothHeadsetConnected()) {
                    //as soon as bluetooth specific device turns off, set the device speaker
                    listener.onButtonClick(1);
                    dismiss();
                }
            }
            if (intent.getAction().equals(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)) {
                if (!isBluetoothHeadsetConnected()) {
                    listener.onButtonClick(1);
                    dismiss();
                } else {
                    listener.onButtonClick(3);
                    dismiss();
                }
            }
        }
    };
    private LinearLayout deviceAudio;
    private LinearLayout speakerAudio;
    private LinearLayout bluetoothAudio;
    private Context context;

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        try {
            listener = (TelehealthSpeakerBottomDialogListener) activity;
            this.context = activity;
        } catch (ClassCastException exception) {
            Log.e(TAG, "exception: " + exception);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_telehealth_speaker_bottom_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceAudio = view.findViewById(R.id.device_audio);
        speakerAudio = view.findViewById(R.id.speaker_audio);
        bluetoothAudio = view.findViewById(R.id.bluetooth_audio);
        if (isBluetoothHeadsetConnected()) {
            bluetoothAudio.setVisibility(View.VISIBLE);
        }
        handleLayoutClicks();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register for broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        context.registerReceiver(bluetoothReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(bluetoothReceiver);
    }

    private boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void handleLayoutClicks() {
        deviceAudio.setOnClickListener(view -> {
            listener.onButtonClick(1);
            dismiss();
        });

        speakerAudio.setOnClickListener(view -> {
            listener.onButtonClick(2);
            dismiss();
        });

        bluetoothAudio.setOnClickListener(view -> {
            listener.onButtonClick(3);
            dismiss();
        });
    }

    public interface TelehealthSpeakerBottomDialogListener {
        void onButtonClick(int speakerType);
    }
}