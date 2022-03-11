package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreFormsLibraryActivity;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.Facility;
import com.myctca.model.MedicalCenter;
import com.myctca.service.MoreFormsLibraryService;
import com.myctca.service.SessionFacade;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreAnncFragment extends Fragment implements MoreFormsLibraryService.MoreFormsLibraryANNCListenerGet {

    private static final String PURPOSE = "ANNC_FORM_EXISTS";
    private LinearLayout submitAnncFormOnline;
    private LinearLayout downloadAnncForm;
    private TextView tvTopAnncTitle;
    private OnFragmentInteractionListener listener;
    private TextView downloadAnncText;
    private Context context;
    private SessionFacade sessionFacade;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_annc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        tvTopAnncTitle = view.findViewById(R.id.annc_include_layout);
        submitAnncFormOnline = view.findViewById(R.id.submit_annc_form_online);
        downloadAnncForm = view.findViewById(R.id.download_annc_form);
        downloadAnncText = view.findViewById(R.id.forms_library_annc_download_title);

        checkIfFormExists();
        prepareView();
    }

    private void checkIfFormExists() {
        if (context != null)
            ((MoreFormsLibraryActivity) context).showActivityIndicator(getString(R.string.please_wait_loading));
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_annc_form_exists);
        sessionFacade.getANNCFormInfo(this, url, context, PURPOSE);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreFormsLibraryActivity) context).setToolBar(getString(R.string.adv_notice_title));
        menu.clear();
    }

    private void prepareView() {
        tvTopAnncTitle.setText(getString(R.string.adv_notice));

        submitAnncFormOnline.setOnClickListener(view -> listener.addFragment(new MoreAnncFormFragment(), MoreAnncFormFragment.class.getSimpleName()));
        downloadAnncForm.setOnClickListener(view -> listener.addFragment(DownloadPdfFragment.newInstance(), MoreAnncFragment.class.getSimpleName()));
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreFormsLibraryActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void ifANNCExists(boolean anncExists) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
        if (anncExists) {
            downloadAnncText.setVisibility(View.VISIBLE);
            downloadAnncForm.setVisibility(View.VISIBLE);
        } else {
            downloadAnncText.setVisibility(View.GONE);
            downloadAnncForm.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyFetchError(String errMessage) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
        showRequestFailure(VolleyErrorHandler.handleError(errMessage, context));
    }

    @Override
    public void notifyMrn(String response) {
        //do nothing
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment, String className);
    }
}