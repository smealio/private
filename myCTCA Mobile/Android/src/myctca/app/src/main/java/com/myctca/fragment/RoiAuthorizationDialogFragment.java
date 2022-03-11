package com.myctca.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.annotation.Nullable;
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

public class RoiAuthorizationDialogFragment extends DialogFragment {

    private static final String TAG = "myCTCA-ROIAUTH";
    private RoiAuthDialogListener mListener;
    private Context context;

    // Checkbox Array List
    private Map<CheckBox, TextView> checkBoxes = new HashMap<>();
    private Map<String, Boolean> selectedAuth = new HashMap<>();
    private List<String> roiSelectedAuthActions;

    public static RoiAuthorizationDialogFragment newInstance(Context context) {

        RoiAuthorizationDialogFragment fragment = new RoiAuthorizationDialogFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RegisterDialogListener so we can send events to the host
            mListener = (RoiAuthDialogListener) activity;
        } catch (ClassCastException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("RoiAuthorizationDialogFragment:onAttach", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement RoiAuthDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.dialog_roi_authorization_dialog, nullParent);
        builder.setView(v);
        builder.setNegativeButton(R.string.roi_auth_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.roi_auth_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                buildSelectedAuthAction();
                Log.d(TAG, "selectedAuthorizationAction: " + selectedAuth);
                mListener.onAuthActionDone(selectedAuth);
                dialog.dismiss();
            }
        });

        LinearLayout roiAuthTitle = v.findViewById(R.id.roi_auth_title);
        TextView roiAuthText = roiAuthTitle.findViewById(R.id.roi_spinner_text);
        roiAuthText.setText(context.getString(R.string.more_roi_delivery_info_heading));

        TableLayout tableLayout = v.findViewById(R.id.tl_roi_auth);
        List<String> authorizationActions = AppSessionManager.getInstance().getRoiDetails().getAuthorizationActions();

        for (int item = 0; item < authorizationActions.size(); item++) {

            //set rows in table layouts
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.setGravity(Gravity.TOP);

            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            tableRow.addView(relativeLayout);

            TextView textView = new TextView(context);
            textView.setText(authorizationActions.get(item));

            final CheckBox checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.CTCACheckBox));
            checkBox.setText(textView.getText());
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_18));

            tableRow.addView(checkBox);

            tableLayout.addView(tableRow);
            if (roiSelectedAuthActions != null && roiSelectedAuthActions.contains(authorizationActions.get(item))) {
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

    public void buildSelectedAuthAction() {

        for (Map.Entry<CheckBox, TextView> entry : checkBoxes.entrySet()) {
            Log.d(TAG, "buildSelectedPurpose text: " + entry.getKey());
            CheckBox cb = entry.getKey();
            TextView tv = entry.getValue();
            selectedAuth.put(tv.getText().toString(), cb.isChecked());
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
            CTCAAnalyticsManager.createEventForSystemExceptions("RoiAuthorizationDialogFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error " + e.getMessage());
        }
    }

    public void setPreviouslySelected(List<String> roiSelectedAuthActions) {
        this.roiSelectedAuthActions = roiSelectedAuthActions;
    }

    public interface RoiAuthDialogListener {
        void onAuthActionDone(Map<String, Boolean> selectedAuth);
    }
}