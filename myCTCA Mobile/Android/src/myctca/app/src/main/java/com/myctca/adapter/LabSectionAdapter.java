package com.myctca.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.LabsDetailActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.LabResult;
import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class LabSectionAdapter extends Section {

    private static final String TAG = LabSectionAdapter.class.getSimpleName();
    private final Context context;
    private final LabsSectionListener listener;
    private List<LabResult> labResults = new ArrayList<>();
    private String searchText = "";

    public LabSectionAdapter(Context context, List<LabResult> data, LabsSectionListener listener) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.list_item_labs)
                .headerResourceId(R.layout.section_labs)
                .build());

        this.context = context;
        this.listener = listener;
        if (data != null)
            this.labResults = data;
    }

    @Override
    public int getContentItemsTotal() {
        return this.labResults.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new LabsListItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        LabsListItemHolder labsListItemHolder = (LabsListItemHolder) holder;

        LabResult result = labResults.get(position);
        labsListItemHolder.bind(result, searchText);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new LabsHeader(view);
    }

    private void showLabResultDetail(LabResult labResult) {
        Intent labsDetailIntent = LabsDetailActivity.newIntent(context, labResult);
        context.startActivity(labsDetailIntent);
    }

    public void removeFilter() {
        this.labResults = AppSessionManager.getInstance().getLabResults();
        searchText = "";
        listener.notifyDataSetChanged();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        LabsHeader headerHolder = (LabsHeader) holder;

        headerHolder.cvLatestResult.setOnClickListener(v -> {
            //perform your action here
            Log.d(TAG, "HEADER CLICK");
            showLabResultDetail(labResults.get(0));
        });
        if (!labResults.isEmpty()) {
            LabResult latestResult = labResults.get(0);
            headerHolder.tvResultDate.setText(MyCTCADateUtils.getDayDateStr(latestResult.getPerformedDate()));
            headerHolder.tvResultName.setText(latestResult.getSummaryNames("\n"));
            String collectedBy = context.getString(R.string.labs_section_collected_by) + " " + latestResult.getCollectedBy();
            headerHolder.tvResultCollectedBy.setText(collectedBy);

            SearchView.SearchAutoComplete etSearch = headerHolder.searchLabResults.findViewById(androidx.appcompat.R.id.search_src_text);
            etSearch.setHintTextColor(Color.GRAY);
            etSearch.setTextColor(Color.BLACK);
            headerHolder.searchLabResults.setOnSearchClickListener(view -> {
                listener.refreshEnable(false);
                headerHolder.tvSectionTitle.setVisibility(View.GONE);
            });
            headerHolder.searchLabResults.setOnCloseListener(() -> {
                listener.refreshEnable(true);
                headerHolder.tvSectionTitle.setVisibility(View.VISIBLE);
                return false;
            });
            headerHolder.searchLabResults.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    filterItems(s);
                    return false;
                }
            });
        }
    }

    private void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<LabResult> labResults = new ArrayList<>();
        for (LabResult labResult : AppSessionManager.getInstance().getLabResults()) {
            if (labResult.getCollectedBy() != null && labResult.getCollectedBy().toLowerCase().contains(s)
                    || !labResult.getSummaryNames("\n").isEmpty() && labResult.getSummaryNames("\n").toLowerCase().contains(s)) {
                labResults.add(labResult);
            }
        }
        this.labResults = labResults;
        listener.notifyDataSetChanged();
    }

    public interface LabsSectionListener {
        void notifyDataSetChanged();

        void refreshEnable(boolean b);
    }


    private class LabsHeader extends RecyclerView.ViewHolder {

        private final TextView tvSectionTitle;
        private CardView cvLatestResult;
        private TextView tvResultDate;
        private TextView tvResultName;
        private TextView tvResultCollectedBy;
        private SearchView searchLabResults;

        private LabsHeader(View view) {
            super(view);
            tvSectionTitle = view.findViewById(R.id.section_title);
            cvLatestResult = view.findViewById(R.id.latest_result_card);
            tvResultDate = view.findViewById(R.id.latest_result_date);
            tvResultName = view.findViewById(R.id.latest_result_name);
            tvResultCollectedBy = view.findViewById(R.id.latest_result_collected_by);
            searchLabResults = view.findViewById(R.id.search_lab_results);
        }
    }

    private class LabsListItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mLabsPerformDate;
        private TextView mLabsName;
        private TextView mLabsCollected;

        private LabResult result;

        private LabsListItemHolder(View view) {
            super(view);

            mLabsPerformDate = itemView.findViewById(R.id.labs_perform_date);
            mLabsName = itemView.findViewById(R.id.labs_name);
            mLabsCollected = itemView.findViewById(R.id.labs_collected_by);
        }

        private void clearListItems() {

            // Clear the main body of the message item
            mLabsPerformDate.setText("");
            mLabsName.setText("");
            mLabsCollected.setText("");
        }

        public void bind(LabResult result, String searchText) {

            // Clear  list item properties
            clearListItems();

            this.result = result;
            // Set Text
            mLabsPerformDate.setText(MyCTCADateUtils.getDayDateStr(this.result.getPerformedDate()));
            mLabsName.setText(highlightSearchedText(this.result.getSummaryNames(", "), searchText));
            String collectedBy = context.getString(R.string.labs_section_collected_by) + " " + this.result.getCollectedBy();
            mLabsCollected.setText(highlightSearchedText(collectedBy, searchText));
            itemView.setOnClickListener(this);
        }

        public Spannable highlightSearchedText(String fullText, String searchText) {
            int startPos = fullText.toLowerCase().indexOf(searchText.toLowerCase());
            int endPos = startPos + searchText.length();
            Spannable spannable = new SpannableString(fullText);
            if (startPos != -1) {
                spannable.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context,
                        R.color.highlight_text_color)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,
                        R.color.white)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannable;
        }


        @Override
        public void onClick(View view) {
            Log.d(TAG, "LabsListItemHolder onCLick: " + getLayoutPosition() + ":::" + this.result.getSummaryNames(", "));
            showLabResultDetail(this.result);
        }
    }

}
