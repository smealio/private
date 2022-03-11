package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.MoreFormsLibraryActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.UserPermissions;


public class MoreFormsLibraryFragment extends Fragment {

    private static final String TAG = "myCTCA-FORMSLIBRARY";
    private OnFragmentInteractionListener listener;
    private TextView releaseOfInformation;
    private TextView advanceNoticeOfNonCoverage;
    private Context context;
    private View roiSeparator;
    private View anncSeparator;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_forms_library, container, false);
        releaseOfInformation = view.findViewById(R.id.release_of_information);
        advanceNoticeOfNonCoverage = view.findViewById(R.id.adv_notice);
        roiSeparator = view.findViewById(R.id.roi_separator);
        anncSeparator = view.findViewById(R.id.annc_separator);

        setInitialUI();
        setHasOptionsMenu(true);
        return view;
    }

    private void setInitialUI() {
        if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.SUBMIT_ROI_FORM)) {
            releaseOfInformation.setVisibility(View.VISIBLE);
            roiSeparator.setVisibility(View.VISIBLE);
        } else {
            releaseOfInformation.setVisibility(View.GONE);
            roiSeparator.setVisibility(View.GONE);
        }

        if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.SUBMIT_ANNC_FORM)) {
            advanceNoticeOfNonCoverage.setVisibility(View.VISIBLE);
            anncSeparator.setVisibility(View.VISIBLE);
        } else {
            advanceNoticeOfNonCoverage.setVisibility(View.GONE);
            anncSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreFormsLibraryActivity) context).setToolBar(context.getString(R.string.forms_library));
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        releaseOfInformation.setOnClickListener(view1 -> listener.addFragment(MoreReleaseOfInfoFragment.newInstance(), MoreReleaseOfInfoFragment.class.getSimpleName()));

        advanceNoticeOfNonCoverage.setOnClickListener(view12 -> listener.addFragment(new MoreAnncFragment(), MoreReleaseOfInfoFragment.class.getSimpleName()));
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment, String tag);
    }
}