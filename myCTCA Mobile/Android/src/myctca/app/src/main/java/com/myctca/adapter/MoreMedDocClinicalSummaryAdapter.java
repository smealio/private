package com.myctca.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.fragment.MoreMedDocClinicalSummaryDetailFragment;
import com.myctca.model.ClinicalSummary;

import java.util.ArrayList;
import java.util.List;

public class MoreMedDocClinicalSummaryAdapter extends RecyclerView.Adapter<MoreMedDocClinicalSummaryAdapter.MoreClinicalSummaryHolder> {

    private final Context context;
    private final MoreMedDocClinicalSummaryAdapterListener listener;
    private boolean showCheckboxes = false;
    private List<ClinicalSummary> mCriticalSummaryAR = new ArrayList<>();
    private List<String> selectedClinicalSummaryIDs = new ArrayList<>();

    public MoreMedDocClinicalSummaryAdapter(Context context, MoreMedDocClinicalSummaryAdapterListener listener, List<ClinicalSummary> clinicalSummaryAR) {
        this.context = context;
        this.listener = listener;
        if (clinicalSummaryAR != null) {
            mCriticalSummaryAR = clinicalSummaryAR;
        }
    }

    public void setShowCheckboxes(boolean showCheckboxes){
        this.showCheckboxes = showCheckboxes;
    }

    @Override
    public MoreClinicalSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreClinicalSummaryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(MoreClinicalSummaryHolder holder, int position) {
        ClinicalSummary clinicalsummary = mCriticalSummaryAR.get(position);
        holder.bind(clinicalsummary);
    }

    @Override
    public int getItemCount() {
        return mCriticalSummaryAR.size();
    }

    public interface MoreMedDocClinicalSummaryAdapterListener {

        void addFragment(MoreMedDocClinicalSummaryDetailFragment newInstance, List<String> selectedClinicalSummary, String title);

        void updateSelectedClinicalSummaryArray(List<String> selectedClinicalSummaryIDs);
    }

    protected class MoreClinicalSummaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ClinicalSummary mClinicalSummary;

        private TextView tvTitle;
        private TextView tvCreatedDate;
        private CheckBox cbClinicalSummaryItem;
        private LinearLayout llClinicalSummaryItem;
        private LinearLayout llOuterclinicalSummaryItem;

        private MoreClinicalSummaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_clinical_summary, parent, false));
            tvTitle = itemView.findViewById(R.id.clinical_summary_title);
            tvCreatedDate = itemView.findViewById(R.id.clinical_summary_created_date);
            cbClinicalSummaryItem = itemView.findViewById(R.id.cb_clinical_summary_item);
            llClinicalSummaryItem = itemView.findViewById(R.id.ll_clinical_summary_item);
            llOuterclinicalSummaryItem = itemView.findViewById(R.id.ll_outer_clinical_summary_item);
        }

        public void bind(ClinicalSummary criticalSummary) {
            this.mClinicalSummary = criticalSummary;

            llOuterclinicalSummaryItem.setOnClickListener(this);
            if (showCheckboxes) {
                cbClinicalSummaryItem.setClickable(false);
                cbClinicalSummaryItem.setChecked(false);
                llClinicalSummaryItem.startAnimation(outToRightAnimation());
                cbClinicalSummaryItem.setVisibility(View.VISIBLE);
            } else {
                llClinicalSummaryItem.startAnimation(inFromRightAnimation());
                cbClinicalSummaryItem.setVisibility(View.GONE);
            }

            String titleText = mClinicalSummary.getTitle();
            String createdText = "Created " + mClinicalSummary.getSlashFormatCreatedString();

            tvTitle.setText(HtmlCompat.fromHtml(titleText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            tvCreatedDate.setText(createdText);
        }

        @Override
        public void onClick(View v) {
            if (showCheckboxes) {
                cbClinicalSummaryItem.setChecked(!cbClinicalSummaryItem.isChecked());
                if (cbClinicalSummaryItem.isChecked()) {
                    selectedClinicalSummaryIDs.add(mClinicalSummary.getUniqueId());
                } else {
                    selectedClinicalSummaryIDs.remove(mClinicalSummary.getUniqueId());
                }
                listener.updateSelectedClinicalSummaryArray(selectedClinicalSummaryIDs);
            } else {
                List<String> selectedClinicalSummary = new ArrayList<>();
                selectedClinicalSummary.add(this.mClinicalSummary.getUniqueId());
                listener.addFragment(new MoreMedDocClinicalSummaryDetailFragment(), selectedClinicalSummary, this.mClinicalSummary.getTitle());
            }
        }

        private Animation outToRightAnimation() {
            Animation outtoRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -0.05f,
                    Animation.RELATIVE_TO_PARENT, 0.005f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            outtoRight.setDuration(500);
            outtoRight.setInterpolator(new AccelerateInterpolator());
            return outtoRight;
        }

        private Animation inFromRightAnimation() {
            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, +0.1f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromRight.setDuration(500);
            inFromRight.setInterpolator(new AccelerateInterpolator());
            return inFromRight;
        }

    }

}
