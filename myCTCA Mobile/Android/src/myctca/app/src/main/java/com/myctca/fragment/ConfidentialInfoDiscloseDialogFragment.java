package com.myctca.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by tomackb on 2/5/18.
 */

public class ConfidentialInfoDiscloseDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-CONFIDENTIAL";
    private Map<String, Boolean> selectedConfidentialInfo = new HashMap<>();
    private List<String> roiSelectedConfidentialInfo;
    private ConfidentialInfoDiscloseDialogFragment.ConfidentialInfoDiscloseDialogListener mListener;
    private Context context;
    // Checkboxes
    private CheckBox cbSelectAll;
    // Textboxes
    private TextView tvSelectAll;
    // Checkbox Array List
    private Map<CheckBox, TextView> checkBoxes = new HashMap<>();
    private TextView roiHighlyConfidentialTitle;

    public static ConfidentialInfoDiscloseDialogFragment newInstance(Context context) {

        ConfidentialInfoDiscloseDialogFragment fragment = new ConfidentialInfoDiscloseDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (ConfidentialInfoDiscloseDialogFragment.ConfidentialInfoDiscloseDialogListener) activity;
        } catch (ClassCastException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("ConfidentialInfoDiscloseDialogFragment:onAttach", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement ConfidentialInfoDiscloseDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_confidential_info_disclose, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.confidential_info_disclose_cancel, (dialog, id) -> dialog.dismiss());
        builder.setPositiveButton(R.string.confidential_info_disclose_done, (dialog, id) -> {
            buildSelectedConfidentialInfo();
            Log.d(TAG, "selectedConfidentialInfo: " + selectedConfidentialInfo);
            mListener.onConfidentialDiscloseInfoDone(selectedConfidentialInfo);
            dialog.dismiss();
        });

        // Checkboxes
        cbSelectAll = v.findViewById(R.id.select_all_checkbox);
        cbSelectAll.setChecked(true);
        tvSelectAll = v.findViewById(R.id.select_all_textview);
        //layout
        LinearLayout roiConfidentialLabel = v.findViewById(R.id.roi_confidential_label);
        TextView roiConfidentialLabelText = roiConfidentialLabel.findViewById(R.id.roi_spinner_text);
        roiConfidentialLabelText.setText(context.getString(R.string.more_roi_confidential_label));
        TableLayout tableLayout = v.findViewById(R.id.tl_confidential_info);
        List<String> highlyConfidentialInformationList = AppSessionManager.getInstance().getRoiDetails().getHighlyConfidentialInformationList();

        for (int item = 0; item < highlyConfidentialInformationList.size(); item++) {

            //set rows in table layouts
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.setGravity(Gravity.TOP);

            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.addView(relativeLayout);

            TextView textView = new TextView(context);
            textView.setText(highlyConfidentialInformationList.get(item));

            final CheckBox checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.CTCACheckBox));
            checkBox.setText(textView.getText());
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_18));

            tableRow.addView(checkBox);

            tableLayout.addView(tableRow);

            if (roiSelectedConfidentialInfo == null) {
                checkBox.setChecked(true);
            } else {
                if (roiSelectedConfidentialInfo.contains(highlyConfidentialInformationList.get(item)))
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

        boolean allChecked = testAllChecked();
        cbSelectAll.setChecked(allChecked);
        roiHighlyConfidentialTitle = v.findViewById(R.id.roi_highly_confidential_title);

        prepareView();

        return builder.create();
    }

    public void setPreviouslySelected(List<String> roiSelectedConfidentialInfo) {
        this.roiSelectedConfidentialInfo = roiSelectedConfidentialInfo;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepareView() {
        //set title
        roiHighlyConfidentialTitle.setText(context.getString(R.string.more_roi_info_to_disclose_confidential));

        cbSelectAll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    Log.d("Touch", "Touch down");
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    boolean allChecked = testAllChecked();
                    Log.d(TAG, "allChecked: " + allChecked);
                    for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
                        CheckBox cb = entry.getKey();
                        cb.setChecked(!allChecked);
                    }
                }
                return true;
            }
        });
        tvSelectAll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    Log.d("TouchTest", "Touch down");
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    boolean allChecked = testAllChecked();
                    Log.d(TAG, "allChecked: " + allChecked);
                    for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
                        CheckBox cb = entry.getKey();
                        cb.setChecked(!allChecked);
                    }
                }
                return true;
            }
        });

        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            final CheckBox cb = entry.getKey();
            TextView tv = entry.getValue();

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean allChecked = testAllChecked();
                    cbSelectAll.setChecked(allChecked);
                }
            });
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cb.setChecked(!cb.isChecked());
                }
            });
        }
    }

    public boolean testAllChecked() {
        boolean allChecked = true;
        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            CheckBox cb = entry.getKey();
            if (!cb.isChecked()) {
                allChecked = false;
                break;
            }
        }
        return allChecked;
    }

    public void buildSelectedConfidentialInfo() {

        selectedConfidentialInfo = new HashMap<>();

        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            CheckBox cb = entry.getKey();
            TextView tv = entry.getValue();
            selectedConfidentialInfo.put(tv.getText().toString(), cb.isChecked());
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
            CTCAAnalyticsManager.createEventForSystemExceptions("ConfidentialInfoDiscloseDialogFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);

        }
    }

    public interface ConfidentialInfoDiscloseDialogListener {
        void onConfidentialDiscloseInfoDone(Map<String, Boolean> confidentialInfo);
    }
}
