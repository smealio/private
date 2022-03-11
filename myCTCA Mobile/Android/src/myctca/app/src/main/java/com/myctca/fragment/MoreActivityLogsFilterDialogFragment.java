package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.activity.MoreActivityLogsActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreActivityLogsFilterDialogFragment extends DialogFragment {
    private static final String TAG = "myCTCA-MORELOGSDATE";
    private static final String LOGS_DATE = "LOGSDATE";
    OnFilterApplyListener mListener;
    private Context context;
    private TextView tvFilterDate;
    private RelativeLayout rvFilterLayout;
    private Date previousSelected;

    public static MoreActivityLogsFilterDialogFragment newInstance(Context context) {
        MoreActivityLogsFilterDialogFragment fragment = new MoreActivityLogsFilterDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MoreActivityLogsFilterDialogFragment so we can send events to the host
            mListener = (OnFilterApplyListener) context;
        } catch (ClassCastException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreActivityLogsFilterDialogFragment:onAttach", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement MoreActivityLogsFilterDialogFragment");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_more_activitylogs_filter, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.filter_logs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        rvFilterLayout = v.findViewById(R.id.filter_date_layout);
        tvFilterDate = v.findViewById(R.id.et_filter_date);
        final EditText etFilterUsername = v.findViewById(R.id.et_filter_username);
        final EditText etFilterMessage = v.findViewById(R.id.et_filter_message);

        prepareDateView();

        builder.setPositiveButton(R.string.filter_logs_apply, (dialogInterface, i) -> {
            CTCAAnalyticsManager.createEvent("MoreActivityLogsFilterDialogFragment:onCreateDialog", CTCAAnalyticsConstants.ACTION_ACTIVITY_LOG_APPLY_FILTER_TAP, null, null);
            mListener.applyFilterOnActivityLogs(etFilterUsername.getText().toString(), etFilterMessage.getText().toString());
        });

        return builder.create();
    }

    private void prepareDateView() {
        rvFilterLayout.setFocusableInTouchMode(true);
        rvFilterLayout.requestFocus();
        rvFilterLayout.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreActivityLogsFilterDialogFragment:prepareDateView", CTCAAnalyticsConstants.ACTION_ACTIVITY_LOGS_FILTER_TAP, null, null);
            Log.d(TAG, "Got A Click Filter Date");
            showDatePickerDialog(LOGS_DATE, true, false);

        });
        rvFilterLayout.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus && rvFilterLayout.canResolveLayoutDirection()) {
                rvFilterLayout.performClick();
            }

        });
        rvFilterLayout.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
    }

    public void showDatePickerDialog(String purpose, boolean addMax, boolean addMin) {
        MoreActivityLogsActivity activity = (MoreActivityLogsActivity) context;
        MoreDatePickerFragment datePickerFragment = MoreDatePickerFragment.newInstance(activity, purpose, addMax, addMin);
        datePickerFragment.show(activity.getSupportFragmentManager(), "datePicker");
        datePickerFragment.setPreviouslySelected(previousSelected);
        dismissKeyboard();
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && requireActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Button positiveButton = ((AlertDialog) getDialog()).getButton(BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreActivityLogsFilterDialogFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
        }
    }

    public void setDate(Date date) {
        this.previousSelected = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(date);
        tvFilterDate.setText(selectedDate);
    }

    public interface OnFilterApplyListener {
        void applyFilterOnActivityLogs(String etFilterUsername, String etFilterMessage);
    }
}
