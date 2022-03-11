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
import com.myctca.model.CareTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by tomackb on 2/5/18.
 */

public class CareTeamsDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-CARE_TEAMS";
    private Map<String, Boolean> selectedCareTeams = new HashMap<>();
    private List<CareTeam> selectedCareTeamDetails = new ArrayList<>();
    private List<String> previouslySelected;
    private CareTeamsDialogFragment.CareTeamsDialogListener mListener;
    private Context context;

    // Checkboxes
    private CheckBox cbSelectAll;
    // Textboxes
    private TextView tvSelectAll;

    // Checkbox Array List
    private Map<CheckBox, TextView> checkBoxes = new HashMap<>();

    public static CareTeamsDialogFragment newInstance(Context context) {

        CareTeamsDialogFragment fragment = new CareTeamsDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (CareTeamsDialogFragment.CareTeamsDialogListener) activity;
        } catch (ClassCastException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("CareTeamsDialogListener:onAttach", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement CareTeamsDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_fragment_care_teams, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.mail_care_teams_cancel, (dialog, id) -> dialog.dismiss());
        builder.setPositiveButton(R.string.mail_care_teams_done, (dialog, id) -> {
            buildSelectedCareTeams();
            Log.d(TAG, "selectedCareTeamsInfo: " + selectedCareTeams);
            mListener.onCareTeamsSelectionDone(selectedCareTeams, selectedCareTeamDetails);
            dialog.dismiss();
        });

        // Checkboxes
        cbSelectAll = v.findViewById(R.id.select_all_checkbox);
        tvSelectAll = v.findViewById(R.id.select_all_textview);
        //layout
        LinearLayout careTeamsLabel = v.findViewById(R.id.roi_care_teams_label);
        TextView careTeamsLabelText = careTeamsLabel.findViewById(R.id.roi_spinner_text);
        careTeamsLabelText.setText(getString(R.string.mail_care_teams_label));
        TableLayout tableLayout = v.findViewById(R.id.tl_care_teams);
        List<String> careTeams = AppSessionManager.getInstance().getmCareTeam();

        if (careTeams != null) {
            for (int item = 0; item < careTeams.size(); item++) {

                //set rows in table layouts
                TableRow tableRow = new TableRow(context);
                tableRow.setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                tableRow.setGravity(Gravity.TOP);

                RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                tableRow.addView(relativeLayout);

                TextView textView = new TextView(context);
                textView.setText(careTeams.get(item));

                final CheckBox checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.CTCACheckBox));
                checkBox.setText(textView.getText());
                checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_18));

                tableRow.addView(checkBox);

                tableLayout.addView(tableRow);
                if (previouslySelected != null && previouslySelected.contains(careTeams.get(item))) {
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
        }

        prepareView();

        return builder.create();
    }

    public void setPreviouslySelected(List<String> previousSelected) {
        this.previouslySelected = previousSelected;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepareView() {
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

    public void buildSelectedCareTeams() {

        selectedCareTeams = new HashMap<>();
        Map<String, CareTeam> careTeamDetails = AppSessionManager.getInstance().getmCareTeamDetails();
        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            CheckBox cb = entry.getKey();
            TextView tv = entry.getValue();
            if (cb.isChecked()) {
                selectedCareTeams.put(tv.getText().toString(), true);
                selectedCareTeamDetails.add(careTeamDetails.get(tv.getText().toString()));
            } else {
                selectedCareTeams.put(tv.getText().toString(), false);
            }
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
            CTCAAnalyticsManager.createEventForSystemExceptions("CareTeamsDialogFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);

        }
    }

    public interface CareTeamsDialogListener {
        void onCareTeamsSelectionDone(Map<String, Boolean> selectedCareTeams, List<CareTeam> selectedCareTeamsDetails);
    }
}
