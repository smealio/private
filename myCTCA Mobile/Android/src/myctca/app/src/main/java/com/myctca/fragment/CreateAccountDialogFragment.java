package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.activity.LoginActivity;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by tomackb on 6/28/17.
 */

public class CreateAccountDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-CreateAcct";
    CreateAccountDialogListener mListener;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (CreateAccountDialogListener) context;
        } catch (ClassCastException e) {
            // The context doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement RegisterDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = ((LoginActivity) context).getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_create_account, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.create_account_cancel, (dialog, id) -> dialog.dismiss());
        builder.setPositiveButton(R.string.create_account_continue, (dialog, id) -> {
            mListener.onCreateAccount(CreateAccountDialogFragment.this);
            dialog.dismiss();
        });

        Button termsButton = v.findViewById(R.id.createAccountTerms);
        termsButton.setOnClickListener(v1 -> {
            Log.d(TAG, "onClick: TERMS");
            mListener.onTermsClick(CreateAccountDialogFragment.this);
            getDialog().dismiss();
        });

        Button privacyButton = v.findViewById(R.id.createAccountPrivacy);
        privacyButton.setOnClickListener(v12 -> {
            Log.d(TAG, "onClick: PRIVACY");
            mListener.onPrivacyClick(CreateAccountDialogFragment.this);
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
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public interface CreateAccountDialogListener {
        void onTermsClick(DialogFragment dialog);

        void onPrivacyClick(DialogFragment dialog);

        void onCreateAccount(DialogFragment dialog);
    }
}
