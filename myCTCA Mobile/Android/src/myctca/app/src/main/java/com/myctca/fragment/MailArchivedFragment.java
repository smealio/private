package com.myctca.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.myctca.activity.MailArchivedActivity;
import com.myctca.activity.MailNewActivity;
import com.myctca.adapter.MailAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.service.MailService;
import com.myctca.service.SessionFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MailArchivedFragment extends Fragment implements MailService.MailServiceGetListener {

    private static final String TAG = "myCTCA-MailArchived";
    private static final String PURPOSE = "MAIL_ARCHIVED";
    private SwipeRefreshLayout mMailArchivedRefreshLayout;
    private CTCARecyclerView mMailArchivedRecyclerView;
    private MailAdapter mMailArchivedAdapter;
    private SessionFacade sessionFacade;
    private MyCTCAUserProfile userProfile;
    private FloatingActionButton mNewMailFab;
    private TextView mEmptyTextView;
    private View mEmptyView;
    private Context context;

    public static MailArchivedFragment newInstance() {
        return new MailArchivedFragment();
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
        return inflater.inflate(R.layout.fragment_mail_archived, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMailArchivedRecyclerView = view.findViewById(R.id.mailArchivedRecyclerView);
        mMailArchivedRefreshLayout = view.findViewById(R.id.mailArchivedRefreshLayout);
        mNewMailFab = view.findViewById(R.id.mailFab);
        mEmptyView = view.findViewById(R.id.mail_archived_empty_view);
        mEmptyTextView = view.findViewById(R.id.mail_archived_empty_text_view);

        // Pull To Refresh
        mMailArchivedRefreshLayout.setOnRefreshListener(() -> {
            // Refresh items
            refreshItems();
        });

        setEmptyListView();
        setFABNewMail();
        setMailRecyclerView();
        downloadArchivedMail(getString(R.string.get_archived_mail_indicator));
    }

    private void setMailRecyclerView() {
        mMailArchivedRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMailArchivedRecyclerView.setLayoutManager(layoutManager);
        mMailArchivedRecyclerView.setAdapter(mMailArchivedAdapter);
        mMailArchivedRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mMailArchivedRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setFABNewMail() {
        if (userProfile.userCan(UserPermissions.SEND_SECURE_MESSAGES)) {
            mNewMailFab.setVisibility(View.VISIBLE);
            mNewMailFab.setOnClickListener(v -> composeNewMail());
        } else {
            mNewMailFab.setVisibility(View.GONE);
        }
    }

    private void setEmptyListView() {
        if (!userProfile.userCan(UserPermissions.VIEW_SECURE_MESSAGES)) {
            mEmptyTextView.setText(getString(R.string.mail_archived_empty_no_permissions));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((MailArchivedActivity) context).setToolBar(getString(R.string.mail_archived_title));
        inflater.inflate(R.menu.menu_mail_archived, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void composeNewMail() {
        Intent newMailIntent = MailNewActivity.newMailIntent(this.context);
        context.startActivity(newMailIntent);
    }

    public void downloadArchivedMail(String indicatorStr) {
        if (context != null)
            ((MailArchivedActivity) context).showActivityIndicator(indicatorStr);
        Map<String, String> params = new HashMap<>();
        params.put("mailFolder", "DeletedItems");
        sessionFacade.downloadMail(context, params, PURPOSE, this);
    }

    public void updateUI(List<Mail> archivedMails) {
        mMailArchivedAdapter = new MailAdapter((MailArchivedActivity) context, archivedMails);
        mMailArchivedRecyclerView.getRecycledViewPool().clear();
        mMailArchivedRecyclerView.setAdapter(mMailArchivedAdapter);
        mMailArchivedAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mMailArchivedRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mMailArchivedRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        if (userProfile.userCan(UserPermissions.VIEW_LAB_RESULTS)) {
            sessionFacade.clearMails(PURPOSE);
            downloadArchivedMail(getString(R.string.refresh_archive_secure_mail_indicator));
        }
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "MailArchivedFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mMailArchivedRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MailArchivedFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_MAIL_ARCHIVE_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MailArchivedActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<Mail> archivedMails, String purpose) {
        updateUI(archivedMails);
        if (context != null)
            ((MailArchivedActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String message, String purpose) {
        if (context != null)
            ((MailArchivedActivity) context).hideActivityIndicator();
        showRequestFailure(VolleyErrorHandler.handleError(message, context));
    }
}
