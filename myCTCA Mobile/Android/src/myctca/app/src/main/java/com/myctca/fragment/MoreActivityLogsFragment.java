package com.myctca.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.R;
import com.myctca.activity.MoreActivityLogsActivity;
import com.myctca.adapter.MoreActivityLogsAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.ActivityLogItem;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.service.ActivityLogsService;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreActivityLogsFragment extends Fragment implements ActivityLogsService.ActivityLogsListener {

    private static final String TAG = "myCTCA-MOREACTIVITYLOGS";
    private static final String INFINITE_SCROLL = "Infinite Scroll";
    private static final String RETRIEVE_LOGS = "Retrieve Logs";
    private static final String REFRESH_LOGS = "Refresh Logs";
    private static final String REMOVE_FILTERS = "Remove Filters";
    private static final int TAKE = 30;
    private List<Object> activityLogsArray = new ArrayList<>();
    private boolean isScrolling = false;
    private LinearLayout llLoadMore;
    private String selectedDate;
    private SwipeRefreshLayout mActivityLogsRefreshLayout;
    private CTCARecyclerView mActivityLogsRecyclerView;
    private MoreActivityLogsAdapter mActivityLogsAdapter;
    private int totalItems = 0;
    private int skip = 0;
    private LinearLayoutManager layoutManager;
    private String nextDate;
    private TextView mEmptyTextView;
    private View mEmptyView;
    private SessionFacade sessionFacade;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_activity_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        // Find Views
        mActivityLogsRecyclerView = view.findViewById(R.id.more_activity_logs_recycler_view);
        mActivityLogsRefreshLayout = view.findViewById(R.id.more_activity_logs_swipe_refresh);
        llLoadMore = view.findViewById(R.id.loadMoreLayout);

        // Empty View
        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyTextView = view.findViewById(R.id.more_activity_logs_empty_text);
        mEmptyTextView.setText(getString(R.string.empty_list_message, context.getString(R.string.more_activity_logs_title), ""));

        // Pull To Refresh
        mActivityLogsRefreshLayout.setOnRefreshListener(this::refreshItems);

        mActivityLogsAdapter = new MoreActivityLogsAdapter(activityLogsArray);
        setActivityLogsRecyclerView();
        downloadActivityLogs(RETRIEVE_LOGS);
    }

    private void setActivityLogsRecyclerView() {
        mActivityLogsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mActivityLogsRecyclerView.setLayoutManager(layoutManager);
        mActivityLogsRecyclerView.setAdapter(mActivityLogsAdapter);
        mActivityLogsRecyclerView.setEmptyView(mEmptyView);
    }

    public void applyFilterOnActivityLogs(String etFilterUsername, String etFilterMessage) {
        sessionFacade.applyFilterOnActivityLogs(etFilterUsername, etFilterMessage, selectedDate, nextDate);
        //update list
        activityLogsArray.clear();
        skip = 0;
        downloadActivityLogs(RETRIEVE_LOGS);
    }

    public void removeFilters() {
        activityLogsArray.clear();
        skip = 0;
        downloadActivityLogs(REMOVE_FILTERS);
    }

    public void setSelectedAndNextDate(String selectedDate, String nextDate) {
        this.selectedDate = selectedDate;
        this.nextDate = nextDate;
    }

    private void updateUI(Map<Date, List<ActivityLogItem>> activityLogs, List<Date> activityLogsDates) {
        for (int datePos = 0; datePos < activityLogsDates.size(); datePos++) {
            Date activityLogsDate = activityLogsDates.get(datePos);
            List<ActivityLogItem> activityLog = activityLogs.get(activityLogsDate);
            if (!activityLogsArray.contains(activityLogsDate))
                activityLogsArray.add(activityLogsDate);
            activityLogsArray.addAll(activityLog);
        }
        mActivityLogsRecyclerView.getRecycledViewPool().clear();
        mActivityLogsAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mActivityLogsRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        }
        recyclerViewListener();
    }

    private void recyclerViewListener() {
        mActivityLogsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = layoutManager.getItemCount();

                if (isScrolling && (layoutManager.findLastCompletelyVisibleItemPosition() == totalItems - 1)) {
                    isScrolling = false;
                    skip += 30;
                    llLoadMore.setVisibility(View.VISIBLE);
                    downloadActivityLogs(INFINITE_SCROLL);
                }
            }
        });
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        skip = 0;
        downloadActivityLogs(REFRESH_LOGS);
        onRefreshItemsLoadComplete();
    }

    public void downloadActivityLogs(String purpose) {
        String indicatorStr = "";
        if (purpose.equals(RETRIEVE_LOGS) || purpose.equals(REMOVE_FILTERS)) {
            indicatorStr = context.getString(R.string.retrieve_activity_logs);
        } else indicatorStr = context.getString(R.string.refresh_activity_logs);
        if ((purpose.equals(RETRIEVE_LOGS) || purpose.equals(REFRESH_LOGS) || purpose.equals(REMOVE_FILTERS)) && context != null)
            ((MoreActivityLogsActivity) context).showActivityIndicator(indicatorStr);

        sessionFacade.downloadActivityLogs(context, this, skip, TAKE, purpose);
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "MoreHealthHistoryVitalsListFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mActivityLogsRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreActivityLogsFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_ACTIVITY_LOGS_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    if (context != null)
                        ((MoreActivityLogsActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(Map<Date, List<ActivityLogItem>> activityLogs, List<Date> activityLogsDates) {
        llLoadMore.setVisibility(View.GONE);
        if (activityLogs.isEmpty()) {
            mEmptyTextView.setText(getString(R.string.more_no_activity_logs));
        }
        updateUI(activityLogs, activityLogsDates);
        if (context != null)
            ((MoreActivityLogsActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String message) {
        if (context != null)
            ((MoreActivityLogsActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }

    public void setRefreshState() {
        mActivityLogsRefreshLayout.setEnabled(!mActivityLogsRefreshLayout.isEnabled());
    }
}
