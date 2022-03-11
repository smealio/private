package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.common.AppSessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DeliveryMethodDiscloseDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-DELIVERMETHOD";
    private Map<String, Boolean> selectedDeliveryMethod = new HashMap<>();

    // Checkbox Array List
    private Map<CheckBox, TextView> checkBoxes = new HashMap<>();
    private DeliveryMethodDiscloseDialogListener mListener;
    private Context context;
    private List<String> previouslySelected;

    public static DeliveryMethodDiscloseDialogFragment newInstance(Context context) {

        DeliveryMethodDiscloseDialogFragment fragment = new DeliveryMethodDiscloseDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (DeliveryMethodDiscloseDialogFragment.DeliveryMethodDiscloseDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement DeliveryMethodDiscloseDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_delivery_method_disclose, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.confidential_info_disclose_cancel, (dialog, id) -> dialog.dismiss());
        builder.setPositiveButton(R.string.confidential_info_disclose_done, (dialog, id) -> {
            buildSelectedDeliveryMethod();
            Log.d(TAG, "selectedDeliveryMethodInfo: " + selectedDeliveryMethod);
            mListener.onDeliveryMethodDiscloseDone(selectedDeliveryMethod);
            dialog.dismiss();
        });

        LinearLayout roiDeliveryMethodTitle = v.findViewById(R.id.roi_delivery_method_title);
        TextView roiSpinnerText = roiDeliveryMethodTitle.findViewById(R.id.roi_spinner_text);
        roiSpinnerText.setText(getString(R.string.more_roi_delivery_info_heading));

        TableLayout tableLayout = v.findViewById(R.id.tl_delivery_methods);
        List<String> deliveryMethods = AppSessionManager.getInstance().getRoiDetails().getDeliveryMethods();

        for (int method = 0; method < deliveryMethods.size(); method++) {

            //set rows in table layouts
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.setGravity(Gravity.TOP);

            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.addView(relativeLayout);

            TextView textView = new TextView(context);
            textView.setText(deliveryMethods.get(method));

            final CheckBox checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.CTCACheckBox));
            checkBox.setText(textView.getText());
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_18));

            tableRow.addView(checkBox);

            tableLayout.addView(tableRow);
            if (previouslySelected != null && previouslySelected.contains(deliveryMethods.get(method))) {
                checkBox.setChecked(true);
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
            checkBoxes.put(checkBox, textView);
        }

        return builder.create();
    }

    public void setPreviouslySelected(List<String> previousSelected) {
        this.previouslySelected = previousSelected;
    }

    public void buildSelectedDeliveryMethod() {

        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            Log.d(TAG, "buildSelectedDeliveryMethod text: " + entry.getKey());
            CheckBox cb = entry.getKey();
            TextView tv = entry.getValue();
            selectedDeliveryMethod.put(tv.getText().toString(), cb.isChecked());

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
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    public interface DeliveryMethodDiscloseDialogListener {
        void onDeliveryMethodDiscloseDone(Map<String, Boolean> deliveryMethod);
    }
}
