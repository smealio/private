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
import com.myctca.activity.MoreMedDocImagingDetailActivity;
import com.myctca.activity.MoreMedDocImagingListActivity;
import com.myctca.adapter.MoreMedDocImagingListAdapter;
import com.myctca.common.CTCARecyclerView;
import com.myctca.interfaces.MoreMedDocImagingListRecyclerViewListener;
import com.myctca.model.ImagingDoc;
import com.myctca.model.MedDocType;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedDocImagingListFragment extends Fragment implements MoreMedDocImagingListRecyclerViewListener, MoreMedicalDocumentsService.MoreMedDocListenerGet {

    private static final String TAG = "myCTCA-MedDocs";
    private static final String PURPOSE = "IMAGING";
    public String medDocType = MedDocType.IMAGING;
    private SwipeRefreshLayout mMedDocImagingListRefreshLayout;
    private CTCARecyclerView mMedDocImagingListRecyclerView;
    private MoreMedDocImagingListAdapter mMedDocImagingListAdapter;
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
        return inflater.inflate(R.layout.fragment_more_med_doc_imaging_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mMedDocImagingListRecyclerView = view.findViewById(R.id.more_med_doc_imaging_list_recycler_view);
        mMedDocImagingListRefreshLayout = view.findViewById(R.id.more_med_doc_imaging_list_swipe_refresh);
        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyTextView = view.findViewById(R.id.more_med_doc_imaging_list_empty_text);
        mEmptyTextView.setText(context.getString(R.string.empty_list_message, context.getString(R.string.more_medical_docs), ": " + medDocType));

        // Pull To Refresh
        // Refresh items
        mMedDocImagingListRefreshLayout.setOnRefreshListener(this::refreshItems);
        setImagingRecyclerView();
        downloadImagingDocs(context.getString(R.string.get_imaging_indicator));
    }

    private void setImagingRecyclerView() {
        // RecyclerView
        mMedDocImagingListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMedDocImagingListRecyclerView.setLayoutManager(layoutManager);
        mMedDocImagingListRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mMedDocImagingListRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreMedDocImagingListActivity) context).setToolBar(medDocType);

        inflater.inflate(R.menu.menu_more_med_doc_imaging_list, menu);
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
                mMedDocImagingListAdapter.filterItems(s);
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            mMedDocImagingListRefreshLayout.setEnabled(true);
            return false;
        });
        searchView.setOnSearchClickListener(view -> mMedDocImagingListRefreshLayout.setEnabled(false));
        setMenuButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMedDocImagingListAdapter != null)
            mMedDocImagingListAdapter.removeFilter();
        mMedDocImagingListRefreshLayout.setEnabled(true);
    }

    public void downloadImagingDocs(String indicatorStr) {
        if (context != null)
            ((MoreMedDocImagingListActivity) context).showActivityIndicator(indicatorStr);

        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_imaging_docs);
        Log.d(TAG, "URL: " + url);
        sessionFacade.getMedicalDocumentsData(context, this, PURPOSE, url, null);
    }

    private void setMenuButtons() {
        ImageView searchButton = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchButton.setImageResource(R.drawable.search_icon);
        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_action_close_green);
        if (getImagingDocs().isEmpty() && searchView != null) {
            searchButton.setColorFilter(Color.LTGRAY);
            searchButton.setEnabled(false);
        } else {
            searchButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            closeButton.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            searchButton.setEnabled(true);
        }
    }

    public void updateUI(List<ImagingDoc> imagingDocs) {
        // Section Adapter
        mMedDocImagingListAdapter = new MoreMedDocImagingListAdapter(context, imagingDocs, this);
        mMedDocImagingListRecyclerView.setAdapter(mMedDocImagingListAdapter);

        mMedDocImagingListRecyclerView.getRecycledViewPool().clear();
        mMedDocImagingListAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mMedDocImagingListRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mMedDocImagingListRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        sessionFacade.clearMedDocs(PURPOSE);
        downloadImagingDocs(context.getString(R.string.refresh_imaging_indicator));
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        Log.d(TAG, "MoreMedDocImagingListFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mMedDocImagingListRefreshLayout.setRefreshing(false);
    }


    private List<ImagingDoc> getImagingDocs() {
        return sessionFacade.getmImagingDocs();
    }

    public void moreMedDocImagingListRecyclerViewClicked(View v, int position, ImagingDoc imagingDoc) {
        showImagingDocDetail(imagingDoc);
    }

    private void showImagingDocDetail(ImagingDoc imagingDoc) {
        Intent detailIntent = MoreMedDocImagingDetailActivity.newIntent(this.context, imagingDoc.notes, imagingDoc.itemName, imagingDoc.id);
        context.startActivity(detailIntent);
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreMedDocImagingListActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }


    @Override
    public void fetchCarePlan(String document) {
        //not to use for this medical document
    }

    @Override
    public <T> void fetchMedicalDocSuccess(List<T> medicalDocs) {
        if (context != null)
            ((MoreMedDocImagingListActivity) context).hideActivityIndicator();
        updateUI((List<ImagingDoc>) medicalDocs);
        if (searchView != null)
            setMenuButtons();
    }

    @Override
    public void notifyFetchError(String errorMessage) {
        if (context != null)
            ((MoreMedDocImagingListActivity) context).hideActivityIndicator();
        showRequestFailure(errorMessage);
    }
}
