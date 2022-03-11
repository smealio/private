package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MailDetailFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;
import com.myctca.model.MailBox;
import com.myctca.model.MailBoxTask;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.UserPermissions;
import com.myctca.service.MailService;
import com.myctca.service.SessionFacade;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MailDetailActivity extends MyCTCAActivity implements MailService.MailServicePostListener {

    public static final int MAIL_DETAIL_REQUEST = 1;
    private static final String TAG = "myCTCA-MAILDETAIL";
    private Mail mail;
    private boolean archiveSuccess = false;
    private SessionFacade sessionFacade;

    public static Intent newIntent(Context packageContext, Mail mail) {
        Intent intent = new Intent(packageContext, MailDetailActivity.class);
        intent.putExtra("mail", mail);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionFacade = new SessionFacade();
        setContentView(R.layout.activity_mail_detail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.mail_detail_fragment_container);

        if (fragment == null) {
            fragment = new MailDetailFragment();
            fm.beginTransaction()
                    .add(R.id.mail_detail_fragment_container, fragment)
                    .commit();
        }
        selectedFragment = fragment;
        this.mail = (Mail) getIntent().getSerializableExtra("mail");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (this.mail.getFolderName().equals(MailBox.ARCHIVE_BOX)) {
            inflater.inflate(R.menu.menu_mail_detail_archive, menu);
        } else {
            inflater.inflate(R.menu.menu_mail_detail, menu);
            if (!isProxyUser() && !sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.SEND_SECURE_MESSAGES)) {
                menu.getItem(0).setVisible(false);
            } else{
                menu.getItem(0).setVisible(true);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void showPermissionAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(getString(R.string.permisson_not_granted_title))
                .setMessage(message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //do nothing
                }).create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary)));
        if (!isFinishing())
            dialog.show();
    }

    public boolean isProxyUser() {
        SessionFacade sessionFacade = new SessionFacade();
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
            }
        }
        return !isImpersonating;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            case R.id.mail_reply:
                Log.d(TAG, "REPLY PRESSED");
                if (isProxyUser()) {
                    showPermissionAlert(getString(R.string.permisson_not_granted_message_proxy));
                } else {
                    Intent replyMailIntent = MailNewActivity.newReplyIntent(this, this.mail);
                    startActivity(replyMailIntent);
                }
                break;
            case R.id.mail_archive:
                Log.d(TAG, "ARCHIVE PRESSED");
                if (isProxyUser()) {
                    showPermissionAlert(getString(R.string.permisson_not_granted_message_proxy));
                } else {
                    archiveMail(this.mail);
                }
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }
        return true;
    }

    public Mail getMail() {
        return mail;
    }

    public void archiveMail(final Mail archMail) {
        Map<String, String> params = new HashMap<>();
        params.put("mailMessageId", archMail.getMailMessageId());
        sessionFacade.setOnServer(this, this, new JSONObject(params).toString(), MailBoxTask.ARCHIVE);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: archiveSuccess: " + archiveSuccess);
        Intent returnIntent = new Intent();
        if (archiveSuccess || ((MailDetailFragment) selectedFragment).ifReadSuccess()) {
            returnIntent.putExtra("result", Mail.DO_REFRESH);
            setResult(Mail.DO_REFRESH, returnIntent);
        } else {
            returnIntent.putExtra("result", Mail.CANCEL_REFRESH);
            setResult(Mail.CANCEL_REFRESH, returnIntent);
        }
        finish();
        super.onBackPressed();
    }

    public void showMailDetailFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary)));
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void notifyPostSuccess(boolean success, String response, int task) {
        archiveSuccess = success;
        if (success) {
            showSnack(getString(R.string.mail_detail_archive_success));
            sessionFacade.clearMails("MAIL_ARCHIVED");
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_archive_mail);
        CTCAAnalyticsManager.createEvent("MailDetailActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        hideActivityIndicator();
        if(message.isEmpty())
            message = getString(R.string.error_400);
        showMailDetailFailure(message);
    }
}
