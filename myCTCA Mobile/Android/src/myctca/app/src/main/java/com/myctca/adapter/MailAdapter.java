package com.myctca.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.MailDetailActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;

import java.util.ArrayList;
import java.util.List;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailHolder> {

    private final static String TAG = MailAdapter.class.getSimpleName();
    private final Activity activity;
    private List<Mail> mMails = new ArrayList<>();

    public MailAdapter(Activity activity, List<Mail> mailAR) {
        if (mailAR != null) {
            mMails = mailAR;
        }
        this.activity = activity;
    }

    @Override
    public MailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        return new MailHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(MailHolder holder, int position) {
        Mail mail = mMails.get(position);
        holder.bind(mail);
    }

    @Override
    public int getItemCount() {
        return mMails.size();
    }

    private void showMailDetail(Mail mail) {
        Intent mailDetailIntent = MailDetailActivity.newIntent(this.activity, mail);
        activity.startActivityForResult(mailDetailIntent, MailDetailActivity.MAIL_DETAIL_REQUEST);
    }

    class MailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Mail mail;

        private TextView tvFrom;
        private TextView tvFromDate;
        private TextView tvSubject;

        private View vUnreadMarker;

        private MailHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_mail, parent, false));

            tvFrom = itemView.findViewById(R.id.mail_from_name);
            tvFromDate = itemView.findViewById(R.id.mail_date);
            tvSubject = itemView.findViewById(R.id.mail_subject);

            vUnreadMarker = itemView.findViewById(R.id.mail_unread_marker);
        }

        public void bind(Mail mail) {
            this.mail = mail;

            String fromText = mail.getFrom();
            String dateText = mail.getMonthDaySentString();
            String subjectText = mail.getSubject();

            if (!this.mail.getRead()) {
                fromText = "<strong>" + fromText + "</strong>";
                vUnreadMarker.setAlpha(1.0f);
            } else {
                vUnreadMarker.setAlpha(0.0f);
            }

            tvFrom.setText(HtmlCompat.fromHtml(fromText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            tvFromDate.setText(HtmlCompat.fromHtml(dateText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            tvSubject.setText(HtmlCompat.fromHtml(subjectText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CTCAAnalyticsManager.createEvent("MailHolder:onClick", CTCAAnalyticsConstants.ACTION_MAIL_DETAIL_TAP, null, null);
            Log.d(TAG, "ITEM CLICKED mail: " + this.mail.getFrom());
            showMailDetail(this.mail);
        }
    }

}
