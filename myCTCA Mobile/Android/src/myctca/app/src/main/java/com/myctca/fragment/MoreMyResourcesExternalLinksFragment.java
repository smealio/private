package com.myctca.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.R;
import com.myctca.activity.MyResourcesActivity;
import com.myctca.adapter.ExternalLinksAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.ExternalLink;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.service.MyResourcesService;
import com.myctca.service.SessionFacade;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreMyResourcesExternalLinksFragment extends Fragment implements MyResourcesService.MyResourceServiceListener, ExternalLinksAdapter.ExternalLinksAdapterInterface {
    private static final String TAG = MoreMyResourcesExternalLinksFragment.class.getSimpleName();
    private SessionFacade sessionFacade;
    private Context context;
    private String purpose = "External_links_purpose";
    private ExternalLinksAdapter externalLinksAdapter;
    private CTCARecyclerView rvExternalLinks;
    private View mEmptyView;
    private TextView mEmptyTextView;
    private SwipeRefreshLayout refreshLayout;
    private MyCTCAUserProfile userProfile;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_my_resources_external_links, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        sessionFacade = new SessionFacade();
        userProfile = sessionFacade.getMyCtcaUserProfile();
        rvExternalLinks = view.findViewById(R.id.rvExternalLinks);
        mEmptyView = view.findViewById(R.id.external_links_empty_view);
        mEmptyTextView = view.findViewById(R.id.external_links_empty_text_view);
        boolean reopen = this.getArguments().getBoolean("reopen");

        setEmptyListView();
        setExternalLinksRecyclerView();
        if (!reopen)
            downloadAllExternalLinks(context.getString(R.string.get_external_links_indicator));
        else
            updateUi(((MyResourcesActivity) context).getExternalLinks());
    }

    private void setEmptyListView() {
        if (userProfile.userCan(UserPermissions.VIEW_EXTERNAL_LINKS)) {
            mEmptyTextView.setText(context.getString(R.string.empty_list_message, getText(R.string.my_resources_external_links_title), ""));
        } else {
            mEmptyTextView.setText(context.getString(R.string.external_links_empty_no_permissions));
        }
    }

    private void downloadAllExternalLinks(String string) {
        if (context != null)
            ((MyResourcesActivity) context).showActivityIndicator(string);
        sessionFacade.downloadAllExternalLinks(context, this, purpose);
    }

    private void setExternalLinksRecyclerView() {
        rvExternalLinks.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvExternalLinks.setLayoutManager(layoutManager);
        rvExternalLinks.setAdapter(externalLinksAdapter);
        rvExternalLinks.setEmptyView(mEmptyView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_my_resources, menu);
        ((MyResourcesActivity) context).setToolBar(this.getArguments().getString("TOOLBAR_NAME"));
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreMyResourcesExternalLinksFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_HOME_ALERT_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<ExternalLink> externalLinks) {
        if (context != null)
            ((MyResourcesActivity) context).hideActivityIndicator();
        updateUi(externalLinks);
    }

    private void updateUi(List<ExternalLink> externalLinks) {
        externalLinksAdapter = new ExternalLinksAdapter(this, (Activity) context, externalLinks);
        rvExternalLinks.getRecycledViewPool().clear();
        rvExternalLinks.setAdapter(externalLinksAdapter);
        externalLinksAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyFetchError(String message) {
        if (context != null)
            ((MyResourcesActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }

    @Override
    public void openExternalLinks(String title, List<ExternalLink> externalLinks) {
        ((MyResourcesActivity) context).setExternalLinks(externalLinks);
        ((MyResourcesActivity) context).addFragment(new MoreMyResourcesExternalLinksFragment(), "", title, true);
    }
}