package com.myctca.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.interfaces.PostListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;
import com.myctca.model.MailBoxTask;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.myctca.util.GeneralUtil.noNull;

public class MailService implements GetListener, PostListener {
    private static final String TAG = MailService.class.getSimpleName();
    private static final String MAIL_SENT = "MAIL_SENT";
    private static final String MAIL_INBOX = "MAIL_INBOX";
    private static final String MAIL_ARCHIVED = "MAIL_ARCHIVED";
    private static MailService mailService;
    private MailServiceGetListener getListener;
    private MailServicePostListener postListener;
    private Context context;

    public static MailService getInstance() {
        if (mailService == null) {
            mailService = new MailService();
        }
        return mailService;
    }

    public void downloadMail(Context context, Map<String, String> params, String purpose, MailServiceGetListener listener) {
        this.getListener = listener;
        this.context = context;
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_mail);
        if (shouldDownloadMail(purpose)) {
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, params, purpose);
        }
    }

    protected boolean shouldDownloadMail(String purpose) {
        switch (purpose) {
            case MAIL_SENT:
                if (!AppSessionManager.getInstance().getmSentbox().isEmpty()) {
                    getListener.notifyFetchSuccess(AppSessionManager.getInstance().getmSentbox(), purpose);
                    return false;
                }
                break;
            case MAIL_INBOX:
                if (!AppSessionManager.getInstance().getmInbox().isEmpty()) {
                    getListener.notifyFetchSuccess(AppSessionManager.getInstance().getmInbox(), purpose);
                    return false;
                }
                break;
            case MAIL_ARCHIVED:
                if (!AppSessionManager.getInstance().getmArchivebox().isEmpty()) {
                    getListener.notifyFetchSuccess(AppSessionManager.getInstance().getmArchivebox(), purpose);
                    return false;
                }
                break;
            default:
                //do nothing
        }
        return true;
    }

    public void clearMails(String purpose) {
        switch (purpose) {
            case MAIL_INBOX:
                AppSessionManager.getInstance().getmInbox().clear();
                break;
            case MAIL_SENT:
                AppSessionManager.getInstance().getmSentbox().clear();
                break;
            case MAIL_ARCHIVED:
                AppSessionManager.getInstance().getmArchivebox().clear();
                break;
            default:
                //do nothing
        }
    }

    public void setOnServer(Context context, MailServicePostListener listener, int task, String body) {
        this.postListener = listener;
        String url;
        switch (task) {
            case MailBoxTask.MARK_AS_READ:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_read_mail);
                Log.d(TAG, "markAsRead url: " + url);
                break;
            case MailBoxTask.ARCHIVE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_archive_mail);
                Log.d(TAG, "submitForm url: " + url);
                break;
            case MailBoxTask.SEND_NEW:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_new_mail);
                Log.d(TAG, "submitForm url: " + url);
                break;
            default:
                url = "";
        }
        PostClient postClient = new PostClient(this, context, task);
        postClient.sendData(url, body, null);
    }

    protected boolean checkInteger(String stringInt) {
        try {
            Integer.parseInt(stringInt.substring(1, stringInt.length() - 1));
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error" + e);
            return false;
        }
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        switch (purpose) {
            case MAIL_SENT:
                try {
                    Mail[] sentMails = new Gson().fromJson(parseSuccess, Mail[].class);
                    AppSessionManager.getInstance().setmSentbox(new LinkedList<>(Arrays.asList(sentMails)));
                    Log.d(TAG, "Sent box retrieved: " + AppSessionManager.getInstance().getmSentbox().size());
                    getListener.notifyFetchSuccess(AppSessionManager.getInstance().getmSentbox(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception:" + exception);
                    getListener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            case MAIL_INBOX:
                try {
                    Mail[] inboxMails = new Gson().fromJson(parseSuccess, Mail[].class);
                    AppSessionManager.getInstance().setmInbox(new LinkedList<>(Arrays.asList(inboxMails)));
                    Log.d(TAG, "Inbox retrieved: " + AppSessionManager.getInstance().getmInbox().size());
                    getListener.notifyFetchSuccess(AppSessionManager.getInstance().getmInbox(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception:" + exception);
                    getListener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            case MAIL_ARCHIVED:
                try {
                    Mail[] archivedMails = new Gson().fromJson(parseSuccess, Mail[].class);
                    AppSessionManager.getInstance().setmArchivebox(new LinkedList<>(Arrays.asList(archivedMails)));
                    Log.d(TAG, "Archived box retrieved: " + AppSessionManager.getInstance().getmArchivebox().size());
                    getListener.notifyFetchSuccess(AppSessionManager.getInstance().getmArchivebox(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception:" + exception);
                    getListener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            default: //do nothing
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", noNull(error.getMessage()));
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_mail);
        CTCAAnalyticsManager.createEvent("MailService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        getListener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        Log.d(TAG, "New Mail Request Response: " + response + "::: task: " + task);
        switch (task) {
            case MailBoxTask.MARK_AS_READ:
                postListener.notifyPostSuccess(checkInteger(response), response, task);
                Log.d(TAG, "MarkAsRead Mail Request Response: " + response + "::: readSuccess: " + checkInteger(response));
                break;
            case MailBoxTask.ARCHIVE:
                postListener.notifyPostSuccess(response.equals("\"Success\""), response, task);
                Log.d(TAG, "Archive Mail Request Response: " + response + "::: archiveSuccess: " + response.equals("\"Success\""));
                break;
            case MailBoxTask.SEND_NEW:
                postListener.notifyPostSuccess(response.equals("\"Success\""), response, task);
                Log.d(TAG, "Send New Mail Request Response: " + response + "::: newMailSuccess: " + response.equals("\"Success\""));
                break;
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        if(message.isEmpty())
            message = context.getString(R.string.error_400);
        postListener.notifyPostError(error, message, task);
    }

    public interface MailServiceGetListener {
        void notifyFetchSuccess(List<Mail> mail, String purpose);

        void notifyFetchError(String message, String purpose);
    }

    public interface MailServicePostListener {
        void notifyPostSuccess(boolean success, String response, int task);

        void notifyPostError(VolleyError error, String message, int task);
    }
}
