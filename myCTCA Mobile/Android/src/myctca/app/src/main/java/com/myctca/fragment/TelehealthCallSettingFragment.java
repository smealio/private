package com.myctca.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.CameraFacing;
import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.VideoDeviceInfo;
import com.azure.android.communication.calling.VideoStreamRenderer;
import com.azure.android.communication.calling.VideoStreamRendererView;
import com.myctca.R;
import com.myctca.activity.TelehealthCommunicationActivity;
import com.myctca.model.Appointment;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.service.SessionFacade;
import com.myctca.util.GeneralUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class TelehealthCallSettingFragment extends Fragment {

    private static final int TELEHEALTH_MEETING = 1;
    private ImageButton speakerBtn;
    private ImageButton videoBtn;
    private ImageButton muteBtn;
    private Button joinNowBtn;
    private Button cancelBtn;
    private LinearLayout videoOffLayout;
    private RelativeLayout videoOnLayout;
    private TextView telehealthUserInitials;

    private boolean isVideoOn = false;
    private boolean isMicOn = true;
    private int speakerType = 2;

    private VideoDeviceInfo desiredCamera;
    private LocalVideoStream currentLocalVideoStream;
    private Context context;
    private VideoStreamRenderer previewRenderer;
    private TextView telehealthMeetingTitle;
    private SessionFacade sessionFacade;
    private VideoStreamRendererView uiView;
    private ImageButton switchCamera;
    private boolean isCameraFront = true;
    private AudioManager audioManager;
    private TextView tvMic;
    private TextView tvVideo;
    private TextView tvSpeaker;
    private CallAgent agent;
    private Appointment appointment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_telehealth_call_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        videoBtn = view.findViewById(R.id.video_btn);
        muteBtn = view.findViewById(R.id.mute_btn);
        speakerBtn = view.findViewById(R.id.speaker_btn);

        cancelBtn = view.findViewById(R.id.telehealth_cancel_meeting);
        joinNowBtn = view.findViewById(R.id.telehealth_join_now);
        videoOnLayout = view.findViewById(R.id.rl_video_on_layout);
        videoOffLayout = view.findViewById(R.id.ll_video_off_layout);
        telehealthUserInitials = view.findViewById(R.id.telehealth_user_initials);
        telehealthMeetingTitle = view.findViewById(R.id.telehealth_meeting_title);
        switchCamera = view.findViewById(R.id.ib_telehealth_switch_camera);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        tvVideo = view.findViewById(R.id.tv_video);
        tvSpeaker = view.findViewById(R.id.tv_speaker);
        tvMic = view.findViewById(R.id.tv_mic);

        if (getArguments() != null) {
            appointment = getArguments().getParcelable("APPOINTMENT");
        }
        initializeScreenValues();
        handleButtonClicks();
    }

    private void initializeScreenValues() {
        if (isBluetoothHeadsetConnected()) {
            ((TelehealthCommunicationActivity) context).onButtonClick(3);
        }
        telehealthMeetingTitle.setText(appointment.getDescription());
        telehealthUserInitials.setText(getUserInitials(sessionFacade.getMyCtcaUserProfile().getFullName()));

        //by default set speaker as an option
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(true);
    }

    private boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private String getUserInitials(String name) {
        String[] nameSplit = name.split(" ");
        if (nameSplit.length == 1) {
            return String.valueOf(nameSplit[0].charAt(0));
        } else if (nameSplit.length > 1) {
            return String.valueOf(nameSplit[0].charAt(0)) + nameSplit[1].charAt(0);
        }
        return "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TELEHEALTH_MEETING)
            GeneralUtil.logoutApplication();
    }

    private void handleButtonClicks() {
        videoBtn.setOnClickListener(view -> {
            //set visibilities and icons
            if (isVideoOn) {
                tvVideo.setText("Video is off");
                stopVideoCall();
                switchCamera.setVisibility(View.GONE);
                videoOnLayout.setVisibility(View.GONE);
                videoOffLayout.setVisibility(View.VISIBLE);
                videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_off_icon));
                isVideoOn = !isVideoOn;
            } else {
                //open camera
                if (desiredCamera == null)
                    desiredCamera = getFrontCamera();
                try {
                    currentLocalVideoStream = new LocalVideoStream(desiredCamera, context);
                    // Render a local preview of video so the user knows that their video is being shared
                    previewRenderer = new VideoStreamRenderer(currentLocalVideoStream, context);
                    uiView = previewRenderer.createView(new CreateViewOptions(ScalingMode.CROP));
                    videoOnLayout.addView(uiView);
                    videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_on_icon));
                    isVideoOn = !isVideoOn;
                    tvVideo.setText("Video is on");
                    switchCamera.setVisibility(View.VISIBLE);
                    videoOnLayout.setVisibility(View.VISIBLE);
                    videoOffLayout.setVisibility(View.GONE);
                } catch (Exception exception) {
                    Toast.makeText(context, "Unable to open camera", Toast.LENGTH_LONG).show();
                }
            }
        });

        muteBtn.setOnClickListener(view -> {
            if (isMicOn) {
                tvMic.setText("Mic is off");
                muteBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mic_off_icon));
            } else {
                tvMic.setText("Mic is on");
                muteBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mic_on_icon));
            }
            isMicOn = !isMicOn;
        });

        switchCamera.setOnClickListener(view -> handleSwitchCameraOnClicks());

        speakerBtn.setOnClickListener(view -> {
            TelehealthSpeakerBottomDialog bottomDialog = new TelehealthSpeakerBottomDialog();
            bottomDialog.show(((TelehealthCommunicationActivity) context).getSupportFragmentManager(), TelehealthSpeakerBottomDialog.class.getSimpleName());
        });

        cancelBtn.setOnClickListener(view -> closeScreen());

        joinNowBtn.setOnClickListener(view -> {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TelehealthCallSettingFragment::handleButtonClicks", CTCAAnalyticsConstants.ACTION_TELEHEALTH_JON_NOW_TAP, appointment.getMeetingId(), 0));
            if (((TelehealthCommunicationActivity) context).isNetworkAvailable()) {
                if (previewRenderer != null)
                    previewRenderer.dispose();
                ((TelehealthCommunicationActivity) context).addFragment(new TeleHealthCommunicationFragment(), isVideoOn, isMicOn, speakerType, isCameraFront, agent);
            } else {
                showErrorMessage(getString(R.string.telehealth_internet_error_title), getString(R.string.telehealth_internet_error_message));
            }
        });
    }

    private void showErrorMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog1, which) -> {
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void handleSwitchCameraOnClicks() {
        if (isCameraFront) {
            isCameraFront = false;
            desiredCamera = getBackCamera();
        } else {
            isCameraFront = true;
            desiredCamera = getFrontCamera();
        }
        if (desiredCamera != null)
            currentLocalVideoStream.switchSource(desiredCamera);
        else
            Toast.makeText(context, "Sorry! Cannot open camera", Toast.LENGTH_LONG).show();
    }

    private VideoDeviceInfo getBackCamera() {
        try {
            List<VideoDeviceInfo> cameras = new CallClient().getDeviceManager(context).get().getCameras();
            for (VideoDeviceInfo camera : cameras) {
                if (camera.getCameraFacing() == CameraFacing.BACK) {
                    return camera;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private VideoDeviceInfo getFrontCamera() {
        try {
            List<VideoDeviceInfo> cameras = new CallClient().getDeviceManager(context).get().getCameras();
            for (VideoDeviceInfo camera : cameras) {
                if (camera.getCameraFacing() == CameraFacing.FRONT
                        || camera.getCameraFacing() == CameraFacing.RIGHT_FRONT
                        || camera.getCameraFacing() == CameraFacing.LEFT_FRONT) {
                    return camera;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private void stopVideoCall() {
        if (previewRenderer != null)
            previewRenderer.dispose();
    }

    private void closeScreen() {
        if (previewRenderer != null)
            previewRenderer.dispose();
        ((TelehealthCommunicationActivity) context).finish();
    }

    public void setSpeakerType(int speakerType) {
        this.speakerType = speakerType;
        if (speakerType == 1) {
            speakerBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.device_audio_on));
            tvSpeaker.setText("Device");
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
        } else if (speakerType == 2) {
            speakerBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.speaker_on_icon));
            tvSpeaker.setText("Speaker");
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(true);
        } else {
            speakerBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_bluetooth));
            tvSpeaker.setText("Bluetooth");
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.startBluetoothSco();
            audioManager.setBluetoothScoOn(true);
        }
    }
}