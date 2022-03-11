package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.myctca.activity.MoreMedDocClinicalSummaryActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class StartEndDateDialogFragment extends DialogFragment {
    private static final String TAG = "myCTCA-MORELOGSDATE";
    private static final String LOGS_START_DATE = "LOGSSTARTDATE";
    private static final String LOGS_END_DATE = "LOGSENDDATE";
    StartEndDateDialogListener mListener;
    private Context context;
    private TextView tvFilterStartDate;
    private TextView tvFilterEndDate;
    private RelativeLayout rlFilterStartDateLayout;
    private RelativeLayout rlFilterEndDateLayout;
    private Button positiveButton;
    private TextView filterStartDateLabel;
    private TextView filterEndDateLabel;
    private Date startDate = null;
    private Date endDate = null;
    private TextView startEndDateDialogTitle;
    private String titleDialog;
    private String activityName = "";
    private String positiveButtonText;
    private boolean addMax;
    private boolean addMin;
    private Date previousSelectedStartDate;
    private Date previousSelectedEndDate;

    public static StartEndDateDialogFragment newInstance(Context context) {
        StartEndDateDialogFragment fragment = new StartEndDateDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MoreClinicalSummaryFilterDialogFragment so we can send events to the host
            mListener = (StartEndDateDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.e(TAG, "Exception: ", e);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (activityName != null && activityName.equals(MoreMedDocClinicalSummaryActivity.class.getSimpleName()))
            CTCAAnalyticsManager.createEvent("StartEndDateDialogFragment:onCreateDialog", CTCAAnalyticsConstants.PAGE_CLINICAL_SUMMARIES_FILTER_VIEW, null, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_start_end_date, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.filter_logs_cancel, (dialog, id) -> dialog.dismiss());

        rlFilterStartDateLayout = v.findViewById(R.id.filter_clinical_start_date_layout);
        rlFilterEndDateLayout = v.findViewById(R.id.filter_clinical_end_date_layout);
        tvFilterStartDate = v.findViewById(R.id.tv_filter_clinical_start_date);
        tvFilterEndDate = v.findViewById(R.id.tv_filter_clinical_end_date);


        filterStartDateLabel = v.findViewById(R.id.filter_clinical_start_date_title);
        filterEndDateLabel = v.findViewById(R.id.filter_clinical_end_date_title);

        startEndDateDialogTitle = v.findViewById(R.id.startEndDateDialogTitle);

        prepareDateView();

        builder.setPositiveButton(positiveButtonText,
                (dialogInterface, i) -> {
                    if (activityName.equals(MoreMedDocClinicalSummaryActivity.class.getSimpleName())) {
                        mListener.applyResultUsingDates(tvFilterStartDate.getText().toString(), tvFilterEndDate.getText().toString());
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            if (!TextUtils.isEmpty(tvFilterStartDate.getText().toString()) && !TextUtils.isEmpty(tvFilterEndDate.getText().toString())) {
                if (endDate.before(startDate)) {
                    showErrorAlert(getString(R.string.start_end_date_error_title), context.getString(R.string.start_end_date_error_message));
                } else {
                    mListener.applyResultUsingDates(tvFilterStartDate.getText().toString(), tvFilterEndDate.getText().toString());
                    dialog.dismiss();
                }
            } else if (TextUtils.isEmpty(tvFilterStartDate.getText().toString()) || TextUtils.isEmpty(tvFilterEndDate.getText().toString())) {
                showErrorAlert("Select Date Range", "Please select a valid start and end date.");
            }
        });
        return dialog;
    }

    private void showErrorAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", (dialogInterface, i) -> {
            //do nothing
        }).show();
    }

    public void setDateMinMax(boolean addMax, boolean addMin) {
        this.addMax = addMax;
        this.addMin = addMin;
    }

    private void prepareDateView() {
        if (activityName.equals(MoreMedDocClinicalSummaryActivity.class.getSimpleName())) {
            titleDialog = context.getString(R.string.more_med_doc_clinical_summary_filter_title);
        }
        positiveButtonText = getString(R.string.filter_logs_apply);
        startEndDateDialogTitle.setText(titleDialog);

        rlFilterStartDateLayout.setFocusableInTouchMode(true);
        rlFilterStartDateLayout.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Filter Date");
            showDatePickerDialog(LOGS_START_DATE, addMax, addMin);

        });
        rlFilterStartDateLayout.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                if (rlFilterStartDateLayout.canResolveLayoutDirection()) {
                    rlFilterStartDateLayout.performClick();
                }
            } else {
                if (TextUtils.isEmpty(tvFilterStartDate.getText().toString())) {
                    filterStartDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    filterStartDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            }
        });
        rlFilterStartDateLayout.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });


        rlFilterEndDateLayout.setFocusableInTouchMode(true);
        rlFilterEndDateLayout.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Filter Date");
            showDatePickerDialog(LOGS_END_DATE, addMax, addMin);

        });
        rlFilterEndDateLayout.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                if (rlFilterEndDateLayout.canResolveLayoutDirection()) {
                    rlFilterEndDateLayout.performClick();
                }
            } else {
                if (TextUtils.isEmpty(tvFilterEndDate.getText().toString())) {
                    filterEndDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    filterEndDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            }
        });
        rlFilterEndDateLayout.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
    }

    private void enablePositiveButton() {
        if (!TextUtils.isEmpty(tvFilterStartDate.getText().toString()) && !TextUtils.isEmpty(tvFilterEndDate.getText().toString())) {
            if (endDate.before(startDate)) {
                filterStartDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                filterEndDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                filterStartDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                filterEndDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }
    }

    public void showDatePickerDialog(String purpose, boolean addMax, boolean addMin) {
        MoreDatePickerFragment datePickerFragment = new MoreDatePickerFragment();
        if (activityName.equals(MoreMedDocClinicalSummaryActivity.class.getSimpleName())) {
            datePickerFragment = MoreDatePickerFragment.newInstance((MoreMedDocClinicalSummaryActivity) context, purpose, addMax, addMin);
        }
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");

        if (purpose.equals(LOGS_START_DATE))
            datePickerFragment.setPreviouslySelected(previousSelectedStartDate);
        else
            datePickerFragment.setPreviouslySelected(previousSelectedEndDate);

        dismissKeyboard();
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        if (imm != null && requireActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        positiveButton = ((AlertDialog) getDialog()).getButton(BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
            activityName = tag;
        } catch (IllegalStateException e) {
            Log.e(TAG, "error :" + e);
        }
    }

    public void setStartDate(Date date) {
        this.previousSelectedStartDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault());
        String selectedDate = sdf.format(date);
        tvFilterStartDate.setText(selectedDate);
        try {
            startDate = sdf.parse(selectedDate);
        } catch (ParseException e) {
            Log.e(TAG, "error " + e.getMessage());
        }
        enablePositiveButton();
    }

    public void setEndDate(Date date) {
        this.previousSelectedEndDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault());
        String selectedDate = sdf.format(date);
        tvFilterEndDate.setText(selectedDate);
        try {
            endDate = sdf.parse(selectedDate);
        } catch (ParseException e) {
            Log.e(TAG, "error " + e.getMessage());
        }
        enablePositiveButton();
    }

    public interface StartEndDateDialogListener {
        void applyResultUsingDates(String startDate, String endDate);
    }
}
