package com.myctca.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.activity.LoginActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallTechSupportFragment extends DialogFragment {


    private static final String TAG = "CTCA-CallTechSupport";
    private Context context;

    public static CallTechSupportFragment newInstance(Context context) {
        CallTechSupportFragment fragment = new CallTechSupportFragment();
        fragment.context = context;
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
        builder.setTitle(R.string.call_tech_title);
        builder.setMessage(context.getString(R.string.call_tech_text, AppSessionManager.getInstance().getTechnicalSupport()));
        builder.setCancelable(false);
        if (capableOfCalling()) {
            builder.setPositiveButton(R.string.call_tech_call, (dialog, id) -> {
                checkPermission();
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.call_tech_cancel, (dialog, id) -> dialog.cancel());
        } else {
            builder.setNegativeButton(R.string.call_tech_ok, (dialog, id) -> dialog.cancel());
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(context.getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(context.getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), context.getString(R.string.telehealth_permission_initial_positive), context.getString(R.string.telehealth_permission_initial_negative));
        } else {
            // Permission has already been granted or Android earlier than Marshmallow
            callTechSupport();
        }
    }

    private void showPermissionError(String title, String message, String positiveBtn, String negativeBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(positiveBtn, (dialogInterface, i) -> {
                    if (positiveBtn.equals(context.getString(R.string.telehealth_permission_positive))) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ((LoginActivity) context).requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, AppSessionManager.getInstance().getpermissionsRequestCallPhone());
                        }
                    }
                })
                .setNegativeButton(negativeBtn, (dialogInterface, i) -> {
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppSessionManager.getInstance().getpermissionsRequestCallPhone()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callTechSupport();
            } else {
                ArrayList<String> permissionsToAskFor = new ArrayList<>();
                boolean somePermissionsForeverDenied = false;
                boolean denied = false;
                for (String permission : permissions) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        //denied
                        denied = true;
                        permissionsToAskFor.add(permission);
                        Log.e("denied", permission);
                    } else {
                        if (checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            //allowed
                            Log.e("allowed", permission);
                        } else {
                            //set to never ask again
                            Log.e("set to never ask again", permission);
                            somePermissionsForeverDenied = true;
                        }
                    }
                }
                if (somePermissionsForeverDenied) {
                    showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(context.getString(R.string.phone_permission_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(context.getString(R.string.phone_permission_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), context.getString(R.string.telehealth_permission_positive), context.getString(R.string.telehealth_permission_negative));
                } else if (denied) {
                    showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(context.getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(context.getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), context.getString(R.string.telehealth_permission_initial_positive), context.getString(R.string.telehealth_permission_initial_negative));
                }
            }
        }
    }

    public void callTechSupport() {
        String techPhoneNumber = AppSessionManager.getInstance().getTechnicalSupport();
        techPhoneNumber = Constants.countryCode + techPhoneNumber;
        Log.d(TAG, "techPhoneNumber: " + techPhoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + techPhoneNumber));
        Log.d(TAG, "OK TO CALL: " + techPhoneNumber);
        context.startActivity(callIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (capableOfCalling()) {
            Button positiveButton = ((AlertDialog) getDialog()).getButton(BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    private boolean capableOfCalling() {

        final PackageManager mgr = context.getPackageManager();

        Uri callToUri = Uri.parse("tel:");
        Intent intent = new Intent(Intent.ACTION_CALL, callToUri);
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Log.d(TAG, "list: " + list);
        return (!list.isEmpty());
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("CallTechSupportFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
        }
    }
}

