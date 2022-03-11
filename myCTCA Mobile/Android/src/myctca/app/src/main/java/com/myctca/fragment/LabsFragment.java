package com.myctca.fragment;


import android.content.Context;
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

import com.myctca.R;
import com.myctca.activity.NavActivity;
import com.myctca.adapter.LabSectionAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.LabResult;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.service.LabResultsService;
import com.myctca.service.SessionFacade;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LabsFragment extends Fragment implements LabResultsService.LabResultsServiceListener, LabSectionAdapter.LabsSectionListener {

    private static final String TAG = "myCTCA-Labs";
    private static final String PURPOSE = "LAB_RESULTS";
    private SwipeRefreshLayout mLabsRefreshLayout;
    private CTCARecyclerView mLabsRecyclerView;
    private SessionFacade sessionFacade;
    private MyCTCAUserProfile userProfile;
    private View mEmptyView;
    private TextView mEmptyTextView;
    private Context context;
    private SectionedRecyclerViewAdapter mLabsAdapter;
    private LabSectionAdapter mLabsSectionAdapter;

    public static LabsFragment newInstance() {
        return new LabsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("LabsFragment:onCreateView", CTCAAnalyticsConstants.PAGE_LAB_RESULTS_VIEW, null, null);
        setHasOptionsMenu(true);
        sessionFacade = new SessionFacade();
        userProfile = sessionFacade.getMyCtcaUserProfile();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_labs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find Views
        mLabsRecyclerView = view.findViewById(R.id.labsRecyclerView);
        mLabsRefreshLayout = view.findViewById(R.id.labsRefreshLayout);
        mEmptyView = view.findViewById(R.id.labs_empty_view);
        mEmptyTextView = view.findViewById(R.id.labs_empty_text_view);

        // Pull To Refresh
        mLabsRefreshLayout.setOnRefreshListener(() -> {
            // Refresh items
            refreshItems();
        });

        setEmptyListView();
        setRecyclerView(mEmptyView);
        downloadLabResults(getString(R.string.get_lab_results_indicator));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");

        ((NavActivity) context).setToolBar(getString(R.string.labs_title));

        inflater.inflate(R.menu.menu_labs, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setRecyclerView(View mEmptyView) {
        mLabsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLabsRecyclerView.setLayoutManager(layoutManager);
        mLabsRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mLabsRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    private void setEmptyListView() {
        if (userProfile.userCan(UserPermissions.VIEW_LAB_RESULTS)) {
            mEmptyTextView.setText(getString(R.string.empty_list_message, context.getString(R.string.labs_title), ""));
        } else {
            mEmptyTextView.setText(getString(R.string.labs_empty_no_permissions));
        }
    }

    public void downloadLabResults(String indicatorStr) {
        if (userProfile.userCan(UserPermissions.VIEW_LAB_RESULTS)) {
            ((NavActivity) context).showActivityIndicator(indicatorStr);

            sessionFacade.getLabResults(context, PURPOSE, this);
        }
    }

    private void updateUI(List<LabResult> labResults) {

        // Section Adapter
        mLabsAdapter = new SectionedRecyclerViewAdapter();
        mLabsRecyclerView.setAdapter(mLabsAdapter);
        mLabsSectionAdapter = new LabSectionAdapter(context, labResults, this);

        if (!labResults.isEmpty() && context != null) {
            mLabsAdapter.addSection(mLabsSectionAdapter);
            notifyDataSetChanged();
        }

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mLabsRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mLabsRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        if (userProfile.userCan(UserPermissions.VIEW_LAB_RESULTS)) {
            sessionFacade.clearLabResults();
            downloadLabResults(getString(R.string.refresh_lab_results_indicator));
        }
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "ApptFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mLabsRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("LabsFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_LABS_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<LabResult> labResults) {
        updateUI(labResults);
        if (context != null)
            ((NavActivity) context).presentationReady();
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        if (context != null)
            ((NavActivity) context).hideActivityIndicator();
        showRequestFailure(error);
    }

    @Override
    public void notifyDataSetChanged() {
        mLabsRecyclerView.getRecycledViewPool().clear();
        mLabsAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshEnable(boolean b) {
        mLabsRefreshLayout.setEnabled(b);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLabsSectionAdapter != null)
            mLabsSectionAdapter.removeFilter();
        mLabsRefreshLayout.setEnabled(true);
    }
}
