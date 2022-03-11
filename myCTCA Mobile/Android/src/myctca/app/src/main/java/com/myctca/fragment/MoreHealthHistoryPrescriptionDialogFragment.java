package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.myctca.R;
import com.myctca.activity.PrescriptionRefillActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.Prescription;
import com.myctca.model.UserPermissions;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreHealthHistoryPrescriptionDialogFragment extends DialogFragment {

    private Context mContext;

    private Prescription mPrescription;
    private Context context;

    public static MoreHealthHistoryPrescriptionDialogFragment newInstance(Context context, Prescription prescription) {
        MoreHealthHistoryPrescriptionDialogFragment fragment = new MoreHealthHistoryPrescriptionDialogFragment();
        fragment.mContext = context;
        fragment.mPrescription = prescription;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_more_health_history_prescription, nullParent);
        builder.setView(v);
        builder.setPositiveButton(R.string.more_health_history_prescription_detail_done, (dialog, id) -> dialog.dismiss());

        TextView tvDrugName = v.findViewById(R.id.drug_name_tv);
        TextView tvStatusPrescribed = v.findViewById(R.id.status_prescribed_tv);
        TextView tvStartDate = v.findViewById(R.id.start_date_value_tv);
        TextView tvExpireDate = v.findViewById(R.id.expire_date_value_tv);
        TextView tvInstructions = v.findViewById(R.id.instructions_value_tv);
        TextView tvComments = v.findViewById(R.id.comments_value_tv);

        tvDrugName.setText(mPrescription.getDrugName());
        String statusPrescribedString = mPrescription.getStatusType() + ", " + mPrescription.getPrescriptionType();
        tvStatusPrescribed.setText(statusPrescribedString);
        tvStartDate.setText(mPrescription.getStartDateAsSlashDate());
        tvExpireDate.setText(mPrescription.getExpireDateAsSlashDate());
        tvInstructions.setText(mPrescription.getInstructions());
        tvComments.setText(mPrescription.getComments());

        ImageView renewPrescription = v.findViewById(R.id.renew_prescription);
        if (!mPrescription.getPrescriptionType().equals("CTCA-Prescribed")) {
            renewPrescription.setVisibility(View.GONE);
        }
        renewPrescription.setOnClickListener(view -> {
            if (!AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.REQUEST_PRESCRIPTION_REFILL)) {
                showPermissionAlert();
            } else if (mPrescription.getAllowRenewal()) {
                List<Prescription> refillRequest = new ArrayList<>();
                refillRequest.add(mPrescription);
                Intent intent = PrescriptionRefillActivity.refillRequestIntent(context, refillRequest);
                startActivity(intent);
            } else {
                showAlertDialog();
            }
        });
        return builder.create();
    }

    public boolean isProxyUser() {
        SessionFacade sessionFacade = new SessionFacade();
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
            }
        }
        return !isImpersonating;
    }

    public void showPermissionAlert() {
        String message = "";
        if (isProxyUser()) {
            message = context.getString(R.string.permisson_not_granted_message_proxy);
        } else {
            message = context.getString(R.string.permisson_not_granted_message_patient_prescription);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(context.getString(R.string.permisson_not_granted_title))
                .setMessage(message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //do nothing
                }).create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.prescription_refill_no_ctca_prescribed_title))
                .setMessage(context.getString(R.string.prescription_refill_no_ctca_prescribed_message))
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button positiveButton = ((AlertDialog) getDialog()).getButton(BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
    }
}