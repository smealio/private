package com.myctca.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.MoreMyResourcesExternalLinksFragment;
import com.myctca.fragment.MoreMyResourcesFragment;
import com.myctca.fragment.MoreMyResourcesWebViewsFragment;
import com.myctca.model.ExternalLink;

import java.util.List;

public class MyResourcesActivity extends MyCTCAActivity {

    private List<ExternalLink> externalLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_resources);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_my_resources_fragment_container);

        if (fragment == null) {
            fragment = new MoreMyResourcesFragment();
            fm.beginTransaction()
                    .add(R.id.more_my_resources_fragment_container, fragment)
                    .commit();
        }

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_my_resources, menu);

        String toolbarTitle = getString(R.string.more_my_resources);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_my_resources_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_my_resources_fragment_container);
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
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public void addFragment(Fragment fragment, String url, String myResourceType, boolean reopen) {
        if (fragment instanceof DownloadPdfFragment) {
            Bundle bundle = new Bundle();
            bundle.putString("TOOLBAR_NAME", myResourceType);
            ((DownloadPdfFragment) fragment).setFileName(myResourceType + ".pdf");
            ((DownloadPdfFragment) fragment).setPdfFor(myResourceType);
            ((DownloadPdfFragment) fragment).params.clear();
            ((DownloadPdfFragment) fragment).setPdfCheck(false);
            ((DownloadPdfFragment) fragment).setmUrl(url);
            fragment.setArguments(bundle);
        } else if (fragment instanceof MoreMyResourcesWebViewsFragment) {
            Bundle bundle = new Bundle();
            bundle.putString("TOOLBAR_NAME", myResourceType);
            bundle.putString("url", url);
            fragment.setArguments(bundle);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(fragment instanceof MoreMyResourcesExternalLinksFragment) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("reopen", reopen);
            bundle.putString("TOOLBAR_NAME", myResourceType);
            fragment.setArguments(bundle);
        }
        transaction.add(R.id.more_my_resources_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName() + reopen).commit();
    }

    public List<ExternalLink> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<ExternalLink> externalLinks) {
        this.externalLinks = externalLinks;
    }
}