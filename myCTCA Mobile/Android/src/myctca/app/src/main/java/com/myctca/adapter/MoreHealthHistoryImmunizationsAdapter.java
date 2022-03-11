package com.myctca.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.MoreHealthHistoryListActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Immunization;

import java.util.ArrayList;
import java.util.List;

public class MoreHealthHistoryImmunizationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MoreHealthHistoryImmunizationsAdapter.class.getSimpleName();
    private List<Immunization> immunizationList = new ArrayList<>();
    private String searchText = "";
    private Context context;

    public MoreHealthHistoryImmunizationsAdapter(Context context, List<Immunization> immunizationList) {
        this.context = context;
        if (immunizationList != null)
            this.immunizationList = immunizationList;
    }

    @Override
    public MoreHealthHistoryImmunizationsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreHealthHistoryImmunizationsListHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof MoreHealthHistoryImmunizationsListHolder) {
                MoreHealthHistoryImmunizationsListHolder vHolder = (MoreHealthHistoryImmunizationsListHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreHealthHistoryImmunizationsListAdapter:onViewRecycled", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof MoreHealthHistoryImmunizationsListHolder) {

                Immunization immunization = immunizationList.get(position);
                MoreHealthHistoryImmunizationsListHolder vHolder = (MoreHealthHistoryImmunizationsListHolder) holder;
                vHolder.bind(immunization, searchText);
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreHealthHistoryImmunizationsListAdapter:onBindViewHolder", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return immunizationList.size();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<Immunization> filteredImmunizations = new ArrayList<>();
        for (Immunization immunization : AppSessionManager.getInstance().getmImmunizations()) {
            if (immunization.getImmunizationName().toLowerCase().contains(s) ||
                    immunization.getVaccineName().toLowerCase().contains(s) ||
                    immunization.getPerformedBy().toLowerCase().contains(s)) {
                filteredImmunizations.add(immunization);
            }
        }
        this.immunizationList = filteredImmunizations;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        this.immunizationList = AppSessionManager.getInstance().getmImmunizations();
        searchText = "";
        notifyDataSetChanged();
    }


    private class MoreHealthHistoryImmunizationsListHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvVaccine;
        private TextView tvPerformedBy;

        private MoreHealthHistoryImmunizationsListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_immunization, parent, false));

            tvName = itemView.findViewById(R.id.immunization_name);
            tvVaccine = itemView.findViewById(R.id.immunization_vaccine);
            tvPerformedBy = itemView.findViewById(R.id.immunization_performed_by);
        }

        public void bind(Immunization immunization, String searchText) {
            MoreHealthHistoryListActivity c = (MoreHealthHistoryListActivity) context;
            tvName.setText(c.highlightSearchedText(immunization.getImmunizationName(), searchText));
            tvVaccine.setText(c.highlightSearchedText(immunization.getVaccineName(), searchText));
            Spannable performedBy = c.highlightSearchedText(immunization.getPerformedBy(), searchText);
            tvPerformedBy.setText(new SpannableStringBuilder(context.getString(R.string.more_health_history_immunizations_performed_by, immunization.getPerformedDate()) + " ").append(performedBy));
        }

        public void clearListItem() {
            tvName.setText("");
            tvVaccine.setText("");
            tvPerformedBy.setText("");

        }
    }


}
