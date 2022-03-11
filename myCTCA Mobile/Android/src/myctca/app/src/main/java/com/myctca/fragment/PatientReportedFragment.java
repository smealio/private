package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.PatientReportedActivity;

public class PatientReportedFragment extends Fragment {

    private TextView symptomInventoryDocument;
    private TextView sitDocument;
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
        return inflater.inflate(R.layout.fragment_patient_reported, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sitDocument = view.findViewById(R.id.tv_sit_document);
        symptomInventoryDocument = view.findViewById(R.id.tv_symptom_inventory);
        handleOnClickListeners();
    }

    private void handleOnClickListeners() {
        sitDocument.setOnClickListener(view -> {
            ((PatientReportedActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_get_sit_document), context.getString(R.string.patient_reported_sit_document_title));
        });

        symptomInventoryDocument.setOnClickListener(view -> {
            ((PatientReportedActivity) context).addFragment(new PatientSymptomInventoryFragment(), "", "");
        });
    }
}