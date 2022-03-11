package com.myctca.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
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
import static android.view.View.FOCUS_UP;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralInfoDiscloseDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-GENERALINFO";
    private Map<String, Boolean> selectedGeneralInfo = new HashMap<>();
    private String generalInfoOther = "";
    private String selectedGeneralInfoOther = "";
    private List<String> roiSelectedInfo;
    private GeneralInfoDiscloseDialogListener mListener;
    private Context context;
    // Checkboxes
    private CheckBox cbSelectAll;
    //TextView
    private TextView tvSelectAll;
    // Checkbox Map
    private Map<CheckBox, TextView> checkBoxes = new HashMap<>();
    // Edit Text
    private EditText etOther;

    public static GeneralInfoDiscloseDialogFragment newInstance(Context context) {

        GeneralInfoDiscloseDialogFragment fragment = new GeneralInfoDiscloseDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (GeneralInfoDiscloseDialogFragment.GeneralInfoDiscloseDialogListener) activity;
        } catch (ClassCastException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("GeneralInfoDiscloseDialogFragment:onAttach", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement GeneralInfoDiscloseDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_general_info_disclose, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.general_info_disclose_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.general_info_disclose_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                buildSelectedGeneralInfo();
                Log.d(TAG, "selectedGeneralInfo: " + selectedGeneralInfo);
                mListener.onGeneralDiscloseInfoDone(selectedGeneralInfo, generalInfoOther);
                dialog.dismiss();
            }
        });

        // Checkboxes
        cbSelectAll = v.findViewById(R.id.select_all_checkbox);
        tvSelectAll = v.findViewById(R.id.select_all_textview);

        // EditText
        etOther = v.findViewById(R.id.other_edittext);

        TableLayout tableLayout = v.findViewById(R.id.tl_general_info);
        List<String> disclosureInformationList = AppSessionManager.getInstance().getRoiDetails().getDisclosureInformationList();

        for (int item = 0; item < disclosureInformationList.size(); item++) {

            //set rows in table layouts
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.setGravity(Gravity.TOP);

            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.addView(relativeLayout);

            TextView textView = new TextView(context);
            textView.setText(disclosureInformationList.get(item));

            final CheckBox checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.CTCACheckBox));
            checkBox.setText(textView.getText());
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_18));

            tableRow.addView(checkBox);

            tableLayout.addView(tableRow);

            if (roiSelectedInfo != null && roiSelectedInfo.contains(disclosureInformationList.get(item))) {
                checkBox.setChecked(true);
                if (!TextUtils.isEmpty(selectedGeneralInfoOther))
                    etOther.setText(selectedGeneralInfoOther);
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

        //Title
        TextView generalInfoTitle = v.findViewById(R.id.roi_general_info_title);
        generalInfoTitle.setText(context.getString(R.string.more_roi_general_info_heading));
        LinearLayout generalInfoLabel = v.findViewById(R.id.roi_info_label);
        TextView generalInfoLabelText = generalInfoLabel.findViewById(R.id.roi_spinner_text);
        generalInfoLabelText.setText(context.getString(R.string.more_roi_check_category_heading));

        prepareView();

        final ScrollView scrollView = v.findViewById(R.id.general_info_scrollview);
        scrollView.requestFocus();
        scrollView.fullScroll(FOCUS_UP);
        // Wait until my scrollView is ready
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ready, move up
                scrollView.fullScroll(FOCUS_UP);
            }
        });

        return builder.create();
    }

    public void setPreviouslySelected(List<String> roiSelectedInfo, String generalInfoOther) {
        this.roiSelectedInfo = roiSelectedInfo;
        this.selectedGeneralInfoOther = generalInfoOther;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepareView() {
        cbSelectAll.setOnTouchListener(new View.OnTouchListener() {
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

    public void buildSelectedGeneralInfo() {

        selectedGeneralInfo = new HashMap<>();

        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            CheckBox cb = entry.getKey();
            TextView tv = entry.getValue();
            if (cb.isChecked() && tv.getText().toString().equals("Other") && TextUtils.isEmpty(etOther.getText().toString())) {
                //don't add to payload.
                cb.setChecked(false);
            } else if (tv.getText().toString().equals("Other") && !TextUtils.isEmpty(etOther.getText().toString())) {
                selectedGeneralInfo.put(tv.getText().toString(), true);
                generalInfoOther = etOther.getText().toString();
            } else
                selectedGeneralInfo.put(tv.getText().toString(), cb.isChecked());

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
            CTCAAnalyticsManager.createEventForSystemExceptions("GeneralInfoDiscloseDialogFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);

        }
    }

    public interface GeneralInfoDiscloseDialogListener {
        void onGeneralDiscloseInfoDone(Map<String, Boolean> generalInfo, String generalInfoOther);
    }

}
