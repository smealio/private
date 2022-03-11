package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.MoreFormsLibraryActivity;

public class MoreReleaseOfInfoFragment extends Fragment {
    private OnFragmentInteractionListener listener;
    private LinearLayout llSubmitRoiAuthOnline;
    private LinearLayout llDownloadRoiAuthForm;
    private TextView roiIncludelayout;
    private Context context;

    public static MoreReleaseOfInfoFragment newInstance() {
        return new MoreReleaseOfInfoFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_release_of_info, container, false);
        llSubmitRoiAuthOnline = view.findViewById(R.id.submit_roi_auth_online);
        llDownloadRoiAuthForm = view.findViewById(R.id.download_roi_auth_form);
        roiIncludelayout = view.findViewById(R.id.roi_include_layout);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MoreFormsLibraryActivity) context).setTitle(context.getString(R.string.more_roi));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreFormsLibraryActivity) context).setToolBar(context.getString(R.string.release_of_information));
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roiIncludelayout.setText(getString(R.string.release_of_information));

        llDownloadRoiAuthForm.setOnClickListener(view1 -> listener.addFragment(DownloadPdfFragment.newInstance(), MoreReleaseOfInfoFragment.class.getSimpleName()));
        llSubmitRoiAuthOnline.setOnClickListener(view12 -> listener.addFragment(new MoreReleaseOfInfoFormFragment(), MoreReleaseOfInfoFormFragment.class.getSimpleName()));
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment, String tag);
    }
}