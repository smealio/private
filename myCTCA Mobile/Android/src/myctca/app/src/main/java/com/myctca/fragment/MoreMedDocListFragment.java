package com.myctca.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreMedDocDetailActivity;
import com.myctca.activity.MoreMedDocListActivity;
import com.myctca.adapter.MoreMedDocListAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.interfaces.MoreMedDocListRecyclerViewListener;
import com.myctca.model.MedDoc;
import com.myctca.model.MedDocType;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedDocListFragment extends Fragment implements MoreMedDocListRecyclerViewListener, MoreMedicalDocumentsService.MoreMedDocListenerGet {

    private static final String TAG = "myCTCA-MedDocs";
    private final String retrieve = "Retrieve";
    private final String refresh = "Refresh";
    public String medDocType = "";
    private SwipeRefreshLayout mMedDocListRefreshLayout;
    private CTCARecyclerView mMedDocListRecyclerView;
    private MoreMedDocListAdapter mMedDocListAdapter;
    private SearchView searchView;
    private Context context;
    private View mEmptyView;
    private TextView mEmptyTextView;
    private SessionFacade sessionFacade;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_med_doc_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mMedDocListRecyclerView = view.findViewById(R.id.more_med_doc_list_recycler_view);
        mMedDocListRefreshLayout = view.findViewById(R.id.more_med_doc_list_swipe_refresh);
        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyTextView = view.findViewById(R.id.more_med_doc_list_empty_text);

        // Pull To Refresh
        // Refresh items
        mMedDocListRefreshLayout.setOnRefreshListener(this::refreshItems);

        if (getArguments() != null) {
            medDocType = getArguments().getString("MED_DOC_TYPE");
        }
        setEmptyListView();
        setRecyclerView();
        downloadMedDocs(retrieve);
    }

    private void setRecyclerView() {
        mMedDocListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMedDocListRecyclerView.setLayoutManager(layoutManager);
        mMedDocListRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mMedDocListRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setEmptyListView() {
        mEmptyTextView.setText(context.getString(R.string.empty_list_message, context.getString(R.string.more_medical_docs), ": " + medDocType));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
        ((MoreMedDocListActivity) context).setToolBar(medDocType);
        inflater.inflate(R.menu.menu_more_med_doc_list, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchItem.getActionView();
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
                mMedDocListAdapter.filterItems(s);
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            mMedDocListRefreshLayout.setEnabled(true);
            return false;
        });
        searchView.setOnSearchClickListener(view -> mMedDocListRefreshLayout.setEnabled(false));
        setMenuButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMedDocListAdapter != null)
            mMedDocListAdapter.removeFilter();
        mMedDocListRefreshLayout.setEnabled(true);
    }

    public void downloadMedDocs(String type) {
        String indicatorStr = "";
        switch (medDocType) {
            case MedDocType.CLINICAL:
                if (type.equals(retrieve))
                    indicatorStr = context.getString(R.string.get_clinical_indicator);
                else indicatorStr = context.getString(R.string.refresh_clinical_indicator);
                break;
            case MedDocType.INTEGRATIVE:
                if (type.equals(retrieve))
                    indicatorStr = context.getString(R.string.get_integrative_indicator);
                else indicatorStr = context.getString(R.string.refresh_integrative_indicator);
                break;
            case MedDocType.RADIATION:
                if (type.equals(retrieve))
                    indicatorStr = context.getString(R.string.get_radiation_indicator);
                else indicatorStr = context.getString(R.string.refresh_radiation_indicator);
                break;
        }
        if (context != null)
            ((MoreMedDocListActivity) context).showActivityIndicator(indicatorStr);

        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_med_docs, medDocType.toLowerCase());
        Log.d(TAG, "URL: " + url);
        sessionFacade.getMedicalDocumentsData(context, this, medDocType, url, null);
    }

    private void setMenuButtons() {
        ImageView searchButton = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageResource(R.drawable.search_icon);
        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_action_close_green);
        if (sessionFacade.getMedDocs(medDocType).isEmpty() && searchView != null) {
            searchButton.setColorFilter(Color.LTGRAY);
            searchButton.setEnabled(false);
        } else {
            searchButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            closeButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            searchButton.setEnabled(true);
        }
    }

    public void updateUI(List<MedDoc> medicalDocs) {
        // Section Adapter
        mMedDocListAdapter = new MoreMedDocListAdapter(context, medicalDocs, medDocType, this);
        mMedDocListRecyclerView.setAdapter(mMedDocListAdapter);

        mMedDocListRecyclerView.getRecycledViewPool().clear();
        mMedDocListAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mMedDocListRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mMedDocListRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        sessionFacade.clearMedDocs(medDocType);
        downloadMedDocs(refresh);
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        Log.d(TAG, "MoreMedDocClinicalSummaryFragment onRefreshItemsLoadComplete() {\n");
        mMedDocListRefreshLayout.setRefreshing(false);
    }

    public void moreMedDocListRecyclerViewClicked(View v, int position, MedDoc medDoc) {
        showMedDocDetail(medDoc);
    }

    private void showMedDocDetail(MedDoc medDoc) {
        Intent detailIntent = MoreMedDocDetailActivity.newIntent(this.context, medDocType, medDoc.getDocumentId(), medDoc.getDocumentName());
        context.startActivity(detailIntent);
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreMedDocListActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void fetchCarePlan(String document) {
        //not required
    }

    @Override
    public <T> void fetchMedicalDocSuccess(List<T> medicalDocs) {
        if (context != null)
            ((MoreMedDocListActivity) context).hideActivityIndicator();
        updateUI((List<MedDoc>) medicalDocs);
        if (searchView != null)
            setMenuButtons();
    }

    @Override
    public void notifyFetchError(String errorMessage) {
        if (context != null)
            ((MoreMedDocListActivity) context).hideActivityIndicator();
        showRequestFailure(errorMessage);
    }
}
