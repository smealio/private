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
import com.myctca.model.HealthIssue;
import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.List;

public class MoreHealthHistoryHealthIssuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MoreHealthHistoryHealthIssuesAdapter.class.getSimpleName();
    private final Context context;
    private List<HealthIssue> healthIssuesList = new ArrayList<>();
    private String searchText = "";

    public MoreHealthHistoryHealthIssuesAdapter(Context context, List<HealthIssue> healthIssueList) {
        this.context = context;
        if (healthIssueList != null)
            this.healthIssuesList = healthIssueList;
    }

    @Override
    public MoreHealthHistoryHealthIssuesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new MoreHealthHistoryHealthIssuesListHolder(layoutInflater, parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            if (holder instanceof MoreHealthHistoryHealthIssuesListHolder) {
                MoreHealthHistoryHealthIssuesListHolder vHolder = (MoreHealthHistoryHealthIssuesListHolder) holder;
                vHolder.clearListItem();
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreHealthHistoryHealthIssueListFragment:onViewRecycled", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder instanceof MoreHealthHistoryHealthIssuesListHolder) {

                HealthIssue healthIssue = healthIssuesList.get(position);
                MoreHealthHistoryHealthIssuesListHolder vHolder = (MoreHealthHistoryHealthIssuesListHolder) holder;
                vHolder.bind(healthIssue, searchText);
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreHealthHistoryHealthIssueListFragment:onBindViewHolder", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return healthIssuesList.size();
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<HealthIssue> filteredHealthIssues = new ArrayList<>();
        for (HealthIssue healthIssue : AppSessionManager.getInstance().getmHealthIssues()) {
            if (healthIssue.getName().toLowerCase().contains(s) ||
                    healthIssue.getStatus().toLowerCase().contains(s) ||
                    healthIssue.getShortName().toLowerCase().contains(s)) {
                filteredHealthIssues.add(healthIssue);
            }
        }
        this.healthIssuesList = filteredHealthIssues;
        notifyDataSetChanged();
    }

    public void removeFilter() {
        this.healthIssuesList = AppSessionManager.getInstance().getmHealthIssues();
        searchText = "";
        notifyDataSetChanged();
    }


    private class MoreHealthHistoryHealthIssuesListHolder extends RecyclerView.ViewHolder {

        private TextView tvShortName;
        private TextView tvStatusEntered;
        private TextView tvName;

        private MoreHealthHistoryHealthIssuesListHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_health_issues, parent, false));

            tvShortName = itemView.findViewById(R.id.health_issue_short_name);
            tvStatusEntered = itemView.findViewById(R.id.health_issue_status_entered);
            tvName = itemView.findViewById(R.id.health_issue_name);
        }

        public void bind(HealthIssue healthIssue, String searchText) {
            MoreHealthHistoryListActivity c = (MoreHealthHistoryListActivity) context;
            tvShortName.setText(c.highlightSearchedText(healthIssue.getShortName(), searchText));
            Spannable status = c.highlightSearchedText(healthIssue.getStatus(), searchText);
            SpannableStringBuilder statusEnteredText = new SpannableStringBuilder(status).append(context.getString(R.string.more_health_history_health_issues_status_entered, MyCTCADateUtils.getSlashedDateStr(healthIssue.getEnteredDate())));
            tvStatusEntered.setText(statusEnteredText);

            tvName.setText(c.highlightSearchedText(healthIssue.getName(), searchText));
        }

        public void clearListItem() {
            tvShortName.setText("");
            tvStatusEntered.setText("");
            tvName.setText("");
        }
    }

}
