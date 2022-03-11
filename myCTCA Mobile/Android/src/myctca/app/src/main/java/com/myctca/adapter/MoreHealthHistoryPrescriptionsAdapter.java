package com.myctca.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.MoreHealthHistoryListActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.fragment.MoreHealthHistoryPrescriptionDialogFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Prescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreHealthHistoryPrescriptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MoreHealthHistoryPrescriptionsAdapter.class.getSimpleName();
    private final Context context;
    private Map<String, Prescription> mRefillRequest = new HashMap<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private String searchText = "";

    public MoreHealthHistoryPrescriptionsAdapter(Context context, List<Prescription> prescriptions) {
        this.context = context;
        if (prescriptions != null)
            this.prescriptions = prescriptions;
    }

    @Override
    public MoreHealthHistoryPrescriptionsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreHealthHistoryPrescriptionsListHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof MoreHealthHistoryPrescriptionsListHolder) {
                MoreHealthHistoryPrescriptionsListHolder vHolder = (MoreHealthHistoryPrescriptionsListHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreHealthHistoryPrescriptionsListFragment:onViewRecycled", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof MoreHealthHistoryPrescriptionsListHolder) {
                Prescription prescription = prescriptions.get(position);
                MoreHealthHistoryPrescriptionsListHolder vHolder = (MoreHealthHistoryPrescriptionsListHolder) holder;
                vHolder.bind(prescription, searchText);

            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreHealthHistoryPrescriptionsListFragment:onBindViewHolder", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<Prescription> filteredPrescriptions = new ArrayList<>();
        for (Prescription prescription : AppSessionManager.getInstance().getmPrescriptions()) {
            if (prescription.getDrugName().toLowerCase().contains(s) ||
                    prescription.getPrescriptionType().toLowerCase().contains(s) ||
                    prescription.getStatusType().toLowerCase().contains(s)) {
                filteredPrescriptions.add(prescription);
            }
        }
        this.prescriptions = filteredPrescriptions;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        this.prescriptions = AppSessionManager.getInstance().getmPrescriptions();
        searchText = "";
        notifyDataSetChanged();
    }

    public Map<String, Prescription> getmRefillRequest() {
        return this.mRefillRequest;
    }

    private class MoreHealthHistoryPrescriptionsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvDrugName;
        private TextView tvStatusPrescribed;

        private boolean includeInRefill;

        private Prescription prescription;

        private MoreHealthHistoryPrescriptionsListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_prescription, parent, false));

            tvDrugName = itemView.findViewById(R.id.prescription_drug_name);
            tvStatusPrescribed = itemView.findViewById(R.id.prescription_status_prescribed);
            includeInRefill = false;
        }

        private void openPrescriptionDialogFragment(Prescription prescription) {
            MoreHealthHistoryListActivity activity = (MoreHealthHistoryListActivity) context;
            FragmentManager manager = activity.getSupportFragmentManager();
            MoreHealthHistoryPrescriptionDialogFragment dialog = MoreHealthHistoryPrescriptionDialogFragment.newInstance(activity, prescription);
            dialog.show(manager, "");
        }

        public void bind(Prescription prescription, String searchText) {
            MoreHealthHistoryListActivity activity = (MoreHealthHistoryListActivity) context;
            this.prescription = prescription;
            String statusEnteredText = context.getString(R.string.more_health_history_prescriptions_status_prescribed, prescription.getStatusType(), prescription.getPrescriptionType());

            if (!searchText.isEmpty()) {
                tvDrugName.setText(activity.highlightSearchedText(prescription.getDrugName(), searchText));
                tvStatusPrescribed.setText(activity.highlightSearchedText(statusEnteredText, searchText));
            } else {
                tvDrugName.setText(prescription.getDrugName());
                tvStatusPrescribed.setText(statusEnteredText);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "MoreHealthHistoryPrescriptionsListHolder onCLick: " + getLayoutPosition());
            openPrescriptionDialogFragment(this.prescription);
        }

        private void toggleIncludeInRefillButton() {
            Log.d(TAG, "toggleIncludeInRefillButton");
            int color;
            if (includeInRefill) {
                color = ContextCompat.getColor(context, R.color.colorPrimary);
                mRefillRequest.put(prescription.getPrescriptionId(), prescription);
            } else {
                color = ContextCompat.getColor(context, R.color.editTextHint);
                mRefillRequest.remove(prescription.getPrescriptionId());
            }
        }

        public void clearListItem() {
            tvDrugName.setText("");
            tvStatusPrescribed.setText("");
        }
    }
}
