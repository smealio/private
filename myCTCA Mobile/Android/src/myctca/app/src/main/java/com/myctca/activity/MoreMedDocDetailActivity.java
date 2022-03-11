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
import com.myctca.fragment.MoreMedDocDetailFragment;

public class MoreMedDocDetailActivity extends MyCTCAActivity implements MoreMedDocDetailFragment.OnFragmentInteractionListener {

    public static final String MED_DOC_TYPE = "MedDocType";
    public static final String MED_DOC_ID = "MedDocId";
    public static final String MED_DOC_NAME = "MedDocName";
    private static final String TAG = "myCTCA-MORECLINSUMMDET";
    private String mMedDocType = "";
    private String mMedDocId;
    private String mMedDocName;

    public static Intent newIntent(Context packageContext, String docType, String docId, String docName) {
        Intent intent = new Intent(packageContext, MoreMedDocDetailActivity.class);
        intent.putExtra(MED_DOC_TYPE, docType);
        intent.putExtra(MED_DOC_ID, docId);
        intent.putExtra(MED_DOC_NAME, docName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_med_doc_detail);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mMedDocType = (String) extras.getSerializable(MED_DOC_TYPE);
            mMedDocId = (String) extras.getSerializable(MED_DOC_ID);
            mMedDocName = (String) extras.getSerializable(MED_DOC_NAME);
        }
        FragmentManager fm = getSupportFragmentManager();
        MoreMedDocDetailFragment fragment = new MoreMedDocDetailFragment();
        fm.beginTransaction()
                .add(R.id.more_med_doc_detail_fragment_container, fragment)
                .commit();

        fragment.setmMedDocType(mMedDocType);
        fragment.setmMedDocId(mMedDocId);

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_med_doc_detail, menu);

        String toolbarTitle = mMedDocName;
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setToolBar(String title) {
        TextView titleTV = findViewById(R.id.toolbar_tvTitle);
        titleTV.setText(title);
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
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_med_doc_detail_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_med_doc_detail_fragment_container);
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
        bundle.putString("TOOLBAR_NAME", mMedDocName);

        ((DownloadPdfFragment) fragment).setFileName(mMedDocName + ".pdf");
        ((DownloadPdfFragment) fragment).setPdfFor(mMedDocType);
        ((DownloadPdfFragment) fragment).params.clear();
        ((DownloadPdfFragment) fragment).params.put("docType", mMedDocType);
        ((DownloadPdfFragment) fragment).params.put("documentId", mMedDocId);
        ((DownloadPdfFragment) fragment).setPdfCheck(false);
        ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_download_med_doc_report));

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.more_med_doc_detail_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }
}
