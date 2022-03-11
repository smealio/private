package com.myctca.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.model.PatientReportedSymptomInventory;
import com.myctca.model.SymptomInventory;

import java.util.ArrayList;
import java.util.List;

public class MorePatientSymptomInventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MorePatientSymptomInventoryAdapter.class.getSimpleName();
    private final Context context;
    private List<SymptomInventory> symptomInventories = new ArrayList<>();
    private String searchText = "";

    public MorePatientSymptomInventoryAdapter(Context context, List<SymptomInventory> symptomInventories) {
        this.context = context;
        if (symptomInventories != null)
            this.symptomInventories = symptomInventories;
    }

    @Override
    public MorePatientSymptomInventoryListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MorePatientSymptomInventoryListHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof MorePatientSymptomInventoryListHolder) {
                MorePatientSymptomInventoryListHolder vHolder = (MorePatientSymptomInventoryListHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof MorePatientSymptomInventoryListHolder) {
                SymptomInventory symptomInventory = symptomInventories.get(position);
                MorePatientSymptomInventoryListHolder vHolder = (MorePatientSymptomInventoryListHolder) holder;
                vHolder.bind(symptomInventory);

                vHolder.itemView.setOnClickListener(view -> {
                    symptomInventory.setExpanded(!symptomInventory.isExpanded());
                    notifyItemChanged(position);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return symptomInventories.size();
    }

    private class MorePatientSymptomInventoryListHolder extends RecyclerView.ViewHolder {

        private final String TAG = MorePatientSymptomInventoryListHolder.class.getSimpleName();
        private TextView tvEnteredDate;
        private LinearLayout llSymptomInventoryLayout;

        private MorePatientSymptomInventoryListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_symptom_inventory_header, parent, false));

            tvEnteredDate = itemView.findViewById(R.id.symptom_inventory_date);
            llSymptomInventoryLayout = itemView.findViewById(R.id.symptom_inventory_group_list_view);
        }

        public void bind(SymptomInventory symptomInventory) {
            List<PatientReportedSymptomInventory> patientReportedSymptomInventories = symptomInventory.getSymptomInventories();

            tvEnteredDate.setText(symptomInventory.getDate());

            if (symptomInventory.isExpanded()) {
                llSymptomInventoryLayout.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.arrow_symptom_inventory_group).animate().rotation(0).start();
            } else {
                llSymptomInventoryLayout.setVisibility(View.GONE);
                itemView.findViewById(R.id.arrow_symptom_inventory_group).animate().rotation(180).start();
            }

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (PatientReportedSymptomInventory inventory : patientReportedSymptomInventories) {
                final ViewGroup nullParent = null;
                assert mInflater != null;
                View symptomInventoryView = mInflater.inflate(R.layout.list_item_symptom_inventory, nullParent);

                TextView tvSymptomName = symptomInventoryView.findViewById(R.id.tvSymptomName);
                TextView tvSymptomRange = symptomInventoryView.findViewById(R.id.tvSymptomRange);
                ImageView increasedIcon = symptomInventoryView.findViewById(R.id.increasedIcon);
                tvSymptomName.setText(inventory.getItemName());
                tvSymptomRange.setText("Normal Range: " + inventory.getRangeValue());

                if (inventory.getFormatedTextEncoded().equals("INCREASED"))
                    increasedIcon.setVisibility(View.VISIBLE);

                // insert into main view
                llSymptomInventoryLayout.addView(symptomInventoryView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        public void clearListItem() {
            tvEnteredDate.setText("");
            llSymptomInventoryLayout.removeAllViews();
        }
    }

}
