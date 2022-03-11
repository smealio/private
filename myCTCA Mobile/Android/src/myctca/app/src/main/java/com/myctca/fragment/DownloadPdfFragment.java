package com.myctca.fragment;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.adapter.PdfDocumentAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.receiver.ShareReceiver;
import com.myctca.service.PdfService;
import com.myctca.service.SessionFacade;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadPdfFragment extends Fragment implements PdfService.PdfServiceListener {
    private static final String TAG = DownloadPdfFragment.class.getSimpleName();
    private static final int STORAGE_PERMISSION_CODE = 101;
    public Map<String, String> params = new HashMap<>();
    private String fileName;
    private String mUrl;
    private boolean pdfCheck;
    private String pdfFor = "";
    private PDFView pdfView;
    private File file;
    private String toolBarName;
    private SessionFacade sessionFacade;
    private Context context;
    private String appointmentId;

    public static DownloadPdfFragment newInstance() {
        return new DownloadPdfFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download_pdf, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        toolBarName = bundle.getString("TOOLBAR_NAME");
        sessionFacade = new SessionFacade();
        pdfView = view.findViewById(R.id.pdfView);
        try {
            if (context != null)
                file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            checkPdf();
        } catch (Exception e) {
            Log.e(TAG, "Unable to download PDF");
        }


        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_more_download_pdf, menu);
        ((MyCTCAActivity) context).setToolBar(toolBarName);
    }

    public void checkPdf() {
        //check if pdf already exists
        if (pdfCheck) {
            openPdf();
        } else {
            if (file != null && file.exists())
                file.delete();
            downloadPdfFile();
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setPdfCheck(boolean pdfCheck) {
        this.pdfCheck = pdfCheck;
    }

    public void setPdfFor(String pdfFor) {
        this.pdfFor = pdfFor;
    }

    private void downloadPdfFile() {
        String activityIndicatorText = "";
        switch (this.pdfFor) {
            case "Lab Results":
                activityIndicatorText = context.getString(R.string.download_lab_results_indicator);
                break;
            case "Appointment Schedule":
                activityIndicatorText = context.getString(R.string.download_appointments_indicator);
                break;
            case "Vitals":
                activityIndicatorText = context.getString(R.string.download_vitals_indicator);
                break;
            case "Allergies":
                activityIndicatorText = context.getString(R.string.download_allergies_indicator);
                break;
            case "Prescriptions":
                activityIndicatorText = context.getString(R.string.download_prescriptions_indicator);
                break;
            case "Immunizations":
                activityIndicatorText = context.getString(R.string.download_immunizations_indicator);
                break;
            case "Health Issues":
                activityIndicatorText = context.getString(R.string.download_health_issues_indicator);
                break;
            case "Care Plan":
                activityIndicatorText = context.getString(R.string.download_care_plan_indicator);
                break;
            case "Clinical":
                activityIndicatorText = context.getString(R.string.download_clinical_indicator);
                break;
            case "Imaging":
                activityIndicatorText = context.getString(R.string.download_imaging_indicator);
                break;
            case "Radiation":
                activityIndicatorText = context.getString(R.string.download_radiation_indicator);
                break;
            case "Integrative":
                activityIndicatorText = context.getString(R.string.download_integrative_indicator);
                break;
            case "Single Appointment Schedule":
                activityIndicatorText = context.getString(R.string.download_appointment_indicator);
                break;
            default:
                activityIndicatorText = "Downloading...";
                break;
        }
        if (context != null)
            ((MyCTCAActivity) context).showActivityIndicator(activityIndicatorText);

        if (pdfFor.equals("Single Appointment Schedule")) {
            if (!appointmentId.isEmpty()) {
                AppointmentId appointmentId = new AppointmentId();
                appointmentId.setAppointmentId(this.appointmentId);
                sessionFacade.downloadAppointmentSchedule(this, context, mUrl, new Gson().toJson(appointmentId));
            }
        } else {
            sessionFacade.downloadPdfFile(this, context, mUrl, params);
        }
    }

    private void openPdf() {
        try {
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onError(t -> Log.d(TAG, "Open File Error:" + t.toString()))
                    .enableAntialiasing(true)
                    .spacing(0)
                    .load();

        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    public void sharePdf() {
        try {
            //get the selected app in broadcast receiver
            Intent receiver = new Intent(context, ShareReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                context.startActivity(Intent.createChooser(share, "Share", pendingIntent.getIntentSender()));
            } else {
                context.startActivity(Intent.createChooser(share, "Share"));
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    public void printSavePdf() {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(file.toString(), fileName);
            printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Log.d(TAG, "Pdf Document Exception:" + e);
        }
    }

    private void showErrorDialog(String message) {
        if (this.pdfFor != null) {
            switch (this.pdfFor) {
                case "ANNC":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_ANNC_DOWNLOAD_FAIL, null, null);
                    break;
                case "ROI":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_ROI_DOWNLOAD_FAIL, null, null);
                    break;
                case "Lab Results":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_LAB_DOWNLOAD_FAIL, null, null);
                    break;
                case "Appointment Schedule":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_DOWNLOAD_FAIL, null, null);
                    break;
                case "Care Plan":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_CARE_PLAN_DOWNLOAD_FAIL, null, null);
                    break;
                case "Clinical":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_CLINICAL_DOWNLOAD_FAIL, null, null);
                    break;
                case "Imaging":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_IMAGING_DOWNLOAD_FAIL, null, null);
                    break;
                case "Radiation":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_RADIATION_DOWNLOAD_FAIL, null, null);
                    break;
                case "Integrative":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_INTEGRATIVE_DOWNLOAD_FAIL, null, null);
                    break;
                case "Vitals":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_VITALS_DOWNLOAD_FAIL, null, null);
                    break;
                case "Allergies":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_ALLERGIES_DOWNLOAD_FAIL, null, null);
                    break;
                case "Prescriptions":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_PRESCRIPTIONS_DOWNLOAD_FAIL, null, null);
                    break;
                case "Immunizations":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_IMMUNIZATIONS_DOWNLOAD_FAIL, null, null);
                    break;
                case "Health Issues":
                    CTCAAnalyticsManager.createEvent("DownloadPdfFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_HEALTH_ISSUES_DOWNLOAD_FAIL, null, null);
                    break;
                default:
                    break;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyGetPdfSuccess(byte[] response) {
        if (context != null)
            ((MyCTCAActivity) context).hideActivityIndicator();
        try {
            if (response != null) {
                FileOutputStream outputStream;
                outputStream = new FileOutputStream(file, true);
                outputStream.write(response);
                outputStream.close();
                if (!TextUtils.isEmpty(this.pdfFor)) {
                    if (this.pdfFor.equals(MoreReleaseOfInfoFragment.class.getSimpleName())) {
                        AppSessionManager.getInstance().setROIPdfInstalled(true);
                    } else if (this.pdfFor.equals(MoreAnncFragment.class.getSimpleName())) {
                        AppSessionManager.getInstance().setANNCPdfInstalled(true);
                    }
                }
                openPdf();
            }
        } catch (Exception e) {
            Log.d(TAG, "UNABLE TO DOWNLOAD FILE");
            showErrorDialog(context.getString(R.string.error_400));
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    @Override
    public void notifyGetPdfError(VolleyError error) {
        if (context != null)
            ((MyCTCAActivity) context).hideActivityIndicator();
        showErrorDialog(VolleyErrorHandler.handleError(error, context));
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    class AppointmentId {
        String appointmentId;

        public void setAppointmentId(String appointmentId) {
            this.appointmentId = appointmentId;
        }
    }
}