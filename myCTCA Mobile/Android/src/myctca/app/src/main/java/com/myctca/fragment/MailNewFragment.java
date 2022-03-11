package com.myctca.fragment;


import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.activity.MailNewActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.CareTeam;
import com.myctca.model.Mail;
import com.myctca.model.NewMailSend;
import com.myctca.service.CommonService;
import com.myctca.service.SessionFacade;
import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MailNewFragment extends Fragment implements CommonService.CommonServiceListener {

    private static final String TAG = "myCTCA-MailNew";
    private static final int ANIMATE_MIN_WIDTH = 0;
    private static final String PURPOSE = "MAIL_NEW";
    private static final String CARE_PLAN_PURPOSE = "CARE_PLAN_NEW";
    private NewMailSend newMailSendData;
    private Mail respondingMail;
    // Input Fields
    private TextView tvNewMailFromInput;
    private TextView tvNewMailToInput;
    private EditText etNewMailSubjectInput;
    private EditText etNewMailMessageInput;
    // Labels
    private TextView tvNewMailSubjectLabel;
    private TextView tvNewMailMessageLabel;
    private TextView tvNewMailToLabel;
    // Separators and Highlights
    private View viewNewMailSubjectSeparator;
    private View viewNewMailSubjectHighlight;
    private LinearLayout llCareTeams;
    private List<String> mailCareTeamsSelected = new ArrayList<>();
    private SessionFacade sessionFacade;
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
        return inflater.inflate(R.layout.fragment_mail_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newMailSendData = new NewMailSend();
        sessionFacade = new SessionFacade();

        view.findViewById(R.id.newMailScrollView).requestFocus();
        tvNewMailFromInput = view.findViewById(R.id.new_mail_from_input);
        tvNewMailToInput = view.findViewById(R.id.new_mail_to_input);
        etNewMailSubjectInput = view.findViewById(R.id.new_mail_subject_input);
        etNewMailMessageInput = view.findViewById(R.id.new_mail_message_input);

        tvNewMailToLabel = view.findViewById(R.id.new_mail_to_label);
        tvNewMailSubjectLabel = view.findViewById(R.id.new_mail_subject_label);
        tvNewMailMessageLabel = view.findViewById(R.id.new_mail_message_label);

        viewNewMailSubjectSeparator = view.findViewById(R.id.new_mail_subject_separator);
        viewNewMailSubjectHighlight = view.findViewById(R.id.new_mail_subject_highlight);

        llCareTeams = view.findViewById(R.id.ll_care_teams);
        if (((MailNewActivity) context).getRespondingMail() != null) {
            respondingMail = ((MailNewActivity) context).getRespondingMail();
        }
        prepareView();
        downloadCareTeam();
    }

    public NewMailSend getNewMailSendData() {
        return newMailSendData;
    }

    private void prepareView() {

        llCareTeams.setFocusableInTouchMode(true);
        llCareTeams.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click To Care Teams");
            showCareTeams();

        });
        llCareTeams.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
        llCareTeams.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "llCareTeams onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                if (tvNewMailToInput.getText().equals("")) {
                    tvNewMailToLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvNewMailToLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            }
        });

        tvNewMailSubjectLabel.setOnClickListener(v -> {
            etNewMailSubjectInput.setFocusableInTouchMode(true);
            etNewMailSubjectInput.requestFocus();
        });
        etNewMailSubjectInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewNewMailSubjectHighlight, viewNewMailSubjectSeparator.getWidth());
            } else {
                changePriorityOfField(tvNewMailSubjectLabel, etNewMailSubjectInput.getText().toString());
                animateHighlightOut(viewNewMailSubjectHighlight, viewNewMailSubjectSeparator.getWidth());
            }
        });

        // Reason
        tvNewMailMessageLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click tvMessageLabel");
            etNewMailMessageInput.requestFocus();
            etNewMailMessageInput.performClick();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etNewMailMessageInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        etNewMailMessageInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                changePriorityOfField(tvNewMailMessageLabel, etNewMailMessageInput.getText().toString());
            }
        });
    }

    public void setCareTeamsInputText(Map<String, Boolean> selectedCareTeams, List<CareTeam> carePlansDetails) {
        StringBuilder careTeamsText = new StringBuilder();
        mailCareTeamsSelected.clear();
        for (Map.Entry<String, Boolean> entry : selectedCareTeams.entrySet()) {
            if (entry != null && entry.getValue()) {
                // Build String for display
                if (careTeamsText.toString().equals("")) {
                    careTeamsText = new StringBuilder(entry.getKey());
                } else {
                    careTeamsText.append(", ").append(entry.getKey());
                }
                // Build array for ROI
                mailCareTeamsSelected.add(entry.getKey());
            }
        }
        newMailSendData.selectedTo = carePlansDetails;
        newMailSendData.to = carePlansDetails;
        tvNewMailToInput.setText(careTeamsText.toString());
    }

    private void changePriorityOfField(TextView inputFieldLabel, String inputFieldText) {
        if (inputFieldText.isEmpty()) {
            inputFieldLabel.setTextColor(ContextCompat.getColor(Objects.requireNonNull(context), R.color.red));
        } else {
            inputFieldLabel.setTextColor(ContextCompat.getColor(Objects.requireNonNull(context), R.color.colorAccent));
        }
    }

    private void downloadCareTeam() {
        if (context != null)
            ((MailNewActivity) context).showActivityIndicator("Retrieving Care Team data…");
        sessionFacade.downloadCareTeams(this, context, CARE_PLAN_PURPOSE);
    }

    public void updateUI() {
        // Add preset input
        tvNewMailFromInput.setText(AppSessionManager.getInstance().getUserProfile().getFullName());

        if (respondingMail != null) {
            etNewMailSubjectInput.setText("RE:" + respondingMail.getSubject());
            String msgInput = "\n\n\n-----Original Message-----\n\n";
            String fromMsg = "\nFrom: " + respondingMail.getFrom() + "\n";
            String sentMsg = "\nSent: " + MyCTCADateUtils.getMonthDateYearAtTimeStr(respondingMail.getSent()) + "\n";
            String toMsg = "\nTo: " + respondingMail.getSelectedTo() + "\n";
            String subMsg = "\nSubject: " + respondingMail.getSubject() + "\n";
            String message = "\n" + respondingMail.getComments() + "\n";
            etNewMailMessageInput.setText(msgInput + fromMsg + sentMsg + toMsg + subMsg + message);
        }
    }

    public void showCareTeams() {
        dismissKeyboard();
        MailNewActivity activity = (MailNewActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        CareTeamsDialogFragment dialog = CareTeamsDialogFragment.newInstance(activity);
        dialog.show(manager, "");
        dialog.setPreviouslySelected(mailCareTeamsSelected);
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && ((MailNewActivity) context).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((MailNewActivity) context).getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean formIsValid() {
        boolean validForm = true;
        String newMailTo = tvNewMailToInput.getText().toString();
        if (TextUtils.isEmpty(newMailTo)) {
            validForm = false;
            changePriorityOfField(tvNewMailToLabel, newMailTo);
        } else {
            changePriorityOfField(tvNewMailToLabel, newMailTo);
        }

        String newMailSubject = etNewMailSubjectInput.getText().toString();
        if (TextUtils.isEmpty(newMailSubject)) {
            validForm = false;
            changePriorityOfField(tvNewMailSubjectLabel, newMailSubject);
        } else {
            changePriorityOfField(tvNewMailSubjectLabel, newMailSubject);
        }

        String newMailMessage = etNewMailMessageInput.getText().toString();
        if (TextUtils.isEmpty(newMailMessage)) {
            validForm = false;
            changePriorityOfField(tvNewMailMessageLabel, newMailMessage);
        } else {
            changePriorityOfField(tvNewMailMessageLabel, newMailMessage);
        }

        if (validForm) {
            newMailSendData.from = (String) tvNewMailFromInput.getText();
            newMailSendData.comments = newMailMessage;
            if (respondingMail != null) {
                newMailSendData.subject = "RE: " + respondingMail.getSubject();
                newMailSendData.parentMessageId = respondingMail.getParentMessageId();
                newMailSendData.folderName = respondingMail.getFolderName();
            } else {
                newMailSendData.subject = newMailSubject;
                newMailSendData.parentMessageId = null;
                newMailSendData.folderName = null;
            }
            // Need a data in Sent even though it gets overwritten by server
            newMailSendData.sent = MyCTCADateUtils.convertDateToLocalStringUTC(new Date());

            // All Secure Mail is type 1
            newMailSendData.messageType = 1;
        }
        return validForm;
    }

    private void animateHighlightIn(final View view, final int maxWidth) {
        int viewWidth = view.getWidth();
        view.getLayoutParams().width = ANIMATE_MIN_WIDTH;
        Log.d(TAG, "animateHighlightIn() view: " + view + "::::minWidth: " + ANIMATE_MIN_WIDTH + ":::: maxWidth: " + maxWidth + ":::: viewWidth: " + viewWidth);
        view.setVisibility(View.VISIBLE);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(ANIMATE_MIN_WIDTH, maxWidth);
        int mDuration = 300; //in millis
        widthAnimator.setDuration(mDuration);
        widthAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().width = (int) animation.getAnimatedValue();
            view.requestLayout();
            if (view.getWidth() == maxWidth) {
                view.setVisibility(View.VISIBLE);
            }
        });
        widthAnimator.start();
    }

    private void animateHighlightOut(final View view, int maxWidth) {
        int viewWidth = view.getWidth();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(maxWidth, ANIMATE_MIN_WIDTH);
        int mDuration = 100; //in millis
        Log.d(TAG, "animateHighlightIn() view: " + view + "::::minWidth: " + ANIMATE_MIN_WIDTH + ":::: maxWidth: " + maxWidth + ":::: viewWidth: " + viewWidth);
        widthAnimator.setDuration(mDuration);
        widthAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().width = (int) animation.getAnimatedValue();
            view.requestLayout();
            if (view.getWidth() == ANIMATE_MIN_WIDTH) {
                view.setVisibility(View.GONE);
            }
        });
        widthAnimator.start();
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MailNewFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_NEW_MAIL_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(String purpose) {
        if (context != null)
            ((MailNewActivity) context).hideActivityIndicator();
        updateUI();
    }

    @Override
    public void notifyFetchError(String message, String purpose) {
        if (context != null)
            ((MailNewActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }
}