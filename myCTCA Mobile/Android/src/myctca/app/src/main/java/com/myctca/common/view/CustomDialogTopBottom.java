package com.myctca.common.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.myctca.R;

public class CustomDialogTopBottom {

    public AlertDialog getDialog(Activity activity, CustomDialogListener listener, String title, String message, String positiveBtn, String negativeBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_yes_no_abovebelow_dialog_box, null);
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        Button dialogPositiveButton = view.findViewById(R.id.dialogPositiveButton);
        Button dialogNegativeButton = view.findViewById(R.id.dialogNegativeButton);

        if (title.isEmpty()) {
            dialogTitle.setVisibility(View.GONE);
        }
        if (positiveBtn.isEmpty()) {
            dialogPositiveButton.setVisibility(View.GONE);
        }
        if (negativeBtn.isEmpty()) {
            dialogNegativeButton.setVisibility(View.GONE);
        }

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogPositiveButton.setText(positiveBtn);
        dialogNegativeButton.setText(negativeBtn);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPositiveButton.setOnClickListener(view1 -> dialog.dismiss());

        dialogNegativeButton.setOnClickListener(view1 -> {
            dialog.dismiss();
            listener.negativeButtonAction();
        });
        return dialog;
    }

    public interface CustomDialogListener {
        void negativeButtonAction();
    }
}
