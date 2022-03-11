package com.myctca.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.MoreHealthHistoryListActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.Vitals;
import com.myctca.model.VitalsGroup;

import java.util.ArrayList;
import java.util.List;

public class MoreHealthHistoryVitalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MoreHealthHistoryVitalsAdapter.class.getSimpleName();
    private final Context context;
    private List<VitalsGroup> vitalsGroups = new ArrayList<>();
    private String searchText = "";

    public MoreHealthHistoryVitalsAdapter(Context context, List<VitalsGroup> vitalsGroups) {
        this.context = context;
        if (vitalsGroups != null)
            this.vitalsGroups = vitalsGroups;
    }

    @Override
    public MoreHealthHistoryVitalsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreHealthHistoryVitalsListHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof MoreHealthHistoryVitalsListHolder) {
                MoreHealthHistoryVitalsListHolder vHolder = (MoreHealthHistoryVitalsListHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof MoreHealthHistoryVitalsListHolder) {
                VitalsGroup vitalsGroup = vitalsGroups.get(position);
                MoreHealthHistoryVitalsListHolder vHolder = (MoreHealthHistoryVitalsListHolder) holder;
                vHolder.bind(vitalsGroup, searchText);

                vHolder.itemView.setOnClickListener(view -> {
                    vitalsGroup.setExpanded(!vitalsGroup.isExpanded());
                    notifyItemChanged(position);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return vitalsGroups.size();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<VitalsGroup> filteredVitalsGroups = new ArrayList<>();
        for (int i = 0; i < AppSessionManager.getInstance().getmVitalsGroups().size(); i++) {
            List<Vitals> filteredVitalDetails = new ArrayList<>();
            for (Vitals vital : AppSessionManager.getInstance().getmVitalsGroups().get(i).getDetails()) {
                if (vital.getObservationItem().toLowerCase().contains(s)) {
                    filteredVitalDetails.add(vital);
                }
            }
            AppSessionManager.getInstance().getmVitalsGroups().get(i).setFilteredDetails(filteredVitalDetails);
            if (!filteredVitalDetails.isEmpty())
                filteredVitalsGroups.add(AppSessionManager.getInstance().getmVitalsGroups().get(i));
        }
        vitalsGroups = filteredVitalsGroups;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        vitalsGroups = AppSessionManager.getInstance().getmVitalsGroups();
        for (int i = 0; i < vitalsGroups.size(); i++) {
            vitalsGroups.get(i).setFilteredDetails(vitalsGroups.get(i).getDetails());
        }
        searchText = "";
        notifyDataSetChanged();
    }

    private class MoreHealthHistoryVitalsListHolder extends RecyclerView.ViewHolder {

        private final String TAG = MoreHealthHistoryVitalsListHolder.class.getSimpleName();
        private TextView tvEnteredDate;
        private LinearLayout llVitalsLayout;

        private MoreHealthHistoryVitalsListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_vitals_group, parent, false));

            tvEnteredDate = itemView.findViewById(R.id.vitals_group_entered_date);
            llVitalsLayout = itemView.findViewById(R.id.vitals_group_list_view);
        }

        public void bind(VitalsGroup vitalsGroup, String searchText) {
            MoreHealthHistoryListActivity c = (MoreHealthHistoryListActivity) context;
            List<Vitals> vitals1 = vitalsGroup.getFilteredDetails();
            Log.d(TAG, "vitals size:  " + vitals1.size());
            Log.d(TAG, "vitals: " + vitals1);

            tvEnteredDate.setText(vitalsGroup.getDisplayEnteredDate());

            if (vitalsGroup.isExpanded()) {
                llVitalsLayout.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.arrow_vitals_group).animate().rotation(0).start();
            } else {
                llVitalsLayout.setVisibility(View.GONE);
                itemView.findViewById(R.id.arrow_vitals_group).animate().rotation(180).start();
            }

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int idx = 0;
            for (Vitals vitals : vitalsGroup.getFilteredDetails()) {
                final ViewGroup nullParent = null;
                assert mInflater != null;
                View vitalsView = mInflater.inflate(R.layout.list_item_vitals, nullParent);

                TextView tvObservationItem = vitalsView.findViewById(R.id.vitals_observation_item);
                TextView tvValue = vitalsView.findViewById(R.id.vitals_value);
                View separator = vitalsView.findViewById(R.id.vitals_separator);

                tvValue.setText(vitals.getValue());

                if (!searchText.isEmpty()) {
                    tvObservationItem.setText(c.highlightSearchedText(vitals.getObservationItem(), searchText));
                } else {
                    tvObservationItem.setText(vitals.getObservationItem());
                }
                // insert into main view
                llVitalsLayout.addView(vitalsView, idx, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                idx++;
                if (idx == vitalsGroup.getFilteredDetails().size()) {
                    separator.setVisibility(View.GONE);
                }
            }
        }

        public void clearListItem() {
            tvEnteredDate.setText("");
            llVitalsLayout.removeAllViews();
        }
    }

}
