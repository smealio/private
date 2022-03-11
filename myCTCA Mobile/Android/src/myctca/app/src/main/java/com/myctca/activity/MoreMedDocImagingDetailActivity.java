package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.MoreMedDocImagingDetailFragment;

public class MoreMedDocImagingDetailActivity extends MyCTCAActivity implements MoreMedDocImagingDetailFragment.OnFragmentInteractionListener {

    public static final String IMAGING_DOC_NAME = "ImagingDocNAME";
    public static final String IMAGING_DOC_TEXT = "ImagingDocText";
    public static final String IMAGING_DOC_ID = "ImagingDocId";
    private static final String TAG = "myCTCA-MOREIMGDOCDET";
    private String mImagingName;
    private String mImagingText;
    private String mImagingId;

    public static Intent newIntent(Context packageContext, String docText, String docName, String docId) {
        Intent intent = new Intent(packageContext, MoreMedDocImagingDetailActivity.class);
        intent.putExtra(IMAGING_DOC_NAME, docName);
        intent.putExtra(IMAGING_DOC_TEXT, docText);
        intent.putExtra(IMAGING_DOC_ID, docId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_med_doc_imaging_detail);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mImagingName = (String) extras.getSerializable(IMAGING_DOC_NAME);
            mImagingText = (String) extras.getSerializable(IMAGING_DOC_TEXT);
            mImagingId = (String) extras.getSerializable(IMAGING_DOC_ID);
        }

        FragmentManager fm = getSupportFragmentManager();
        MoreMedDocImagingDetailFragment fragment = new MoreMedDocImagingDetailFragment();
        fm.beginTransaction()
                .add(R.id.more_med_doc_imaging_detail_fragment_container, fragment)
                .commit();
        fragment.mImagingText = this.mImagingText;
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_med_doc_detail, menu);

        String toolbarTitle = this.mImagingName;
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setToolBar(String title) {
        if (toolbar != null) {
            TextView titleTV = findViewById(R.id.toolbar_tvTitle);
            titleTV.setText(title);
        }
    }

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_med_doc_imaging_detail_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_med_doc_imaging_detail_fragment_container);
        downloadPdfFragment.printSavePdf();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            case R.id.item_share_pdf:
                sharePdf();
                break;
            case R.id.item_print_pdf:
                openMorePdfOptions();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }
        return true;
    }

    @Override
    public void addFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("TOOLBAR_NAME", mImagingName);

        ((DownloadPdfFragment) fragment).setFileName(mImagingName + ".pdf");
        ((DownloadPdfFragment) fragment).setPdfFor(getString(R.string.more_med_doc_imaging));
        ((DownloadPdfFragment) fragment).params.clear();
        ((DownloadPdfFragment) fragment).params.put("documentId", mImagingId);
        ((DownloadPdfFragment) fragment).setPdfCheck(false);
        ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_download_imaging_report));

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.more_med_doc_imaging_detail_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }
}

