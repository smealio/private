package com.myctca.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.LabResult;
import com.myctca.model.LabSet;
import com.myctca.model.LabSetDetail;
import com.myctca.service.SessionFacade;

public class LabsDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = LabsDetailAdapter.class.getSimpleName();
    private static final int FOOTER_VIEW = 1;
    private final Context context;

    private LabResult labResult;

    private Activity mActivity;

    public LabsDetailAdapter(Context context, Activity activity, LabResult labResult) {
        this.context = context;
        mActivity = activity;
        this.labResult = labResult;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        Log.d(TAG, "onCreateViewHolder: viewtype: " + viewType + "::: getItemCount(): " + getItemCount());
        if (viewType == FOOTER_VIEW) {
            return new LabsDetailFooter(layoutInflater, parent);
        }

        return new LabsDetailHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof LabsDetailHolder) {
                LabsDetailHolder vHolder = (LabsDetailHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("LabsDetailAdapter:onViewRecycled();", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof LabsDetailHolder) {
                LabSet labSet = labResult.getSummary().get(position);
                LabsDetailHolder vHolder = (LabsDetailHolder) holder;
                vHolder.bind(mActivity, labSet);
            } else if (holder instanceof LabsDetailFooter) {
                LabsDetailFooter vHolder = (LabsDetailFooter) holder;
                vHolder.bind(footerText());
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("LabsDetailFragment:onBindViewHolder;", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return labResult.getSummary().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == labResult.getSummary().size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }

    public void setLabResult(LabResult labResult) {
        this.labResult = labResult;
    }

    private String footerText() {

        String disclaimer = "";

        if (!labResult.getCollectedBy().equals("CTCA")) {
            disclaimer += context.getString(R.string.labs_detail_disclaimer_external);
            disclaimer += "\n\n";
        }
        if (!isLessThan24HoursAgo()) {
            disclaimer += context.getString(R.string.labs_detail_disclaimer_24);
            disclaimer += "\n\n";
        }
        disclaimer += context.getString(R.string.labs_detail_disclaimer_unofficial);

        return disclaimer;
    }

    public boolean isLessThan24HoursAgo() {
        return new SessionFacade().isLabResultLessThan24HoursAgo(labResult.getPerformedDate());
    }

    private class LabsDetailHolder extends RecyclerView.ViewHolder {

        private TextView mLabSetName;

        private LinearLayout mLabSetLinearLayout;

        private LabsDetailHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_labs_detail, parent, false));

            mLabSetName = itemView.findViewById(R.id.lab_set_name);
            mLabSetLinearLayout = itemView.findViewById(R.id.lab_set_list_view);
        }

        public void clearListItem() {
            mLabSetName.setText("");
            mLabSetLinearLayout.removeAllViews();
        }

        public void bind(Activity activity, LabSet labSet) {
            mLabSetName.setText(labSet.getName());

            LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int idx = 0;
            for (LabSetDetail detail : labSet.getDetail()) {

                View detailView = mInflater.inflate(R.layout.list_item_labs_set, null);

                TextView tvItemName = detailView.findViewById(R.id.lab_set_item_name);
                TextView tvNormalRange = detailView.findViewById(R.id.lab_set_normal_range);
                TextView tvResult = detailView.findViewById(R.id.lab_set_result);
                ImageView ivResult = detailView.findViewById(R.id.lab_set_result_image);
                TextView tvNotes = detailView.findViewById(R.id.lab_set_notes);
                View separator = detailView.findViewById(R.id.lab_set_separator);

                tvItemName.setText(detail.getItemName());

                String normalRange = detail.getNormalRange();
                if (isStringEmpty(normalRange)) {
                    tvNormalRange.setVisibility(View.GONE);
                } else {
                    tvNormalRange.setVisibility(View.VISIBLE);
                    String normalRangeStr = context.getString(R.string.labs_detail_normal_range) + " " + normalRange;
                    tvNormalRange.setText(normalRangeStr);
                }

                String resultStr = detail.getResult();
                if (isStringEmpty(resultStr)) {
                    tvResult.setVisibility(View.GONE);
                    ivResult.setVisibility(View.GONE);
                } else {
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(resultStr);

                    String abCode = detail.getAbnormalityCodeCalculated();
                    Log.d(TAG, "abnormalityCodeCalculated: " + abCode);
                    if (abCode.equals("N") || abCode.isEmpty()) {
                        tvResult.setTextColor(ContextCompat.getColor(context, R.color.black));
                        ivResult.setVisibility(View.GONE);
                    } else {
                        tvResult.setTextColor(ContextCompat.getColor(context, R.color.red));
                        ivResult.setVisibility(View.VISIBLE);
                        ImageViewCompat.setImageTintList(ivResult, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                        if (abCode.equals("H")) {
                            ivResult.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                        }
                        if (abCode.equals("L")) {
                            ivResult.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                        }
                    }
                }

                String notes = detail.getNotes();
                if (isStringEmpty(notes)) {
                    tvNotes.setVisibility(View.GONE);
                } else {
                    tvNotes.setVisibility(View.VISIBLE);
                    tvNotes.setText(notes);
                }

                // insert into main view
                mLabSetLinearLayout.addView(detailView, idx, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                idx++;
                if (idx == labSet.getDetail().size()) {
                    separator.setVisibility(View.GONE);
                }
            }
        }

        private boolean isStringEmpty(String testString) {
            return testString == null || testString.isEmpty() || ("null").equals(testString);
        }
    }

    private class LabsDetailFooter extends RecyclerView.ViewHolder {

        private TextView mDisclaimerText;

        private LabsDetailFooter(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_labs_disclaimer, parent, false));

            mDisclaimerText = itemView.findViewById(R.id.labs_disclaimer_text);
        }

        public void bind(String disclaimerText) {
            mDisclaimerText.setText(disclaimerText);
        }
    }
}
