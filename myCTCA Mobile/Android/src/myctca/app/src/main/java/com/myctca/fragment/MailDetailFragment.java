package com.myctca.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MailDetailActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;
import com.myctca.model.MailBoxTask;
import com.myctca.service.MailService;
import com.myctca.service.SessionFacade;
import com.myctca.util.MyCTCADateUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MailDetailFragment extends Fragment implements MailService.MailServicePostListener {

    private static final String TAG = MailDetailFragment.class.getSimpleName();
    private boolean readSuccess = false;
    private Mail mail;
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
        sessionFacade = new SessionFacade();
        return inflater.inflate(R.layout.fragment_mail_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvMailDetail = view.findViewById(R.id.mail_detail_text_view);
        tvMailDetail.setMovementMethod(new ScrollingMovementMethod());

        // Lab Result from Intent
        MailDetailActivity activity = (MailDetailActivity) context;
        this.mail = activity.getMail();

        tvMailDetail.setText(buildMailDetail());

        if (!this.mail.getRead()) {
            markMailAsRead();
        }
    }

    public boolean ifReadSuccess() {
        return readSuccess;
    }

    private SpannableStringBuilder buildMailDetail() {

        SpannableString s0 = new SpannableString("Subject: ");
        SpannableString s1 = new SpannableString(this.mail.getSubject() + "\n\n");
        SpannableString s2 = new SpannableString("From: ");
        SpannableString s3 = new SpannableString(this.mail.getFrom() + "\n\n");
        SpannableString s4 = new SpannableString("Date: ");
        SpannableString s5 = new SpannableString(MyCTCADateUtils.getMonthDateYearAtTimeStr(this.mail.getSent()) + "\n\n");
        SpannableString s6 = new SpannableString("To: ");
        SpannableString s7 = new SpannableString(this.mail.getCommaSeparatedToList() + "\n\n");
        SpannableString s8 = new SpannableString(this.mail.getComments());

        // set the style
        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        s0.setSpan(new StyleSpan(Typeface.BOLD), 0, s0.length(), flag);
        s2.setSpan(new StyleSpan(Typeface.BOLD), 0, s2.length(), flag);
        s4.setSpan(new StyleSpan(Typeface.BOLD), 0, s4.length(), flag);
        s6.setSpan(new StyleSpan(Typeface.BOLD), 0, s6.length(), flag);

        // build the string
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(s4).append(s5).append(s2).append(s3).append(s6).append(s7).append(s0).append(s1).append(s8);
        return builder;
    }

    private void markMailAsRead() {
        Map<String, String> data = new HashMap<>();
        data.put("mailMessageId", this.mail.getMailMessageId());
        sessionFacade.setOnServer(context, this, new JSONObject(data).toString(), MailBoxTask.MARK_AS_READ);
    }

    @Override
    public void notifyPostSuccess(boolean success, String response, int task) {
        Log.d(TAG, "New Mail Request Response: " + success + "::: task: " + task);
        Log.d(TAG, "IS THE TASK MARK_AS_READ: " + (task == MailBoxTask.MARK_AS_READ));
        readSuccess = success;
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_read_mail);
        CTCAAnalyticsManager.createEvent("MailDetailActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        Log.d(TAG, "Something went wrong while trying to send new mail request! Error: " + error.toString() + "::" + error.getLocalizedMessage());
        if (context != null) {
            ((MailDetailActivity) context).hideActivityIndicator();
        }
        if (message.isEmpty())
            message = context.getString(R.string.error_400);
        ((MailDetailActivity) context).showMailDetailFailure(message);
    }
}
