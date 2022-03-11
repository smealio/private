package com.myctca.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreHealthHistoryListActivity;
import com.myctca.adapter.MoreHealthHistoryAllergyAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.Allergy;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.service.HealthHistoryService;
import com.myctca.service.SessionFacade;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreHealthHistoryAllergiesListFragment extends Fragment implements HealthHistoryService.HealthHistoryServiceListener {

    private static final String TAG = "myCTCA-MOREHHList";
    private static final String PURPOSE = "ALLERGIES";

    private SwipeRefreshLayout mAllergiesListRefreshLayout;
    private CTCARecyclerView mAllergiesListRecyclerView;
    private MoreHealthHistoryAllergyAdapter mAllergiesListAdapter;
    private MenuItem downloadAllergies;
    private SearchView searchView;
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_health_history_allergies_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mAllergiesListRecyclerView = view.findViewById(R.id.more_health_history_allergies_list_recycler_view);
        mAllergiesListRefreshLayout = view.findViewById(R.id.more_health_history_allergies_list_swipe_refresh);
        mEmptyView = view.findViewById(R.id.empty_view);
        TextView emptyMessage = view.findViewById(R.id.more_health_history_allergies_list_empty_text);
        emptyMessage.setText(getString(R.string.empty_list_message, context.getString(R.string.more_health_history_title), ": " + context.getString(R.string.more_health_history_allergies)));

        // Pull To Refresh
        mAllergiesListRefreshLayout.setOnRefreshListener(this::refreshItems);

        setAllergyRecyclerView();
        downloadAllergiesData(getString(R.string.get_allergies_indicator));
    }

    private void setAllergyRecyclerView() {
        mAllergiesListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAllergiesListRecyclerView.setLayoutManager(layoutManager);
        mAllergiesListRecyclerView.setEmptyView(mEmptyView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_health_history_allergies, menu);
        initializeAllergyMenuItems(menu);
        searchAllergies();
        setMenuButtons();
    }

    private void initializeAllergyMenuItems(Menu menu) {
        downloadAllergies = menu.findItem(R.id.toolbar_allergies_download);
        MenuItem searchItem = menu.findItem(R.id.allergies_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(view -> {
            downloadAllergies.setVisible(false);
            mAllergiesListRefreshLayout.setEnabled(false);
        });
        searchView.setOnCloseListener(() -> {
            ((MoreHealthHistoryListActivity) context).invalidateOptionsMenu();
            mAllergiesListRefreshLayout.setEnabled(true);
            return false;
        });
    }

    private void searchAllergies() {
        SearchView.SearchAutoComplete etSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        etSearch.setHintTextColor(Color.GRAY);
        etSearch.setTextColor(Color.BLACK);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAllergiesListAdapter.filterItems(s);
                return false;
            }
        });
    }

    private void setMenuButtons() {
        ImageView searchButton = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageResource(R.drawable.search_icon);
        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_action_close_green);
        if (sessionFacade.getHealthHistoryType(PURPOSE).isEmpty()) {
            downloadAllergies.getIcon().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            downloadAllergies.setEnabled(false);

            searchButton.setColorFilter(Color.LTGRAY);
            searchButton.setEnabled(false);
        } else {
            downloadAllergies.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            downloadAllergies.setEnabled(true);

            searchButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            closeButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            searchButton.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAllergiesListAdapter != null)
            mAllergiesListAdapter.removeFilter();
        mAllergiesListRefreshLayout.setEnabled(true);
    }

    private void updateUI(List<Allergy> list) {
        mAllergiesListAdapter = new MoreHealthHistoryAllergyAdapter(context, list);
        mAllergiesListRecyclerView.setAdapter(mAllergiesListAdapter);
        mAllergiesListRecyclerView.getRecycledViewPool().clear();
        mAllergiesListAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mAllergiesListRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mAllergiesListRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        sessionFacade.clearHealthHistoryType(PURPOSE);
        downloadAllergiesData(getString(R.string.refresh_allergies_indicator));
        onRefreshItemsLoadComplete();
    }

    public void downloadAllergiesData(String indicatorStr) {
        if (context != null)
            ((MoreHealthHistoryListActivity) context).showActivityIndicator(indicatorStr);

        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_allergies);
        Log.d(TAG, "URL: " + url);
        sessionFacade.downloadHealthHistoryTypeList(this, context, url, PURPOSE);
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "MoreHealthHistoryAllergiesListFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mAllergiesListRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreHealthHistoryAllergiesListFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_ALLERGIES_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreHealthHistoryListActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public <T> void notifyFetchSuccess(List<T> list, String purpose) {
        updateUI((List<Allergy>) list);
        if (searchView != null)
            setMenuButtons();
        if (context != null)
            ((MoreHealthHistoryListActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String message, String purpose) {
        if (context != null)
            ((MoreHealthHistoryListActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }
}
