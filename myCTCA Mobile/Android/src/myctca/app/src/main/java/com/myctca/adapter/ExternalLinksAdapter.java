package com.myctca.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MyResourcesActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.MoreMyResourcesWebViewsFragment;
import com.myctca.model.ExternalLink;

import java.util.ArrayList;
import java.util.List;

public class ExternalLinksAdapter extends RecyclerView.Adapter<ExternalLinksAdapter.ExternalLinksHolder> {

    private final static String TAG = ExternalLinksAdapter.class.getSimpleName();
    private final Activity activity;
    private final ExternalLinksAdapterInterface listener;
    private List<ExternalLink> externalLinkList = new ArrayList<>();

    public ExternalLinksAdapter(ExternalLinksAdapterInterface listener, Activity activity, List<ExternalLink> externalLinks) {
        if (externalLinks != null) {
            externalLinkList = externalLinks;
        }
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    public ExternalLinksHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        return new ExternalLinksHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(ExternalLinksHolder holder, int position) {
        ExternalLink externalLink = externalLinkList.get(position);
        holder.bind(externalLink);
    }

    @Override
    public int getItemCount() {
        return externalLinkList.size();
    }

    public interface ExternalLinksAdapterInterface {
        void openExternalLinks(String title, List<ExternalLink> externalLinks);
    }

    class ExternalLinksHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ExternalLink externalLink;

        private TextView tvExternalLinkTitle;

        private ExternalLinksHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_external_link, parent, false));

            tvExternalLinkTitle = itemView.findViewById(R.id.external_link_title);
        }

        public void bind(ExternalLink link) {
            this.externalLink = link;

            String linkTitle = link.getTitle();
            tvExternalLinkTitle.setText(linkTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (externalLink.getChildren() == null || externalLink.getChildren().isEmpty()) {
                if (!externalLink.getIsDownload())
                    ((MyResourcesActivity) activity).addFragment(new MoreMyResourcesWebViewsFragment(), externalLink.getUrl(), externalLink.getTitle(), false);
                else
                    ((MyResourcesActivity) activity).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + "/" + externalLink.getUrl(), externalLink.getTitle(), false);
            } else
                listener.openExternalLinks(externalLink.getTitle(),this.externalLink.getChildren());
        }
    }
}
