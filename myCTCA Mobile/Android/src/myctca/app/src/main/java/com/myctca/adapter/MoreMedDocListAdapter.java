package com.myctca.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.interfaces.MoreMedDocListRecyclerViewListener;
import com.myctca.model.MedDoc;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;

public class MoreMedDocListAdapter extends RecyclerView.Adapter<MoreMedDocListAdapter.MoreMedDocListHolder> {

    private static final String TAG = MoreMedDocListAdapter.class.getSimpleName();
    private final Context context;
    private final String medDocType;
    private List<MedDoc> mMedDocs = new ArrayList<>();
    private MoreMedDocListRecyclerViewListener rvListener;
    private String searchText = "";
    private SessionFacade sessionFacade;

    public MoreMedDocListAdapter(Context context, List<MedDoc> medDocs, String medDocType, MoreMedDocListRecyclerViewListener rvListener) {
        Log.d(TAG, "ADAPTER: " + medDocs);
        if (mMedDocs != null) {
            mMedDocs = medDocs;
        }
        this.context = context;
        this.medDocType = medDocType;
        sessionFacade = new SessionFacade();
        this.rvListener = rvListener;
    }

    @Override
    public MoreMedDocListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreMedDocListHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(MoreMedDocListHolder holder, int position) {

        MedDoc medDoc = mMedDocs.get(position);
        holder.bind(medDoc, rvListener, searchText);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mMedDocs.size());
        return mMedDocs.size();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<MedDoc> filteredMedDocs = new ArrayList<>();
        for (MedDoc medDoc : sessionFacade.getMedDocs(medDocType)) {
            if (medDoc.getDocumentAuthor().toLowerCase().contains(s) ||
                    medDoc.getDocumentName().toLowerCase().contains(s)) {
                filteredMedDocs.add(medDoc);
            }
        }
        this.mMedDocs = filteredMedDocs;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        this.mMedDocs = sessionFacade.getMedDocs(medDocType);
        searchText = "";
        notifyDataSetChanged();
    }

    protected class MoreMedDocListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MoreMedDocListRecyclerViewListener rvListener;
        private MedDoc mMedDoc;

        private TextView tvName;
        private TextView tvAuthor;

        private MoreMedDocListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_med_doc_list, parent, false));

            tvName = itemView.findViewById(R.id.med_doc_list_name);
            tvAuthor = itemView.findViewById(R.id.med_doc_list_author);
        }

        public void bind(MedDoc medDoc, MoreMedDocListRecyclerViewListener rvListener, String searchText) {

            this.mMedDoc = medDoc;
            this.rvListener = rvListener;
            itemView.setOnClickListener(this);

            String titleText = mMedDoc.getDocumentName();

            if (!searchText.isEmpty()) {
                tvName.setText(highlightSearchedText(titleText, searchText));

                Spannable docAuthor = highlightSearchedText(mMedDoc.getDocumentAuthor(), searchText);
                SpannableStringBuilder authorText = new SpannableStringBuilder("Created " + mMedDoc.getSlashedDateFullYearString() + " by ").append(docAuthor);
                if (!TextUtils.isEmpty(mMedDoc.getDocumentAuthorOccupationCode())) {
                    authorText.append(" (" + mMedDoc.getDocumentAuthorOccupationCode() + ")");
                }
                tvAuthor.setText(authorText);
            } else {
                tvName.setText(titleText);
                String authorText = "Created " + mMedDoc.getSlashedDateFullYearString() + " by " + mMedDoc.getDocumentAuthor();
                if (!TextUtils.isEmpty(mMedDoc.getDocumentAuthorOccupationCode())) {
                    authorText += " (" + mMedDoc.getDocumentAuthorOccupationCode() + ")";
                }
                tvAuthor.setText(authorText);
            }
        }

        private Spannable highlightSearchedText(String fullText, String searchText) {
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
        public void onClick(View v) {
            rvListener.moreMedDocListRecyclerViewClicked(v, this.getLayoutPosition(), this.mMedDoc);
        }
    }
}
