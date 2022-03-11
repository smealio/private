package com.myctca.fragment.appointmment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.activity.AppointmentDownloadPdfActivity;
import com.myctca.activity.AppointmentDownloadScheduleActivity;
import com.myctca.common.view.CustomDialogSideBySide;
import com.myctca.fragment.common.CalendarRangeFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentDownloadScheduleFragment extends Fragment implements CustomDialogSideBySide.CustomDialogSideBySideListener {

    private static final String TAG = ApptRequestContactPreferenceFragment.class.getSimpleName();
    private static final String START_DATE = "START";
    private static final String END_DATE = "END";
    private TextView tvStartDate;
    private TextView tvEndDate;
    private Context context;
    private Date startDate;
    private Date endDate;
    private Button downloadAppointmentSchedule;
    private CalendarRangeFragment childFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_schedule_download, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downloadAppointmentSchedule = view.findViewById(R.id.downloadAppointmentSchedule);
        downloadAppointmentSchedule.setAlpha(0.4f);
        downloadAppointmentSchedule.setEnabled(false);

        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);

        childFragment = new CalendarRangeFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.calender_fragment_container, childFragment).commit();

        handleOnClickListeners();

        downloadAppointmentSchedule.setOnClickListener(view1 -> new CustomDialogSideBySide().getButtonsSideBySideDialog(this, (AppointmentDownloadScheduleActivity) context, "", context.getString(R.string.appt_download_schedule_message), context.getString(R.string.download_btn_text), context.getString(R.string.appt_detail_cancel_icon_title)).show());
    }

    private void handleOnClickListeners() {
        tvStartDate.setOnClickListener(view -> focusStartDate());

        tvEndDate.setOnClickListener(view -> focusEndDate());
    }

    public void setRange(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        if (start == null || end == null) {
            downloadAppointmentSchedule.setAlpha(0.4f);
            downloadAppointmentSchedule.setEnabled(false);
        } else {
            downloadAppointmentSchedule.setAlpha(1);
            downloadAppointmentSchedule.setEnabled(true);
        }
        if (start == null) {
            focusStartDate();
            tvStartDate.setText("");
        } else {
            tvStartDate.setText(sdf.format(start));
        }
        if (end == null) {
            focusEndDate();
            tvEndDate.setText("");
        } else {
            tvEndDate.setText(sdf.format(end));
        }
    }

    private void focusStartDate() {
        tvEndDate.setBackground(ContextCompat.getDrawable(context, R.drawable.border_with_label));
        tvStartDate.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_round_border));
        childFragment.setDateType(START_DATE);
    }

    private void focusEndDate() {
        tvStartDate.setBackground(ContextCompat.getDrawable(context, R.drawable.border_with_label));
        tvEndDate.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_round_border));
        childFragment.setDateType(END_DATE);
    }

    @Override
    public void negativeButtonAction() {
        //do nothing
    }

    @Override
    public void positiveButtonAction() {
        Log.d(TAG, "downloadApptsPdf");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault());
        Intent intent = AppointmentDownloadPdfActivity.newIntent(context);
        intent.putExtra("startDate", sdf.format(startDate));
        intent.putExtra("endDate", sdf.format(endDate));
        startActivity(intent);
    }
}