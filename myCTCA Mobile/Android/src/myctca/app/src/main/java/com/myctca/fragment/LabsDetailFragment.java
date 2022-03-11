package com.myctca.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.LabsDetailActivity;
import com.myctca.adapter.LabsDetailAdapter;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.LabResult;
import com.myctca.util.MyCTCADateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LabsDetailFragment extends Fragment {

    private static final String TAG = "myCTCA-LabsDetail";

    private RecyclerView labsDetailRecyclerView;
    private LabsDetailAdapter labsDetailAdapter;

    private LabResult labResult;
    private Button cancelButton;
    private Button downloadButton;
    private OnFragmentInteractionListener listener;
    private LinearLayout labsBottomButtonLayout;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_labs_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get Views
        labsDetailRecyclerView = view.findViewById(R.id.labs_detail_rv);
        labsBottomButtonLayout = view.findViewById(R.id.labs_bottom_button_layout);
        cancelButton = labsBottomButtonLayout.findViewById(R.id.btn_close_clinical_summary);
        downloadButton = labsBottomButtonLayout.findViewById(R.id.btn_download_clinical_summary);

        this.labResult = ((LabsDetailActivity) context).getLabResult();

        setLabsDetailRecyclerView();
        setLabsBottomButtonLayout();
        updateUI(labResult);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");

        String toolbarTitle = context.getString(R.string.labs_detail_title) + " " + MyCTCADateUtils.getSlashedDateStr(this.labResult.getPerformedDate());
        ((LabsDetailActivity) context).setToolBar(toolbarTitle);

        inflater.inflate(R.menu.menu_labs_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setLabsDetailRecyclerView() {
        labsDetailRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        labsDetailRecyclerView.setLayoutManager(layoutManager);
    }

    private void setLabsBottomButtonLayout() {
        cancelButton = labsBottomButtonLayout.findViewById(R.id.btn_close_clinical_summary);
        downloadButton = labsBottomButtonLayout.findViewById(R.id.btn_download_clinical_summary);
        downloadButton.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(view -> ((LabsDetailActivity) context).onBackPressed());
        downloadButton.setOnClickListener(view -> {
            CTCAAnalyticsManager.createEvent("LabsDetailFragment:handleButtonActions", CTCAAnalyticsConstants.ACTION_LAB_RESULTS_PDF_DOWNLOAD_TAP, null, null);
            listener.addFragment(DownloadPdfFragment.newInstance());
        });
    }

    private void updateUI(LabResult labResult) {
        if (labsDetailAdapter == null) {
            labsDetailAdapter = new LabsDetailAdapter(context, ((LabsDetailActivity) context), labResult);
            labsDetailRecyclerView.setAdapter(labsDetailAdapter);
        } else {
            labsDetailAdapter.setLabResult(labResult);
            labsDetailRecyclerView.getRecycledViewPool().clear();
            labsDetailAdapter.notifyDataSetChanged();
        }
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment);
    }
}
