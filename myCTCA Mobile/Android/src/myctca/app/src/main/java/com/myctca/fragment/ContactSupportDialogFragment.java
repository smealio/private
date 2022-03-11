package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by tomackb on 6/28/17.
 */

public class ContactSupportDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-CreateAcct";
    ContactSupportDialogListener mListener;
    private Context context;

    public static ContactSupportDialogFragment newInstance(Context context) {

        ContactSupportDialogFragment fragment = new ContactSupportDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (ContactSupportDialogListener) activity;
        } catch (ClassCastException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("ContactSupportDialogFragment:onAttach", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement RegisterDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_contact_support, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.create_account_cancel, (dialog, id) -> dialog.dismiss());

        TextView tvVersion = v.findViewById(R.id.contact_support_version);
        String versionName = BuildConfig.VERSION_NAME;

        tvVersion.setText(context.getString(R.string.more_about_myctca_version, versionName));

        LinearLayout callButton = v.findViewById(R.id.contactSupportCall);
        callButton.setOnClickListener(v1 -> {
            CTCAAnalyticsManager.createEvent("ContactSupportDialogFragment:onCreateDialog", CTCAAnalyticsConstants.ACTION_LOGIN_CALL_GOTO_TAP, null, null);
            Log.d(TAG, "onClick: CALL");
            mListener.onCall(ContactSupportDialogFragment.this);
            getDialog().dismiss();
        });

        Button sendMessage = v.findViewById(R.id.contactSupportSendMessage);
        sendMessage.setOnClickListener(v12 -> {
            CTCAAnalyticsManager.createEvent("ContactSupportDialogFragment:onCreateDialog", CTCAAnalyticsConstants.ACTION_LOGIN_SEND_MESSAGE_GOTO_TAP, null, null);
            Log.d(TAG, "onClick: SEND MESSAGE");
            mListener.onSendMessage(ContactSupportDialogFragment.this);
            getDialog().dismiss();
        });

        return builder.create();
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
            CTCAAnalyticsManager.createEventForSystemExceptions("ContactSupportDialogFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);

        }
    }

    public interface ContactSupportDialogListener {
        void onCall(DialogFragment dialog);

        void onSendMessage(DialogFragment dialog);
    }
}
