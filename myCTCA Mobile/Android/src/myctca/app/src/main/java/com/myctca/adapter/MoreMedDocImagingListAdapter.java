package com.myctca.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.myctca.interfaces.MoreMedDocImagingListRecyclerViewListener;
import com.myctca.model.ImagingDoc;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;


public class MoreMedDocImagingListAdapter extends RecyclerView.Adapter<MoreMedDocImagingListAdapter.MoreMedDocImagingListHolder> {

    private static final String TAG = MoreMedDocImagingListAdapter.class.getSimpleName();
    private final Context context;
    private final SessionFacade sessionFacade;
    private List<ImagingDoc> mImagingDocs = new ArrayList<>();
    private MoreMedDocImagingListRecyclerViewListener rvListener;
    private String searchText = "";

    public MoreMedDocImagingListAdapter(Context context, List<ImagingDoc> imagingDocs, MoreMedDocImagingListRecyclerViewListener rvListener) {
        Log.d(TAG, "ADAPTER: " + imagingDocs);
        if (mImagingDocs != null) {
            mImagingDocs = imagingDocs;
        }
        sessionFacade = new SessionFacade();
        this.context = context;
        this.rvListener = rvListener;
    }

    @Override
    public MoreMedDocImagingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreMedDocImagingListHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(MoreMedDocImagingListHolder holder, int position) {

        ImagingDoc imagingDoc = mImagingDocs.get(position);
        holder.bind(imagingDoc, rvListener, searchText);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mImagingDocs.size());
        return mImagingDocs.size();
    }

    private List<ImagingDoc> getImagingDocs() {
        return sessionFacade.getmImagingDocs();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<ImagingDoc> filteredImagingDocs = new ArrayList<>();
        for (ImagingDoc imagingDoc : getImagingDocs()) {
            if (imagingDoc.itemName.toLowerCase().contains(s)) {
                filteredImagingDocs.add(imagingDoc);
            }
        }
        this.mImagingDocs = filteredImagingDocs;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        this.mImagingDocs = getImagingDocs();
        searchText = "";
        notifyDataSetChanged();
    }

    class MoreMedDocImagingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MoreMedDocImagingListRecyclerViewListener rvListener;
        private ImagingDoc mImagingDoc;

        private TextView tvName;
        private TextView tvAuthor;

        private MoreMedDocImagingListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_med_doc_list, parent, false));

            tvName = itemView.findViewById(R.id.med_doc_list_name);
            tvAuthor = itemView.findViewById(R.id.med_doc_list_author);
        }

        public void bind(ImagingDoc imagingDoc, MoreMedDocImagingListRecyclerViewListener rvListener, String searchText) {

            this.mImagingDoc = imagingDoc;
            this.rvListener = rvListener;
            itemView.setOnClickListener(this);

            String titleText = mImagingDoc.itemName;
            String authorText = "Created " + mImagingDoc.getSlashedDateFullYearString();

            if (!searchText.isEmpty()) {
                tvName.setText(highlightSearchedText(titleText, searchText));
            } else {
                tvName.setText(titleText);
            }

            tvAuthor.setText(authorText);
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
            rvListener.moreMedDocImagingListRecyclerViewClicked(v, this.getLayoutPosition(), this.mImagingDoc);
        }
    }
}
