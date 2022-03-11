package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.PatientReportedActivity;
import com.myctca.adapter.MorePatientSymptomInventoryAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.SymptomInventory;
import com.myctca.service.PatientReportedService;
import com.myctca.service.SessionFacade;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class PatientSymptomInventoryFragment extends Fragment implements PatientReportedService.PatientReportedServiceListener {

    private static final String TAG = PatientSymptomInventoryFragment.class.getSimpleName();
    private static final String PURPOSE = "SYMPTOM_INVENTORY";
    private SwipeRefreshLayout patientInventoryListRefreshLayout;
    private CTCARecyclerView rvPatientReported;
    private MorePatientSymptomInventoryAdapter symptomInventoryListAdapter;
    private View mEmptyView;
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_patient_symptom_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        rvPatientReported = view.findViewById(R.id.more_patient_symptom_inventory_recycler_view);
        patientInventoryListRefreshLayout = view.findViewById(R.id.more_patient_symptom_inventory_list_swipe_refresh);
        mEmptyView = view.findViewById(R.id.empty_view);
        TextView emptyMessage = view.findViewById(R.id.more_patient_symptom_inventory_list_empty_text);
        emptyMessage.setText(getString(R.string.empty_list_message, context.getString(R.string.more_patient_reported), ": " + context.getString(R.string.patient_reported_symptom_inventory_title)));

        // Pull To Refresh
        patientInventoryListRefreshLayout.setOnRefreshListener(this::refreshItems);

        setSymptomInventoryRecyclerView();
        downloadPatientSymptomInventoryData(getString(R.string.retrieving_patient_symptom_inventory));
    }

    private void setSymptomInventoryRecyclerView() {
        rvPatientReported.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPatientReported.setLayoutManager(layoutManager);
        rvPatientReported.setEmptyView(mEmptyView);
    }

    private void updateUI(List<SymptomInventory> symptomInventories) {
        symptomInventoryListAdapter = new MorePatientSymptomInventoryAdapter(context, symptomInventories);
        rvPatientReported.setAdapter(symptomInventoryListAdapter);
        rvPatientReported.getRecycledViewPool().clear();
        symptomInventoryListAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (patientInventoryListRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            rvPatientReported.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        sessionFacade.clearHealthHistoryType(PURPOSE);
        downloadPatientSymptomInventoryData(getString(R.string.refreshing_patient_symptom_inventory));
        onRefreshItemsLoadComplete();
    }

    public void downloadPatientSymptomInventoryData(String indicatorStr) {
        if (context != null)
            ((PatientReportedActivity) context).showActivityIndicator(indicatorStr);
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_patient_symptom_inventory);
        sessionFacade.downloadPatientSymptomInventory(this, context, url, PURPOSE);
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "PatientSymptomInventoryFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        patientInventoryListRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((PatientReportedActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<SymptomInventory> symptomInventories) {
        updateUI(symptomInventories);
        ((PatientReportedActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String handleError, String purpose) {
        showRequestFailure(handleError);
    }
}
