package com.myctca.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.MoreChangePatientActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.common.CTCARecyclerView;
import com.myctca.interfaces.MoreChangePatientRecyclerViewListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreChangePatientFragment extends Fragment implements MoreChangePatientRecyclerViewListener {

    private static final String TAG = "myCTCA-MORECHGPATIENT";

    private CTCARecyclerView mChangePatientRecyclerView;
    private ChangePatientAdapter mChangePatientAdapter;

    private List<String> viewablePatients;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (AppSessionManager.getInstance().getUserProfile() == null) {
            ((MoreChangePatientActivity) context).getFragmentManager().popBackStackImmediate();
            return null;
        } else {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_more_change_patient, container, false);

            // get Views
            mChangePatientRecyclerView = view.findViewById(R.id.more_change_patient_recycler_view);

            // Set initial conditions for recycler view
            mChangePatientRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mChangePatientRecyclerView.setLayoutManager(layoutManager);

            // Get Viewable Patients
            if (AppSessionManager.getInstance().getUserProfile().getViewablePatients() != null) {
                viewablePatients = AppSessionManager.getInstance().getUserProfile().getViewablePatients();

                updateUI();
            }

            return view;
        }
    }

    public void updateUI() {

        if (mChangePatientAdapter == null) {
            mChangePatientAdapter = new ChangePatientAdapter(viewablePatients, this);
            mChangePatientRecyclerView.setAdapter(mChangePatientAdapter);
        } else {
            mChangePatientRecyclerView.getRecycledViewPool().clear();
            mChangePatientAdapter.notifyDataSetChanged();
            mChangePatientRecyclerView.scrollToPosition(0);
        }
    }

    public void moreChangePatientRecyclerViewListClicked(View v, int position, String selectedPatient) {
        Log.d(TAG, "selectedPatient: " + selectedPatient);
        String ctcaUniqueId = AppSessionManager.getInstance().getUserProfile().getCTCAUniqueIdFromFullName(selectedPatient);
        Log.d(TAG, "ctcaUniqueId: " + ctcaUniqueId);
        MoreChangePatientActivity activity = (MoreChangePatientActivity) context;
        activity.changeDisplayedPatient(ctcaUniqueId);
    }

    private class ChangePatientInstructHolder extends RecyclerView.ViewHolder {

        private TextView tvCurrentPatientName;

        private ChangePatientInstructHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_change_patient_instruct, parent, false));

            tvCurrentPatientName = itemView.findViewById(R.id.tv_more_change_patient_instruct);
        }

        public void bind(String currentPatientName) {

            String sourceString = context.getString(R.string.more_change_patient_instruct_text, currentPatientName);

            int startingIndex = sourceString.indexOf(currentPatientName);
            int endingIndex = startingIndex + currentPatientName.length();

            SpannableStringBuilder spannableString = new SpannableStringBuilder(sourceString);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);

            tvCurrentPatientName.setText(spannableString);
        }
    }

    private class ChangePatientNameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MoreChangePatientRecyclerViewListener rvListener;
        private String mPatientName;

        private TextView tvPatientName;

        private ChangePatientNameHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_change_patient_name, parent, false));

            tvPatientName = itemView.findViewById(R.id.more_change_patient_name_tv);
        }

        public void bind(String patientName, MoreChangePatientRecyclerViewListener rvListener) {

            this.mPatientName = patientName;
            this.rvListener = rvListener;

            if (this.mPatientName.equals(AppSessionManager.getInstance().getUserProfile().getFullName())) {
                tvPatientName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                tvPatientName.setAlpha(0.5f);
                itemView.setEnabled(false);
            } else {
                itemView.setEnabled(true);
                itemView.setOnClickListener(this);
            }

            tvPatientName.setText(mPatientName);
        }

        @Override
        public void onClick(View v) {
            rvListener.moreChangePatientRecyclerViewListClicked(v, this.getLayoutPosition(), this.mPatientName);
        }

        public void clearListItem() {
            tvPatientName.setText("");
        }
    }


    private class ChangePatientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 0;
        private static final int NAME_VIEW = 1;

        private List<String> mViewablePatients = new ArrayList<>();
        private MoreChangePatientRecyclerViewListener rvListener;

        private ChangePatientAdapter(List<String> viewablePatientsAR, MoreChangePatientRecyclerViewListener rvListener) {
            if (viewablePatientsAR != null) {
                mViewablePatients = viewablePatientsAR;
            }
            this.rvListener = rvListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            if (viewType == HEADER_VIEW) {
                return new ChangePatientInstructHolder(layoutInflater, parent);
            }

            return new ChangePatientNameHolder(layoutInflater, parent);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof ChangePatientInstructHolder) {
                    ChangePatientInstructHolder instructHolder = (ChangePatientInstructHolder) holder;
                    String currentPatientName = AppSessionManager.getInstance().getUserProfile().getFullName();
                    instructHolder.bind(currentPatientName);
                } else if (holder instanceof ChangePatientNameHolder) {
                    ChangePatientNameHolder nameHolder = (ChangePatientNameHolder) holder;
                    String patientName = getItem(position);
                    nameHolder.bind(patientName, rvListener);
                }
            } catch (Exception e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("ChangePatientAdapter:onBindViewHolder", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG, "error " + e.getMessage());
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
            try {
                if (holder instanceof ChangePatientNameHolder) {
                    ChangePatientNameHolder nameHolder = (ChangePatientNameHolder) holder;
                    nameHolder.clearListItem();
                }
            } catch (Exception e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("ChangePatientAdapter:onViewRecycled", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG, "error " + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return mViewablePatients.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {
                return HEADER_VIEW;
            }
            return NAME_VIEW;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        private String getItem(int position) {
            return mViewablePatients.get(position - 1);
        }
    }
}
