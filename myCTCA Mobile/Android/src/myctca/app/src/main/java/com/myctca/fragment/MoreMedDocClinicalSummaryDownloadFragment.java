package com.myctca.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.myctca.R;
import com.myctca.activity.MoreMedDocClinicalSummaryActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.DownloadClinicalSummary;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreMedDocClinicalSummaryDownloadFragment extends Fragment implements MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPdf {

    private static final String TAG = MoreMedDocClinicalSummaryDownloadFragment.class.getSimpleName();
    ArrayList<String> selectedClinicalSummaryIds;
    private Button closeDownloadClinicalSummary;
    private Button downloadClinialSummary;
    private TextView downloadClinicalSummaryBody;
    private EditText etFilePassword;
    private File file;
    private String fileName;
    private String mimeType;
    private byte[] response;
    private ImageButton mTogglePasswordButton;
    private boolean passwordIsSecure = false;
    private SessionFacade sessionFacade;
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_med_doc_clinical_summary_download, container, false);
    }

    private void doTogglePassword() {
        if (this.passwordIsSecure) {
            this.passwordIsSecure = false;
            etFilePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mTogglePasswordButton.setImageResource(R.drawable.eye_slash);
        } else {
            this.passwordIsSecure = true;
            etFilePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mTogglePasswordButton.setImageResource(R.drawable.eye);
        }
        etFilePassword.setSelection(etFilePassword.getText().length());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreMedDocClinicalSummaryActivity) context).setToolBar(getString(R.string.download_clinical_summary_toolbar_title));
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        fileName = "clinicalsummary.zip";
        mimeType = "application/zip";

        downloadClinicalSummaryBody = view.findViewById(R.id.download_clinical_summary_body);
        etFilePassword = view.findViewById(R.id.file_password_et);
        LinearLayout bottomLayoutDownloadClinicalSummary = view.findViewById(R.id.bottom_layout_download_clinical_summary);
        closeDownloadClinicalSummary = bottomLayoutDownloadClinicalSummary.findViewById(R.id.btn_close_clinical_summary);
        downloadClinialSummary = bottomLayoutDownloadClinicalSummary.findViewById(R.id.btn_download_clinical_summary);
        mTogglePasswordButton = view.findViewById(R.id.toggle_download_clinical_summary_password_button);

        getAllArguments();
        setFile();
        setButtonClickListeners();
    }

    private void getAllArguments() {
        //get args
        String key = "SELECTED_CLINICAL_SUMMARY_ID";
        selectedClinicalSummaryIds = getArguments().getStringArrayList(key);
    }

    private void setFile() {
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    private void setButtonClickListeners() {
        downloadClinialSummary.setVisibility(View.VISIBLE);
        mTogglePasswordButton.setOnClickListener(v -> {
            Log.d(TAG, "HELLO...Toggle Password");
            doTogglePassword();
        });
        closeDownloadClinicalSummary.setOnClickListener(view1 -> ((MoreMedDocClinicalSummaryActivity) context).onBackPressed());
        downloadClinialSummary.setOnClickListener(view12 -> downloadClinicalSummaryFile());

        downloadClinicalSummaryBody.setText(HtmlCompat.fromHtml(getString(R.string.download_clinical_summary_body), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void downloadClinicalSummaryFile() {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).showActivityIndicator(getString(R.string.download_clinical_summary_indicator));
        DownloadClinicalSummary downloadClinicalSummary = new DownloadClinicalSummary(selectedClinicalSummaryIds, etFilePassword.getText().toString());
        sessionFacade.downloadClinicalSummary(this, context, new Gson().toJson(downloadClinicalSummary));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getFragmentManager().popBackStack();

        if (requestCode == 1 && data != null) {
            try {
                Uri treeUri = data.getData();
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);

                //Delete file already exists in selected folder
                for (DocumentFile documentFile : pickedDir.listFiles()) {
                    Log.d("", documentFile.getName());
                    if (documentFile.getName().equals(fileName)) {
                        documentFile.delete();
                    }
                }

                //create a new file
                pickedDir.createFile(mimeType, fileName);

                //write to that file
                for (DocumentFile documentFile : pickedDir.listFiles()) {
                    Log.d("", documentFile.getName());
                    if (documentFile.getName().equals(fileName)) {
                        Uri uri = documentFile.getUri();
                        ContentResolver contentResolver = context.getContentResolver();
                        OutputStream fileOutputStream = contentResolver.openOutputStream(uri);
                        fileOutputStream.write(response);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDownloadFragment:onActivityResult", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARIES_DOWNLOAD_SUCCESS, null, null);
                        Toast.makeText(context, "Saved successfully", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("MoreMedDocClinicalSummaryDownloadFragment:onActivityResult", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG, "error " + e.getMessage());
            }
        }
    }

    private void showErrorDialog() {
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDownloadFragment:showErrorDialog", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARIES_DOWNLOAD_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.download_clinical_summary_error_title))
                .setMessage(getString(R.string.download_clinical_summary_error_body))
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog, which) -> {
                    dialog.cancel();
                    getFragmentManager().popBackStack();
                })
                .show();
    }

    @Override
    public void notifyPostPdfSuccess(byte[] response) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        try {
            if (response != null) {
                this.response = response;
                FileOutputStream outputStream = new FileOutputStream(file, true);
                outputStream.write(response);
                outputStream.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog dialog = builder.setTitle(getString(R.string.download_clinical_summary_success_title))
                        .setMessage(HtmlCompat.fromHtml(getString(R.string.download_clinical_summary_success_body), HtmlCompat.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton(getString(R.string.save_clinical_summary_btn_text), (dialog1, which) -> {
                            CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDownloadFragment:notifyPostPdfSuccess", CTCAAnalyticsConstants.ACTION_CLINICAL_SUMMARIES_DOWNLOAD_ZIP_SAVE, null, null);
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivityForResult(Intent.createChooser(intent, "Choose directory"), 1);
                        })
                        .setNegativeButton(getString(R.string.nav_alert_cancel), (dialogInterface, i) -> {
                            CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDownloadFragment:notifyPostPdfSuccess", CTCAAnalyticsConstants.ACTION_CLINICAL_SUMMARIES_DOWNLOADED_ZIP_CANCEL, null, null);
                            dialogInterface.cancel();
                            if (getFragmentManager() != null) {
                                getFragmentManager().popBackStack();
                            }
                        }).create();
                dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
                dialog.show();
            }
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreMedDocClinicalSummaryDownloadFragment:notifyPostPdfSuccess", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.d("TAG", "UNABLE TO DOWNLOAD FILE");
            Log.e(TAG, "error " + e.getMessage());
            showErrorDialog();
        }
    }

    @Override
    public void notifyPostPdfError() {
        showErrorDialog();
    }
}
