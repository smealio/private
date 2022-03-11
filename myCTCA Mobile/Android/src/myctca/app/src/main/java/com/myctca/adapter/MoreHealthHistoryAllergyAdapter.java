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
import com.myctca.model.Allergy;

import java.util.ArrayList;
import java.util.List;

public class MoreHealthHistoryAllergyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MoreHealthHistoryAllergyAdapter.class.getSimpleName();
    private final Context context;
    private List<Allergy> allergyList = new ArrayList<>();
    private String searchText = "";

    public MoreHealthHistoryAllergyAdapter(Context context, List<Allergy> allergyList) {
        this.context = context;
        if (allergyList != null)
            this.allergyList = allergyList;
    }

    @Override
    public MoreHealthHistoryAllergiesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreHealthHistoryAllergiesListHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof MoreHealthHistoryAllergiesListHolder) {
                MoreHealthHistoryAllergiesListHolder vHolder = (MoreHealthHistoryAllergiesListHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof MoreHealthHistoryAllergiesListHolder) {

                Allergy allergy = allergyList.get(position);
                MoreHealthHistoryAllergiesListHolder vHolder = (MoreHealthHistoryAllergiesListHolder) holder;
                vHolder.bind(allergy, searchText);
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return allergyList.size();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<Allergy> filteredAllergies = new ArrayList<>();
        for (Allergy allergy : AppSessionManager.getInstance().getmAllergies()) {
            if (allergy.getSubstance() != null && allergy.getSubstance().toLowerCase().contains(s) ||
                    allergy.getReactionSeverity() != null && allergy.getReactionSeverity().toLowerCase().contains(s) ||
                    allergy.getStatus() != null && allergy.getStatus().toLowerCase().contains(s)) {
                filteredAllergies.add(allergy);
            }
        }
        this.allergyList = filteredAllergies;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        this.allergyList = AppSessionManager.getInstance().getmAllergies();
        searchText = "";
        notifyDataSetChanged();
    }


    private class MoreHealthHistoryAllergiesListHolder extends RecyclerView.ViewHolder {

        private TextView tvSubstance;
        private TextView tvStatus;
        private TextView tvReaction;

        private MoreHealthHistoryAllergiesListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_allergy, parent, false));
            tvSubstance = itemView.findViewById(R.id.allergy_substance);
            tvStatus = itemView.findViewById(R.id.allergy_status);
            tvReaction = itemView.findViewById(R.id.allergy_reaction_severity);
        }

        public void bind(Allergy allergy, String searchText) {
            MoreHealthHistoryListActivity c = (MoreHealthHistoryListActivity) context;
            tvSubstance.setText(c.highlightSearchedText(allergy.getSubstance() == null
                    || allergy.getSubstance().equals("null")
                    ? "" : allergy.getSubstance(), searchText));
            tvStatus.setText(c.highlightSearchedText(allergy.getStatus() == null
                    || allergy.getStatus().equals("null")
                    ? "" : allergy.getStatus(), searchText));
            String reactionText = (allergy.getReactionSeverity() == null
                    || allergy.getReactionSeverity().equals("null")
                    ? "" : allergy.getReactionSeverity());
            Spannable reaction = c.highlightSearchedText(reactionText, searchText);
            tvReaction.setText(new SpannableStringBuilder(context.getString(R.string.more_health_history_allergies_reaction) + " ").append(reaction));
        }

        public void clearListItem() {
            tvSubstance.setText("");
            tvStatus.setText("");
            tvReaction.setText("");
        }
    }
}