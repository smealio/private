package com.myctca.common.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.myctca.R;

public class CustomRequestDialog {

    public AlertDialog getSuccessFailureDialog(Activity activity, boolean success, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.success_failure_dialog_box, null);
        ImageView dialogImage = view.findViewById(R.id.dialogImage);
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        Button dialogPositiveButton = view.findViewById(R.id.dialogPositiveButton);

        dialogImage.setImageDrawable(success ? ContextCompat.getDrawable(activity, R.drawable.check_circle_icon) :
                ContextCompat.getDrawable(activity, R.drawable.cancel_circle_icon));

        dialogTitle.setTextColor(success ? ContextCompat.getColor(activity, R.color.colorPrimary) :
                ContextCompat.getColor(activity, R.color.red_bright));

        dialogPositiveButton.setTextColor(success ? ContextCompat.getColor(activity, R.color.colorPrimary) :
                ContextCompat.getColor(activity, R.color.colorPrimaryDark));

        dialogTitle.setText(title);
        dialogMessage.setText(message);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPositiveButton.setOnClickListener(view1 -> {
            dialog.dismiss();
            if (success)
                activity.finish();
        });
        return dialog;
    }
}
