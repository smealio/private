package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.MoreMedDocImagingDetailActivity;

public class MoreMedDocImagingDetailFragment extends Fragment {

    public String mImagingText;
    private OnFragmentInteractionListener listener;
    private Button cancelButton;
    private Button downloadButton;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_med_doc_imaging_detail, container, false);

        TextView mTextView = view.findViewById(R.id.text_view);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mTextView.setText(mImagingText);

        LinearLayout labsBottomButtonLayout = view.findViewById(R.id.labs_bottom_button_layout);
        cancelButton = labsBottomButtonLayout.findViewById(R.id.btn_close_clinical_summary);
        downloadButton = labsBottomButtonLayout.findViewById(R.id.btn_download_clinical_summary);
        downloadButton.setVisibility(View.VISIBLE);

        handleButtonActions();
        return view;
    }

    private void handleButtonActions() {
        cancelButton.setOnClickListener(view -> {
            if (context != null)
                ((MoreMedDocImagingDetailActivity) context).onBackPressed();
        });
        downloadButton.setOnClickListener(view -> listener.addFragment(DownloadPdfFragment.newInstance()));
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment);
    }
}
