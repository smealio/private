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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreHealthHistoryListActivity;
import com.myctca.adapter.MoreHealthHistoryPrescriptionsAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.Prescription;
import com.myctca.service.HealthHistoryService;
import com.myctca.service.SessionFacade;

import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreHealthHistoryPrescriptionsListFragment extends Fragment implements HealthHistoryService.HealthHistoryServiceListener {

    private static final String TAG = "myCTCA-MOREPRESCRIPT";
    private static final String PURPOSE = "PRESCRIPTIONS";
    private SwipeRefreshLayout mPrescriptionsListRefreshLayout;
    private CTCARecyclerView mPrescriptionsListRecyclerView;
    private MoreHealthHistoryPrescriptionsAdapter mPrescriptionsListAdapter;
    private View mEmptyView;
    private SessionFacade sessionFacade;
    private SearchView searchView;
    private MenuItem downloadItem;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_health_history_prescriptions_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mPrescriptionsListRecyclerView = view.findViewById(R.id.more_health_history_prescriptions_list_recycler_view);
        mPrescriptionsListRefreshLayout = view.findViewById(R.id.more_health_history_prescriptions_list_swipe_refresh);
        mEmptyView = view.findViewById(R.id.empty_view);
        TextView emptyMessage = view.findViewById(R.id.more_health_history_prescriptions_list_empty_text);
        emptyMessage.setText(getString(R.string.empty_list_message, context.getString(R.string.more_health_history_title), ": " + context.getString(R.string.more_health_history_prescriptions)));

        // Pull To Refresh
        mPrescriptionsListRefreshLayout.setOnRefreshListener(() -> refreshItems());

        setPrescriptionsRecyclerView();
        downloadPrescriptionsData(getString(R.string.get_prescriptions_indicator));
    }

    private void setPrescriptionsRecyclerView() {
        mPrescriptionsListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPrescriptionsListRecyclerView.setLayoutManager(layoutManager);
        mPrescriptionsListRecyclerView.setEmptyView(mEmptyView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_health_history_prescriptions, menu);
        initializePrescriptionMenuItems(menu);
        searchPrescriptions();
        setMenuButtons();
    }

    private void setMenuButtons() {
        ImageView searchButton = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageResource(R.drawable.search_icon);
        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_action_close_green);
        if (sessionFacade.getHealthHistoryType(PURPOSE).isEmpty()) {
            downloadItem.getIcon().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            downloadItem.setEnabled(false);

            searchButton.setColorFilter(Color.LTGRAY);
            searchButton.setEnabled(false);
        } else {
            downloadItem.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            downloadItem.setEnabled(true);

            searchButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            closeButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            searchButton.setEnabled(true);
        }
    }

    private void initializePrescriptionMenuItems(Menu menu) {
        downloadItem = menu.findItem(R.id.toolbar_prescription_download);
        MenuItem searchItem = menu.findItem(R.id.item_prescriptions_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(view -> {
            downloadItem.setVisible(false);
            mPrescriptionsListRefreshLayout.setEnabled(false);
        });
        searchView.setOnCloseListener(() -> {
            ((MoreHealthHistoryListActivity) context).invalidateOptionsMenu();
            mPrescriptionsListRefreshLayout.setEnabled(true);
            return false;
        });
    }

    private void searchPrescriptions() {
        SearchView.SearchAutoComplete etSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        etSearch.setHintTextColor(Color.GRAY);
        etSearch.setTextColor(Color.BLACK);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mPrescriptionsListAdapter.filterItems(s);
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPrescriptionsListAdapter != null)
            mPrescriptionsListAdapter.removeFilter();
        mPrescriptionsListRefreshLayout.setEnabled(true);
    }

    private void updateUI(List<Prescription> prescriptionList) {
        mPrescriptionsListAdapter = new MoreHealthHistoryPrescriptionsAdapter(context, prescriptionList);
        mPrescriptionsListRecyclerView.setAdapter(mPrescriptionsListAdapter);
        mPrescriptionsListRecyclerView.getRecycledViewPool().clear();
        mPrescriptionsListAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mPrescriptionsListRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mPrescriptionsListRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        sessionFacade.clearHealthHistoryType(PURPOSE);
        downloadPrescriptionsData(getString(R.string.refresh_prescriptions_indicator));
        onRefreshItemsLoadComplete();
    }

    public void downloadPrescriptionsData(String indicatorStr) {
        if (context != null)
            ((MoreHealthHistoryListActivity) context).showActivityIndicator(indicatorStr);
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_prescriptions);
        sessionFacade.downloadHealthHistoryTypeList(this, context, url, PURPOSE);
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "MoreHealthHistoryPrescriptionsListFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mPrescriptionsListRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreHealthHistoryListActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public <T> void notifyFetchSuccess(List<T> list, String purpose) {
        updateUI((List<Prescription>) list);
        if (searchView != null)
            setMenuButtons();
        if (context != null)
            ((MoreHealthHistoryListActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String message, String purpose) {
        if (context != null) {
            ((MoreHealthHistoryListActivity) context).hideActivityIndicator();
        }
        showRequestFailure(BuildConfig.myctca_server + context.getString(R.string.myctca_get_prescriptions));
    }

    public Map<String, Prescription> getRefillRequest() {
        return mPrescriptionsListAdapter.getmRefillRequest();
    }
}
