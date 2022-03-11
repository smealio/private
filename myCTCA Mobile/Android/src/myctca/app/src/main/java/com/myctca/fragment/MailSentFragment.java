package com.myctca.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myctca.R;
import com.myctca.activity.MailNewActivity;
import com.myctca.activity.MailSentActivity;
import com.myctca.adapter.MailAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.service.MailService;
import com.myctca.service.SessionFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MailSentFragment extends Fragment implements MailService.MailServiceGetListener {

    private static final String TAG = "myCTCA-MailSent";
    private static final String PURPOSE = "MAIL_SENT";
    private SwipeRefreshLayout mMailSentRefreshLayout;
    private CTCARecyclerView mMailSentRecyclerView;
    private MailAdapter mMailSentAdapter;
    private FloatingActionButton mNewMailFab;
    private View mEmptyView;
    private TextView mEmptyTextView;
    private SessionFacade sessionFacade;
    private MyCTCAUserProfile userProfile;
    private Context context;

    public static MailSentFragment newInstance() {
        return new MailSentFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        userProfile = sessionFacade.getMyCtcaUserProfile();

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_mail_sent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find Views
        mMailSentRecyclerView = view.findViewById(R.id.mailSentRecyclerView);
        mMailSentRefreshLayout = view.findViewById(R.id.mailSentRefreshLayout);
        mNewMailFab = view.findViewById(R.id.mailFab);
        mEmptyView = view.findViewById(R.id.mail_sent_empty_view);
        mEmptyTextView = view.findViewById(R.id.send_new_message);

        // Pull To Refresh
        mMailSentRefreshLayout.setOnRefreshListener(this::refreshItems);

        setEmptyListView();
        setMailRecyclerView();
        setFABNewMail();
        downloadSentMail(context.getString(R.string.get_sent_mail_indicator));
    }

    private void setFABNewMail() {
        if (userProfile.userCan(UserPermissions.SEND_SECURE_MESSAGES)) {
            mNewMailFab.setVisibility(View.VISIBLE);
            mNewMailFab.setOnClickListener(v -> composeNewMail());
        } else {
            mNewMailFab.setVisibility(View.GONE);
        }
    }

    private void composeNewMail() {
        Intent newMailIntent = MailNewActivity.newMailIntent(this.context);
        context.startActivity(newMailIntent);
    }

    private void setMailRecyclerView() {
        mMailSentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMailSentRecyclerView.setLayoutManager(layoutManager);
        mMailSentRecyclerView.setAdapter(mMailSentAdapter);
        mMailSentRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mMailSentRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setEmptyListView() {
        if (isProxyUser()) {
            mEmptyTextView.setVisibility(View.GONE);
        } else {
            if (userProfile.userCan(UserPermissions.VIEW_SECURE_MESSAGES)) {
                setTextClickable();
            } else {
                mEmptyTextView.setText(context.getString(R.string.mail_sent_empty_no_permissions));
            }
        }
    }

    public boolean isProxyUser() {
        SessionFacade sessionFacade = new SessionFacade();
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
            }
        }
        return !isImpersonating;
    }

    private void setTextClickable() {
        String str = context.getString(R.string.new_message_request);
        SpannableString ss = new SpannableString(str);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(context, MailNewActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mEmptyTextView.setText(ss);
        mEmptyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mEmptyTextView.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MailSentActivity) context).setToolBar(context.getString(R.string.mail_sent_title));
        inflater.inflate(R.menu.menu_mail_sent, menu);
    }


    public void downloadSentMail(String indicatorStr) {
        if (context != null)
            ((MailSentActivity) context).showActivityIndicator(indicatorStr);
        Map<String, String> params = new HashMap<>();
        params.put("mailFolder", "SentItems");
        sessionFacade.downloadMail(context, params, PURPOSE, this);
    }

    public void updateUI(List<Mail> sentMail) {
        mMailSentAdapter = new MailAdapter(((MailSentActivity) context), sentMail);
        mMailSentRecyclerView.getRecycledViewPool().clear();
        mMailSentRecyclerView.setAdapter(mMailSentAdapter);
        mMailSentAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mMailSentRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mMailSentRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    public void refreshItems() {
        if (userProfile.userCan(UserPermissions.VIEW_SECURE_MESSAGES)) {
            sessionFacade.clearMails(PURPOSE);
            downloadSentMail(context.getString(R.string.refresh_sent_secure_mail_indicator));
        }
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "MailSentFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mMailSentRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MailSentFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_NEW_MAIL_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MailSentActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<Mail> sentMail, String purpose) {
        updateUI(sentMail);
        if (context != null)
            ((MailSentActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String message, String purpose) {
        if (context != null)
            ((MailSentActivity) context).hideActivityIndicator();
        showRequestFailure(VolleyErrorHandler.handleError(message, context));
    }
}
