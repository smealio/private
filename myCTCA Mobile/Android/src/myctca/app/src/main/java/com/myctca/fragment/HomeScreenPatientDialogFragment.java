package com.myctca.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.adapter.HomeScreenCaregiverAccessPatientAdapter;
import com.myctca.model.MyCTCAProxy;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenPatientDialogFragment extends DialogFragment {
    private HomeScreenPatientDialogListener listener;
    private String TAG = HomeScreenPatientDialogFragment.class.getSimpleName();
    private RecyclerView rvAccessPatientList;
    private HomeScreenCaregiverAccessPatientAdapter adapter;
    private List<MyCTCAProxy> proxies = new ArrayList<>();
    private AlertDialog dialog;
    private SessionFacade sessionFacade;
    private LinearLayout selectPatientTitle;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (HomeScreenPatientDialogListener) getTargetFragment();
        } catch (ClassCastException exception) {
            Log.e(TAG, "exception: " + exception);
        }
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        proxies.addAll(sessionFacade.getProxies());

        removeCaregiverSelfProxy();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_home_screen_patient_dialog, null);
        adapter = new HomeScreenCaregiverAccessPatientAdapter(context, proxies);
        rvAccessPatientList = view.findViewById(R.id.rv_access_patient_list);
        selectPatientTitle = view.findViewById(R.id.select_patient_title);
        TextView textTitle = selectPatientTitle.findViewById(R.id.roi_spinner_text);
        textTitle.setText(context.getString(R.string.access_patient_dialog_title));

        setRecyclerView();
        builder.setView(view);
        dialog = builder.create();
        return dialog;
    }

    private void removeCaregiverSelfProxy() {
        for (MyCTCAProxy proxy : proxies) {
            if (proxy.getToCtcaUniqueId().equals(sessionFacade.getMyCtcaUserProfile().getCtcaId())) {
                proxies.remove(proxy);
                break;
            }
        }
    }

    private void setRecyclerView() {
        rvAccessPatientList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAccessPatientList.setLayoutManager(layoutManager);
        rvAccessPatientList.setAdapter(adapter);
        adapter.setOnItemClickListner(selectedProxy -> {
            listener.onPatientSelected(selectedProxy);
            dialog.dismiss();
        });
    }

    public interface HomeScreenPatientDialogListener {
        void onPatientSelected(MyCTCAProxy proxy);
    }
}