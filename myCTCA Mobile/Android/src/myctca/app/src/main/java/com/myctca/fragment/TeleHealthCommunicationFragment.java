package com.myctca.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallAgentOptions;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.CallState;
import com.azure.android.communication.calling.CallingCommunicationException;
import com.azure.android.communication.calling.CameraFacing;
import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.RemoteParticipant;
import com.azure.android.communication.calling.RemoteVideoStream;
import com.azure.android.communication.calling.RemoteVideoStreamsUpdatedListener;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.TeamsMeetingLinkLocator;
import com.azure.android.communication.calling.VideoDeviceInfo;
import com.azure.android.communication.calling.VideoStreamRenderer;
import com.azure.android.communication.calling.VideoStreamRendererView;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.activity.TelehealthCommunicationActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.common.view.CustomDialogTopBottom;
import com.myctca.model.Appointment;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.service.SessionFacade;
import com.myctca.util.Constants;
import com.myctca.util.GeneralUtil;
import com.myctca.util.MyCTCADateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class TeleHealthCommunicationFragment extends Fragment implements CustomDialogTopBottom.CustomDialogListener {
    private static final String TAG = TeleHealthCommunicationFragment.class.getSimpleName();
    private static final int TELEHEALTH_MEETING = 1;
    private CallAgent agent;
    private Call call;
    private RelativeLayout hostVideoOnLayout;
    private RelativeLayout myVideoOffLayout;
    private RelativeLayout hostVideoOffLayout;
    private LinearLayout myVideoOnLayout;
    private TextView callStatus;
    private ImageButton muteBtn;
    private ImageButton videoBtn;
    private ImageButton endCall;
    private VideoStreamRendererView view;
    private TextView tvWaitingMessage;
    private TextView tvMyInitials;
    private TextView tvHostInitials;
    private SessionFacade sessionFacade;
    private ImageButton switchCamera;
    private boolean isCameraFront = true;
    private String token;
    private VideoStreamRenderer previewRenderer;
    private VideoStreamRendererView uiView;
    private VideoDeviceInfo desiredCamera;
    private LocalVideoStream currentLocalVideoStream;
    private String meetingUrl;
    private ImageButton speakerButton;
    private AudioManager audioManager;
    private LinearLayout llTelehealhCallScreen;
    private LinearLayout llCallButtons;
    private Handler handler;
    private TextView telehealthMeetingTime;
    private Context context;
    private TextView hostFullName;
    private int speakerType;
    private boolean isVideoOn;
    private boolean isMicOn;
    private boolean isMyViewExpanded;
    private String meetingUrlWeb;
    private AlertDialog dialog;
    private LinearLayout remoteUserActive;
    private RelativeLayout rlBody;
    private LinearLayout telehealthMeetingTimeStatus;
    private TextView telehealthMeetingTimer;
    private CountDownTimer timer;
    private TextView telehealthMeetingTimeTitle;
    private CountDownTimer meetingStartTimer;
    private RemoteVideoStreamsUpdatedListener remoteVideoStreamsUpdatedListener;
    private boolean wasCallHold = false;
    private long lobbyWaitingTime;
    private Handler meetingTotalTimeHandler;
    private Runnable meetingTotalTimeRunnable;
    private long totalMeetingTime;
    private long beforeLobbyTime;

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
        return inflater.inflate(R.layout.fragment_telehealth_communcation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        handler = new Handler(Looper.getMainLooper());
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        TextView telehealthMeetingTitle = view.findViewById(R.id.telehealth_meeting_title);

        rlBody = view.findViewById(R.id.rl_body);
        hostVideoOffLayout = view.findViewById(R.id.rl_host_video_off_layout);
        hostVideoOnLayout = view.findViewById(R.id.host_video_on_layout);
        myVideoOffLayout = view.findViewById(R.id.my_video_off_layout);
        myVideoOnLayout = view.findViewById(R.id.my_video_on_layout);

        llTelehealhCallScreen = view.findViewById(R.id.ll_telehealh_call_screen);
        llCallButtons = view.findViewById(R.id.ll_call_buttons);
        callStatus = view.findViewById(R.id.telehealth_call_status);
        muteBtn = view.findViewById(R.id.telehealth_mute_button);
        videoBtn = view.findViewById(R.id.telehealth_video_button);
        endCall = view.findViewById(R.id.telehealth_end_call);
        tvWaitingMessage = view.findViewById(R.id.telehealth_wait_message);
        tvHostInitials = view.findViewById(R.id.telehealth_user_initials);
        tvMyInitials = view.findViewById(R.id.telehealth_my_initials);
        switchCamera = view.findViewById(R.id.ib_telehealth_switch_camera);
        tvHostInitials.setText(getUserInitials(sessionFacade.getMyCtcaUserProfile().getFullName()));
        speakerButton = view.findViewById(R.id.telehealth_speaker_button);
        telehealthMeetingTime = view.findViewById(R.id.telehealth_meeting_time);
        hostFullName = view.findViewById(R.id.host_full_name);
        remoteUserActive = view.findViewById(R.id.telehealth_user_active);
        telehealthMeetingTimeTitle = view.findViewById(R.id.telehealth_meeting_time_title);
        telehealthMeetingTimeStatus = view.findViewById(R.id.telehealth_meeting_time_status);
        telehealthMeetingTimer = view.findViewById(R.id.telehealth_meeting_timer);

        if (getArguments() != null) {
            token = getArguments().getString("TOKEN");
            appointment = getArguments().getParcelable("APPOINTMENT");
            isVideoOn = getArguments().getBoolean("IS_VIDEO_ON");
            isMicOn = getArguments().getBoolean("IS_MIC_ON");
            speakerType = getArguments().getInt("SPEAKER_TYPE");
            isCameraFront = getArguments().getBoolean("IS_CAMERA_FRONT");
        }
        meetingUrl = appointment.getTelehealthMeetingJoinUrl();
        meetingUrlWeb = appointment.getTeleHealthUrl();
        String appointmentName = appointment.getDescription();
        telehealthMeetingTitle.setText(appointmentName);

        createAgent();
        joinTeamsMeeting();
        handleButtons();
        startButtonsInvisibilityTimer();
        setScreenButtons();
        setAudioManagerSettings();
        CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TelehealthCommunicationFragment::onViewCreated", CTCAAnalyticsConstants.PAGE_TELEHEALTH_MEETING, appointment.getMeetingId(), 0));
    }

    private void createAgent() {
        try {
            CommunicationTokenCredential credential = new CommunicationTokenCredential(token);
            if (agent == null)
                agent = new CallClient().createCallAgent(context, credential, new CallAgentOptions().setDisplayName(sessionFacade.getMyCtcaUserProfile().getFullName())).get();
        } catch (Exception ex) {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::createAgent", CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTERRUPTED, appointment.getMeetingId(), getErrorCode(), 0));
            showErrorDialog(context.getString(R.string.telehealth_error_title), context.getString(R.string.telehealth_error_message), false);
            Thread.currentThread().interrupt();
        }
    }

    private void joinTeamsMeeting() {
        try {
            if (agent != null) {
                TeamsMeetingLinkLocator teamsMeetingLinkLocator = new TeamsMeetingLinkLocator(meetingUrl);
                call = agent.join(
                        context,
                        teamsMeetingLinkLocator, null);
                handleRemoteParticipants();
                call.addOnStateChangedListener(p ->
                        setCallStatus(call.getState()));
                if (!wasCallHold)
                    initiateCallTimer();
            }
        } catch (Exception exception) {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::joinTeamsMeeting", CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTERRUPTED, appointment.getMeetingId(), getErrorCode(), 0));
            if (dialog == null || !dialog.isShowing())
                showErrorDialog(context.getString(R.string.telehealth_error_title), context.getString(R.string.telehealth_error_message), false);
        }
    }

    private void handleMuteListener() {
        call.addOnIsMutedChangedListener(propertyChangedEvent -> {
            Log.d("TAG", "The call is muted" + call.isMuted());
            ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
                if (call.isMuted()) {
                    muteBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mic_off_icon));
                } else {
                    muteBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mic_on_icon));
                }
            });
        });
    }

    public void showErrorDialog(String title, String message, boolean isReasonInternet) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!isReasonInternet) {
            dialog = builder.setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Not Now", (dialog1, which) -> leaveMeeting())
                    .setPositiveButton("Continue", (dialog1, which) -> {
                        if (((TelehealthCommunicationActivity) context).isNetworkAvailable()) {
                            if (!meetingUrlWeb.isEmpty()) {
                                CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::showErrorDialog", CTCAAnalyticsConstants.ACTION_TELEHEALTH_JOIN_ON_WEB, appointment.getMeetingId(), getErrorCode(), 0));
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrlWeb));
                                startActivityForResult(intent, TELEHEALTH_MEETING);
                            }
                        } else {
                            dialog.dismiss();
                            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::isNetworkAvailable", CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTERRUPTED, appointment.getMeetingId(), getErrorCode(), 0));
                            showErrorDialog(context.getString(R.string.telehealth_internet_error_title), context.getString(R.string.telehealth_internet_error_message), true);
                        }
                    }).create();
            dialog.setCancelable(false);
        } else {
            dialog = builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", (dialog1, which) -> leaveMeeting())
                    .create();
        }
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    public void leaveMeeting() {
        try {
            if ((call != null && call.getState() == CallState.CONNECTED) || (call != null && call.getState() == CallState.IN_LOBBY))
                call.hangUp().get();
            tvWaitingMessage.setVisibility(View.VISIBLE);
            tvWaitingMessage.setText(context.getString(R.string.leaving_message));
            Runnable timeRunnable = this::closeScreen;
            new Handler(Looper.getMainLooper()).postDelayed(timeRunnable, 2000);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(context, "Unable to leave meeting", Toast.LENGTH_SHORT).show();
            Thread.currentThread().interrupt();
        }
    }

    private void handleRemoteParticipants() {
        call.addOnRemoteParticipantsUpdatedListener(participantsUpdatedEvent -> {
            checkIfHostJoins();
            checkIfHostLeft();
        });
    }

    private void checkIfHostJoins() {
        if (call != null && !call.getRemoteParticipants().isEmpty()) {
            RemoteParticipant remoteParticipant = call.getRemoteParticipants().get(0);
            remoteUIView(remoteParticipant);

            //handle all remote participant listeners.
            remoteVideoStreamsUpdatedListener = e -> showRemoteParticipantVideo(remoteParticipant);
            remoteParticipant.addOnVideoStreamsUpdatedListener(
                    remoteVideoStreamsUpdatedListener
            );
            remoteParticipant.addOnIsSpeakingChangedListener(e -> bindOnIsSpeakingChangedListener(remoteParticipant));
            showRemoteParticipantVideo(remoteParticipant);
            remoteParticipant.addOnStateChangedListener(propertyChangedEvent -> {
                switch (remoteParticipant.getState()) {
                    case HOLD:
                        Log.d(TAG, "Remote Call Hold");
                        remoteParticipant.removeOnVideoStreamsUpdatedListener(
                                remoteVideoStreamsUpdatedListener
                        );
                        ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
                            hostVideoOffLayout.setVisibility(View.VISIBLE);
                            hostVideoOnLayout.setVisibility(View.GONE);
                            tvWaitingMessage.setVisibility(View.VISIBLE);
                            tvWaitingMessage.setText(context.getString(R.string.on_hold_message));
                        });
                        break;
                    case CONNECTED:
                        Log.d(TAG, "Remote Call Connected");
                        showRemoteParticipantVideo(remoteParticipant);
                        remoteParticipant.addOnVideoStreamsUpdatedListener(
                                remoteVideoStreamsUpdatedListener
                        );
                        ((TelehealthCommunicationActivity) context).runOnUiThread(() -> tvWaitingMessage.setVisibility(View.GONE));
                        break;
                }
            });
        }
    }

    private void remoteUIView(RemoteParticipant remoteParticipant) {
        ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
            tvWaitingMessage.setVisibility(View.GONE);
            //check if myview was there in host's view. if yes then switch the places of views because host is available now.
            if (isMyViewExpanded) {
                isMyViewExpanded = false;
                hostFullName.setVisibility(View.VISIBLE);
                myVideoOnLayout.setVisibility(View.VISIBLE);
                myVideoOffLayout.setVisibility(View.VISIBLE);
                if (isVideoOn) {
                    hostVideoOnLayout.removeAllViews();
                    myVideoOnLayout.addView(uiView);
                } else {
                    tvMyInitials.setText(getUserInitials(sessionFacade.getMyCtcaUserProfile().getFullName()));
                }
            }

            hostFullName.setText(remoteParticipant.getDisplayName());
            tvHostInitials.setText(getUserInitials(remoteParticipant.getDisplayName()));
            tvMyInitials.setText(getUserInitials(sessionFacade.getMyCtcaUserProfile().getFullName()));
        });
    }

    private void bindOnIsSpeakingChangedListener(final RemoteParticipant remoteParticipant) {
        ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
            TransitionDrawable showAsActiveCircle = (TransitionDrawable) remoteUserActive.getBackground();
            TransitionDrawable backgroundColor = (TransitionDrawable) rlBody.getBackground();
            if (remoteParticipant.isSpeaking()) {
                showAsActiveCircle.startTransition(1000);
                backgroundColor.startTransition(1000);
            } else {
                showAsActiveCircle.resetTransition();
                backgroundColor.resetTransition();
            }
        });
    }

    private void checkIfHostLeft() {
        //check if not participants other that user are left in a call making sure it happens only when call is connected
        if (call != null && call.getRemoteParticipants().isEmpty() && call.getState() == CallState.CONNECTED) {
            //when host leaves the meeting
            isMyViewExpanded = true;
            ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
                tvWaitingMessage.setVisibility(View.VISIBLE);
                tvWaitingMessage.setText(context.getString(R.string.leaving_message));
                Runnable timeRunnable = () -> {
                    tvWaitingMessage.setText(context.getString(R.string.waiting_for_others_message));
                    myVideoOnLayout.setVisibility(View.GONE);
                    myVideoOffLayout.setVisibility(View.GONE);
                    hostFullName.setVisibility(View.GONE);
                    hostVideoOnLayout.removeAllViews();
                    tvHostInitials.setText(getUserInitials(sessionFacade.getMyCtcaUserProfile().getFullName()));
                    if (isVideoOn) {
                        myVideoOnLayout.removeAllViews();
                        //hide my video and put it on host's video to expand it
                        hostVideoOffLayout.setVisibility(View.GONE);
                        hostVideoOnLayout.setVisibility(View.VISIBLE);
                        myVideoOnLayout.removeAllViews();
                        hostVideoOnLayout.addView(uiView);
                    } else {
                        hostVideoOffLayout.setVisibility(View.VISIBLE);
                        hostVideoOnLayout.setVisibility(View.GONE);
                    }
                };
                new Handler(Looper.getMainLooper()).postDelayed(timeRunnable, 2000);
            });
        }
    }

    private void showRemoteParticipantVideo(RemoteParticipant remoteParticipant) {
        try {
            if (!remoteParticipant.getVideoStreams().isEmpty()) {
                RemoteVideoStream remoteParticipantStream = remoteParticipant.getVideoStreams().get(0);
                VideoStreamRenderer remoteVideoRenderer = new VideoStreamRenderer(remoteParticipantStream, context);

                ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
                    hostVideoOffLayout.setVisibility(View.GONE);
                    hostVideoOnLayout.setVisibility(View.VISIBLE);
                    if (view != null)
                        view.dispose();
                    try {
                        view = remoteVideoRenderer.createView(new CreateViewOptions(ScalingMode.FIT));
                        hostVideoOnLayout.addView(view);
                    } catch (CallingCommunicationException e) {
                        hostVideoOffLayout.setVisibility(View.VISIBLE);
                        hostVideoOnLayout.setVisibility(View.GONE);
                    }
                });
            } else {
                ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
                    hostVideoOffLayout.setVisibility(View.VISIBLE);
                    hostVideoOnLayout.setVisibility(View.GONE);
                });
            }
            checkIfHostLeft();
        } catch (CallingCommunicationException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void setScreenButtons() {
        if (isVideoOn) {
            videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_on_icon));
        } else {
            videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_off_icon));
        }
        if (isMicOn) {
            muteBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mic_on_icon));
        } else {
            muteBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mic_off_icon));
        }
        if (speakerType == 1) {
            speakerButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.device_audio_on));
        } else if (speakerType == 2) {
            speakerButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.speaker_on_icon));
        } else {
            speakerButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_bluetooth));
        }
    }

    private void applyScreenSettings() {
        if (isVideoOn) {
            initiateCamera();
            myVideoOnLayout.setVisibility(View.VISIBLE);
            switchCamera.setVisibility(View.VISIBLE);
        } else {
            myVideoOffLayout.setVisibility(View.VISIBLE);
            switchCamera.setVisibility(View.GONE);
        }
        try {
            if (isMicOn) {
                call.unmute(context).get();
            } else {
                call.mute(context).get();
            }
        } catch (InterruptedException | ExecutionException | CallingCommunicationException e) {
            Toast.makeText(context, "Unable to change the mic option", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (speakerType == 1) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
        } else if (speakerType == 2) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.startBluetoothSco();
            audioManager.setBluetoothScoOn(true);
        }
    }

    private void setAudioManagerSettings() {
        AudioManager.OnAudioFocusChangeListener afChangeListener =
                focusChange -> {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
                        scheduler.schedule(() -> {
                            // Your app is not in focus now
                            Log.d("", "Mode_ringtone");
                            setCallHold(true);
                        }, 100, TimeUnit.MILLISECONDS);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        Log.d("", "focus_gain");
                        setCallHold(false);
                    }
                };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(focusChange -> {
                        //Handle Focus Change
                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
                            scheduler.schedule(() -> {
                                // Your app is not in focus now
                                Log.d("", "Mode_ringtone");
                                setCallHold(true);
                            }, 100, TimeUnit.MILLISECONDS);
                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                            Log.d("", "focus_gain");
                            setCallHold(false);
                        }
                    }).build());
        } else {
            audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void initiateCamera() {
        if (isCameraFront)
            desiredCamera = getFrontCamera();
        else
            desiredCamera = getBackCamera();
        if (desiredCamera != null) {
            currentLocalVideoStream = new LocalVideoStream(desiredCamera, context);

            // Render a local preview of video so the user knows that their video is being shared
            previewRenderer = new VideoStreamRenderer(currentLocalVideoStream, context);
            uiView = previewRenderer.createView(new CreateViewOptions(ScalingMode.CROP));
            myVideoOnLayout.addView(uiView);
            videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_on_icon));
            startVideoCall(currentLocalVideoStream);
        } else
            Toast.makeText(context, context.getString(R.string.camera_error_message), Toast.LENGTH_LONG).show();
    }

    private void closeScreen() {
        if (previewRenderer != null)
            previewRenderer.dispose();
        ((TelehealthCommunicationActivity) context).finish();
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

    private void stopVideoCall(LocalVideoStream currentLocalVideoStream) {
        try {
            previewRenderer.dispose();
            call.stopVideo(context, currentLocalVideoStream).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void startVideoCall(LocalVideoStream currentLocalVideoStream) {
        try {
            call.startVideo(context, currentLocalVideoStream).get();
        } catch (InterruptedException | ExecutionException e) {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::startVideoCall", CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTERRUPTED, appointment.getMeetingId(), getErrorCode(), 0));
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startWaitingInLobbyTimerAfterMeetingTime(false);
        stopMeetingTimer();
        leaveMeeting();
        AppSessionManager.getInstance().setIdleTimeout(Constants.SESSION_EXPIRY);
    }

    //handle telehealth button clicks
    private void handleButtons() {
        llTelehealhCallScreen.setOnClickListener(view1 -> {
            llCallButtons.setVisibility(View.VISIBLE);
            startButtonsInvisibilityTimer();
        });
        muteBtn.setOnClickListener(i -> handleMicOnClicks());

        videoBtn.setOnClickListener(view -> handleVideoButtonOnClicks());

        switchCamera.setOnClickListener(view -> handleSwitchCameraOnClicks());

        endCall.setOnClickListener(view -> showDialogBox());

        speakerButton.setOnClickListener(view1 ->
        {
            TelehealthSpeakerBottomDialog bottomDialog = new TelehealthSpeakerBottomDialog();
            bottomDialog.show(((TelehealthCommunicationActivity) context).getSupportFragmentManager(), TelehealthSpeakerBottomDialog.class.getSimpleName());
        });
    }

    private void showDialogBox() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog((TelehealthCommunicationActivity) context, this, "", getString(R.string.leaving_telehealth_dialog_message), getString(R.string.resume_telehealth), getString(R.string.leaving_telehealth));
        dialog.show();
    }

    private void showTimeoutError() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        final View customLayout = getLayoutInflater().inflate(R.layout.telehealth_timeout_dialog, null);
        alertDialog.setView(customLayout);
        Button ok = customLayout.findViewById(R.id.ok_btn);
        AlertDialog alert = alertDialog.create();
        ok.setOnClickListener(view1 -> {
            alert.dismiss();
            leaveMeeting();
        });
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void handleVideoButtonOnClicks() {
        if (!call.getLocalVideoStreams().isEmpty()) {
            currentLocalVideoStream = call.getLocalVideoStreams().get(0);
            stopVideoCall(currentLocalVideoStream);
            if (isMyViewExpanded) {
                hostVideoOnLayout.setVisibility(View.GONE);
                hostVideoOffLayout.setVisibility(View.VISIBLE);
            } else {
                myVideoOffLayout.setVisibility(View.VISIBLE);
                myVideoOnLayout.setVisibility(View.GONE);
            }
            videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_off_icon));
            switchCamera.setVisibility(View.GONE);
        } else {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::handleVideoButtonOnClicks", CTCAAnalyticsConstants.ACTION_TELEHEALTH_VIDEO_ON, appointment.getMeetingId(), 0));
            if (desiredCamera == null)
                desiredCamera = getFrontCamera();
            if (desiredCamera != null) {
                currentLocalVideoStream = new LocalVideoStream(desiredCamera, context);
                previewRenderer = new VideoStreamRenderer(currentLocalVideoStream, context);
                uiView = previewRenderer.createView(new CreateViewOptions(ScalingMode.CROP));

                //set the video view at the position of host if, video is in expanded mode
                if (isMyViewExpanded) {
                    hostVideoOnLayout.setVisibility(View.VISIBLE);
                    hostVideoOffLayout.setVisibility(View.GONE);
                    hostVideoOnLayout.addView(uiView);
                } else {
                    myVideoOffLayout.setVisibility(View.GONE);
                    myVideoOnLayout.setVisibility(View.VISIBLE);
                    myVideoOnLayout.addView(uiView);
                }
                switchCamera.setVisibility(View.VISIBLE);
                videoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.video_on_icon));
                startVideoCall(currentLocalVideoStream);
            } else
                Toast.makeText(context, "Sorry! Cannot open camera", Toast.LENGTH_LONG).show();
        }

        isVideoOn = !isVideoOn;
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
                if ((camera.getCameraFacing() == CameraFacing.FRONT)
                        || (camera.getCameraFacing() == CameraFacing.LEFT_FRONT)
                        || (camera.getCameraFacing() == CameraFacing.RIGHT_FRONT)) {
                    return camera;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private void handleMicOnClicks() {
        if (call != null && context != null) {
            if (!call.isMuted()) {
                try {
                    call.mute(context).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            } else {
                try {
                    call.unmute(context).get();
                } catch (NullPointerException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            isMicOn = !isMicOn;
        }
    }

    private void startButtonsInvisibilityTimer() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> llCallButtons.setVisibility(View.GONE), 6000);
    }

    public void setSpeakerType(int speakerType) {
        this.speakerType = speakerType;
        if (speakerType == 1) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
            speakerButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.device_audio_on));
        } else if (speakerType == 2) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(true);
            speakerButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.speaker_on_icon));
        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.startBluetoothSco();
            audioManager.setBluetoothScoOn(true);
            speakerButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.audio_bluetooth));
        }
    }

    public void setCallAgent(CallAgent agent) {
        this.agent = agent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TELEHEALTH_MEETING)
            GeneralUtil.logoutApplication();
    }

    public String getCallStatus() {
        return callStatus.getText().toString();
    }

    private void setCallStatus(CallState status) {
        ((TelehealthCommunicationActivity) context).runOnUiThread(() -> {
            callStatus.setText(status.toString());
            switch (status) {
                case CONNECTING:
                    Log.d(TAG, "CALL CONNECTING");
                    videoBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    muteBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    speakerButton.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));

                    videoBtn.setEnabled(false);
                    muteBtn.setEnabled(false);
                    speakerButton.setEnabled(false);

                    callStatus.setVisibility(View.VISIBLE);
                    myVideoOnLayout.setVisibility(View.GONE);
                    switchCamera.setVisibility(View.GONE);
                    break;
                case CONNECTED:
                    Log.d(TAG, "Call Connected");
                    lobbyWaitingTime = totalMeetingTime - beforeLobbyTime;
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::setCallStatus", CTCAAnalyticsConstants.DURATION_TELEHEALTH_PATIENT_IN_LOBBY, appointment.getMeetingId(), lobbyWaitingTime));
                    startWaitingInLobbyTimerAfterMeetingTime(false);
                    if (meetingStartTimer != null) {
                        meetingStartTimer.cancel();
                        telehealthMeetingTimeStatus.setVisibility(View.GONE);
                    }
                    tvWaitingMessage.setVisibility(View.GONE);
                    videoBtn.setColorFilter(ContextCompat.getColor(context, R.color.white));
                    muteBtn.setColorFilter(ContextCompat.getColor(context, R.color.white));
                    speakerButton.setColorFilter(ContextCompat.getColor(context, R.color.white));

                    handleMuteListener();
                    videoBtn.setEnabled(true);
                    muteBtn.setEnabled(true);
                    speakerButton.setEnabled(true);

                    callStatus.setVisibility(View.GONE);
                    switchCamera.setVisibility(View.VISIBLE);
                    tvWaitingMessage.setVisibility(View.GONE);
                    if (MyCTCA.isIsInForeground())
                        applyScreenSettings();
                    break;
                case IN_LOBBY:
                    beforeLobbyTime = totalMeetingTime;
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::setCallStatus", CTCAAnalyticsConstants.DURATION_TELEHEALTH_JOIN_MEETING, appointment.getMeetingId(), beforeLobbyTime));
                    Log.d(TAG, "CALL IN LOBBY");
                    videoBtn.setEnabled(false);
                    muteBtn.setEnabled(false);
                    speakerButton.setEnabled(false);

                    callStatus.setVisibility(View.GONE);
                    myVideoOffLayout.setVisibility(View.GONE);
                    myVideoOnLayout.setVisibility(View.GONE);
                    switchCamera.setVisibility(View.GONE);
                    tvWaitingMessage.setVisibility(View.VISIBLE);
                    getTimeDifferences();
                    break;
                case LOCAL_HOLD:
                    Log.d(TAG, "Call Local Hold");
                    callStatus.setVisibility(View.GONE);
                    tvWaitingMessage.setVisibility(View.VISIBLE);
                    tvWaitingMessage.setText(context.getString(R.string.on_hold_message));

                    videoBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    muteBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    speakerButton.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));

                    videoBtn.setEnabled(false);
                    muteBtn.setEnabled(false);
                    speakerButton.setEnabled(false);
                    if (!call.getLocalVideoStreams().isEmpty()) {
                        currentLocalVideoStream = call.getLocalVideoStreams().get(0);
                        stopVideoCall(currentLocalVideoStream);
                    }
                    break;
                case DISCONNECTED:
                    totalMeetingTime = totalMeetingTime - lobbyWaitingTime;
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::setCallStatus", CTCAAnalyticsConstants.DURATION_TELEHEALTH_TOTAL_MEETING, appointment.getMeetingId(), totalMeetingTime));
                    Log.d(TAG, "CALL DISCONNECTED");
                    callStatus.setVisibility(View.GONE);
                    videoBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    muteBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    speakerButton.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));

                    videoBtn.setEnabled(false);
                    muteBtn.setEnabled(false);
                    speakerButton.setEnabled(false);
                    break;
                case NONE:
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("TeleHealthCommunicationFragment::setCallStatus", CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTERRUPTED, appointment.getMeetingId(), getErrorCode(), 0));
                    Log.d(TAG, "CALL NONE");
                    videoBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    muteBtn.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));
                    speakerButton.setColorFilter(ContextCompat.getColor(context, R.color.editTextHint));

                    videoBtn.setEnabled(false);
                    muteBtn.setEnabled(false);
                    speakerButton.setEnabled(false);
                    break;
            }
        });
    }

    private int getErrorCode() {
        int errorCode;
        if (call != null && call.getCallEndReason() != null) {
            errorCode = call.getCallEndReason().getCode();
        } else {
            errorCode = 0;
        }
        return errorCode;
    }

    public void setCallHold(boolean isHold) {
        if (isHold) {
            wasCallHold = true;
            if (call.getState() == CallState.CONNECTED)
                call.hold();
        } else {
            if (call.getState() == CallState.LOCAL_HOLD)
                call.resume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasCallHold && (call.getState() == CallState.CONNECTED)) {
            Log.d("", "onResume after local hold");
            wasCallHold = false;
            applyScreenSettings();
        }
    }

    private void initiateCallTimer() {
        long startTime = System.currentTimeMillis();
        meetingTotalTimeHandler = new Handler(Looper.getMainLooper());
        meetingTotalTimeRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                totalMeetingTime = millis;
                int secs = (int) (millis / 1000);
                int mins = secs / 60;
                int hours = mins / 60;
                secs = secs % 60;
                mins = mins % 60;
                meetingTotalTimeHandler.postDelayed(this, 1000);
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, secs);
                telehealthMeetingTime.setText(time);
            }
        };
        meetingTotalTimeHandler.postDelayed(meetingTotalTimeRunnable, 0);
    }

    private void stopMeetingTimer() {
        if (meetingTotalTimeHandler != null)
            meetingTotalTimeHandler.removeCallbacks(meetingTotalTimeRunnable);
    }

    private void getTimeDifferences() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(appointment.getStartDate());
        DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        TimeZone pstTime = TimeZone.getTimeZone(MyCTCADateUtils.getTimeZoneId(appointment.getFacilityTimeZone()));
        pstFormat.setTimeZone(pstTime);
        String currentTime = pstFormat.format(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            cal2.setTime(simpleDateFormat.parse(currentTime));
            Date date1 = cal1.getTime();
            Date date2 = cal2.getTime();
            long difference = date1.getTime() - date2.getTime();
            if (difference > 0) {
                //current time is before the meeting time
                startWaitingInLobbyBeforeMeetingTimeCountdown(difference);
            } else {
                //current time is after the meeting time
                startWaitingInLobbyTimerAfterMeetingTime(true);
            }
        } catch (Exception e) {

        }

    }

    private void startWaitingInLobbyBeforeMeetingTimeCountdown(long difference) {
        telehealthMeetingTimeTitle.setText(getString(R.string.meeting_start_timer_title));
        meetingStartTimer = new CountDownTimer(difference, 1000) {
            public void onTick(long millisUntilFinished) {
                int days = (int) (millisUntilFinished / (1000 * 60 * 60 * 24));
                int hours = (int) ((millisUntilFinished - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                int min = (int) (millisUntilFinished - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                int sec = (int) (millisUntilFinished - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / (1000);

                if (hours > 0)
                    telehealthMeetingTimer.setText(String.format("%d hrs %d mins", hours, min));
                else if (min > 0)
                    telehealthMeetingTimer.setText(String.format("%d mins %d secs", min, sec));
                else if (sec > 0) {
                    telehealthMeetingTimer.setText(String.format("%d secs", sec));
                }
            }

            public void onFinish() {
                telehealthMeetingTimeStatus.setVisibility(View.GONE);
                startWaitingInLobbyTimerAfterMeetingTime(true);
            }

        }.start();
    }

    private void startWaitingInLobbyTimerAfterMeetingTime(boolean start) {
        if (start) {
            int minutes = Constants.TELEHEALTH_MEETING_EXPIRY_MINUTES;
            int millis = minutes * 1000 * 60;
            timer = new CountDownTimer(millis, 1000) {
                public void onTick(long millisUntilFinished) {
                    //do nothing
                    Log.d("", "millis: " + millisUntilFinished);
                }

                public void onFinish() {
                    showTimeoutError();
                }

            }.start();
        } else {
            if (timer != null)
                timer.cancel();
        }
    }

    @Override
    public void negativeButtonAction() {
        leaveMeeting();
    }
}