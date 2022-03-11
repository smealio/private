package com.myctca.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreMedDocClinicalSummaryActivity;
import com.myctca.adapter.MoreMedDocClinicalSummaryAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.ClinicalSummary;
import com.myctca.model.UserPermissions;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedDocClinicalSummaryFragment extends Fragment implements MoreMedicalDocumentsService.MoreMedDocListenerGet,
        MoreMedDocClinicalSummaryAdapter.MoreMedDocClinicalSummaryAdapterListener {

    private static final String TAG = "myCTCA-ClinSumm";
    private static final String PURPOSE = "CLINICAL_SUMMARY";
    private Menu menu;
    private SwipeRefreshLayout mClinicalSummaryRefreshLayout;
    private CTCARecyclerView mClinicalSummaryRecyclerView;
    private MoreMedDocClinicalSummaryAdapter mClinicalSummaryAdapter;
    private String filterStartDate;
    private String filterEndDate;
    private boolean showCheckboxes = false;
    private Button btnDownloadClinicalSummary;
    private Button btnTransmitClinicalSummary;
    private Button btnCancelClinicalSummary;
    private MoreMedDocClinicalSummaryListener mInterface;
    private List<String> selectedClinicalSummaryIDs = new ArrayList<>();
    private boolean isFilterApplied = false;
    private LinearLayout bottomLayoutListClinicalSummary;
    private int enabledColor;
    private int disabledColor;
    private Context context;
    private TextView emptyMessage;
    private View mEmptyView;
    private SessionFacade sessionFacade;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        mInterface = (MoreMedDocClinicalSummaryListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_med_doc_clinical_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        // Find Views
        mClinicalSummaryRecyclerView = view.findViewById(R.id.more_clinical_summary_recycler_view);
        mClinicalSummaryRefreshLayout = view.findViewById(R.id.more_clinical_summary_swipe_refresh);

        //Initialize buttons
        bottomLayoutListClinicalSummary = view.findViewById(R.id.bottom_layout_list_clinical_summary);
        btnDownloadClinicalSummary = bottomLayoutListClinicalSummary.findViewById(R.id.btn_download_clinical_summary);
        btnTransmitClinicalSummary = bottomLayoutListClinicalSummary.findViewById(R.id.btn_transmit_clinical_summary);
        btnCancelClinicalSummary = bottomLayoutListClinicalSummary.findViewById(R.id.btn_close_clinical_summary);

        emptyMessage = view.findViewById(R.id.mail_sent_empty_text);
        mEmptyView = view.findViewById(R.id.empty_view);

        //set colors of icons
        enabledColor = ContextCompat.getColor(context, R.color.colorPrimary);
        disabledColor = Color.LTGRAY;

        // Pull To Refresh
        // Refresh items
        mClinicalSummaryRefreshLayout.setOnRefreshListener(this::refreshItems);

        setEmptyView();
        setRecyclerView(mEmptyView);
        downloadClinicalSummaryList(context.getString(R.string.get_clinical_summary_indicator));
        handleButtonClickListeners();
    }

    private void handleButtonClickListeners() {
        btnDownloadClinicalSummary.setVisibility(View.VISIBLE);
        btnTransmitClinicalSummary.setVisibility(View.VISIBLE);
        btnDownloadClinicalSummary.setOnClickListener(view1 -> {
            if (!selectedClinicalSummaryIDs.isEmpty()) {
                if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.DOWNLOAD_CCDA_DOCUMENTS)) {
                    mInterface.addFragment(new MoreMedDocClinicalSummaryDownloadFragment(), selectedClinicalSummaryIDs, "");
                } else {
                    showPermissionAlert();
                }
            }
        });
        btnTransmitClinicalSummary.setOnClickListener(view12 -> {
            if (!selectedClinicalSummaryIDs.isEmpty()) {
                if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.TRANSMIT_CCDA_DOCUMENTS)) {
                    mInterface.addFragment(MoreMedDocClinicalSummaryTransmitFragment.newInstance(), selectedClinicalSummaryIDs, "");
                } else {
                    showPermissionAlert();
                }
            }
        });

        btnCancelClinicalSummary.setOnClickListener(view13 -> {
            //enable filter and download buttons and dont show checkboxes
            setToolBarIcons(true, enabledColor);
            bottomLayoutListClinicalSummary.setVisibility(View.GONE);
            selectedClinicalSummaryIDs.clear();
            showCheckboxes = false;
            mClinicalSummaryAdapter.setShowCheckboxes(showCheckboxes);
            mClinicalSummaryRecyclerView.getRecycledViewPool().clear();
            mClinicalSummaryAdapter.notifyDataSetChanged();
        });
    }

    private void setRecyclerView(View mEmptyView) {
        // RecyclerView
        mClinicalSummaryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mClinicalSummaryRecyclerView.setLayoutManager(layoutManager);
        mClinicalSummaryRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mClinicalSummaryRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setEmptyView() {
        emptyMessage.setText(context.getString(R.string.empty_list_message, context.getString(R.string.more_medical_docs), ": " + context.getString(R.string.more_med_doc_clinical_summary)));
    }

    public void showPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(context.getString(R.string.permisson_not_granted_title))
                .setMessage(context.getString(R.string.permisson_not_granted_message_proxy))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //do nothing
                }).create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).setToolBar(context.getString(R.string.more_med_doc_clinical_summary_title));
        this.menu = menu;
        inflater.inflate(R.menu.menu_more_med_doc_clinical_summary, menu);


        if (isFilterApplied) {
            menu.getItem(1).setIcon(ContextCompat.getDrawable(context, R.drawable.filter_off_icon));
        } else {
            menu.getItem(1).setIcon(ContextCompat.getDrawable(context, R.drawable.filter_icon));
        }

        if (showCheckboxes || AppSessionManager.getInstance().getmClinicalSummaries().isEmpty()) {
            setToolBarIcons(false, disabledColor);
        } else {
            setToolBarIcons(true, enabledColor);
        }
    }

    public void downloadClinicalSummaryList(String indicatorStr) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).showActivityIndicator(indicatorStr);
        Map<String, String> params = new HashMap<>();
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary);
        params.put("startDate", filterStartDate);
        params.put("endDate", filterEndDate);

        sessionFacade.getMedicalDocumentsData(context, this, PURPOSE, url, params);
    }

    public void updateUI(List<ClinicalSummary> clinicalSummaries) {
        // Section Adapter
        mClinicalSummaryAdapter = new MoreMedDocClinicalSummaryAdapter(context,
                this, clinicalSummaries);
        mClinicalSummaryRecyclerView.setAdapter(mClinicalSummaryAdapter);
        mClinicalSummaryAdapter.setShowCheckboxes(showCheckboxes);
        mClinicalSummaryRecyclerView.getRecycledViewPool().clear();
        mClinicalSummaryAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mClinicalSummaryRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mClinicalSummaryRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        sessionFacade.clearClinicalSummaries();
        downloadClinicalSummaryList(context.getString(R.string.refresh_clinical_summary_indicator));
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        Log.d(TAG, "MoreMedDocClinicalSummaryFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mClinicalSummaryRefreshLayout.setRefreshing(false);
        selectedClinicalSummaryIDs.clear();
    }

    public void applyFilterOnClinicalSummary(String startDate, String endDate) {
        filterStartDate = startDate;
        filterEndDate = endDate;
        sessionFacade.clearClinicalSummaries();
        downloadClinicalSummaryList(context.getString(R.string.get_clinical_summary_indicator));
        isFilterApplied = true;
        setRefreshState();
        menu.getItem(1).setIcon(ContextCompat.getDrawable(context, R.drawable.filter_off_icon));
    }

    private void setRefreshState() {
        mClinicalSummaryRefreshLayout.setEnabled(!mClinicalSummaryRefreshLayout.isEnabled());
    }

    public void removeFilters() {
        filterStartDate = "";
        filterEndDate = "";
        sessionFacade.clearClinicalSummaries();
        downloadClinicalSummaryList(context.getString(R.string.get_clinical_summary_indicator));
        isFilterApplied = false;
        setRefreshState();
        menu.getItem(1).setIcon(ContextCompat.getDrawable(context, R.drawable.filter_icon));
    }

    public void selectClinicalSummary() {
        if (!AppSessionManager.getInstance().getmClinicalSummaries().isEmpty()) {
            //download mode on - show checkboxes, and download,transmit buttons
            setToolBarIcons(false, disabledColor);
            bottomLayoutListClinicalSummary.setVisibility(View.VISIBLE);
            showCheckboxes = true;
            mClinicalSummaryAdapter.setShowCheckboxes(showCheckboxes);
            mClinicalSummaryRecyclerView.getRecycledViewPool().clear();
            mClinicalSummaryAdapter.notifyDataSetChanged();
        }
    }

    private void setToolBarIcons(boolean enabled, int color) {
        if(menu != null) {
            MenuItem filterItem = menu.findItem(R.id.toolbar_clinical_summaries_filter);
            if (filterItem != null) {
                filterItem.setEnabled(enabled);
                filterItem.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }

            MenuItem downloadItem = menu.findItem(R.id.toolbar_clinical_summaries_download);
            if (downloadItem != null) {
                downloadItem.setEnabled(enabled);
                downloadItem.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFilterApplied) {
            isFilterApplied = false;
            AppSessionManager.getInstance().getmClinicalSummaries().clear();
        }
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARY_REQUEST_FAILURE, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    if (context != null)
                        ((MoreMedDocClinicalSummaryActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void fetchCarePlan(String document) {
        //not required here
    }

    @Override
    public <T> void fetchMedicalDocSuccess(List<T> clinicalSummaries) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        updateUI((List<ClinicalSummary>) clinicalSummaries);
        if (!clinicalSummaries.isEmpty())
            setToolBarIcons(true, enabledColor);
        else if (isFilterApplied) {
            MenuItem filterItem = menu.findItem(R.id.toolbar_clinical_summaries_filter);
            filterItem.setEnabled(true);
            filterItem.getIcon().setColorFilter(enabledColor, PorterDuff.Mode.SRC_ATOP);

            MenuItem downloadItem = menu.findItem(R.id.toolbar_clinical_summaries_download);
            downloadItem.setEnabled(false);
            downloadItem.getIcon().setColorFilter(disabledColor, PorterDuff.Mode.SRC_ATOP);
        } else
            setToolBarIcons(false, disabledColor);
    }

    @Override
    public void notifyFetchError(String errorMessage) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        setToolBarIcons(false, disabledColor);
        showRequestFailure(errorMessage);
    }

    @Override
    public void addFragment(MoreMedDocClinicalSummaryDetailFragment newInstance, List<String> selectedClinicalSummary, String title) {
        mInterface.addFragment(new MoreMedDocClinicalSummaryDetailFragment(), selectedClinicalSummary, title);
    }

    @Override
    public void updateSelectedClinicalSummaryArray(List<String> selectedClinicalSummaryIDs) {
        this.selectedClinicalSummaryIDs = selectedClinicalSummaryIDs;
    }


    public interface MoreMedDocClinicalSummaryListener {
        void addFragment(Fragment fragment, List<String> selectedClinicalSummaryIDs, String itemSelectedName);
    }
}
