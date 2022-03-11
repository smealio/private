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
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.ExternalLink;
import com.myctca.network.GetClient;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.myctca.util.GeneralUtil.noNull;

public class MyResourcesService implements GetListener {
    private static final String TAG = MyResourcesService.class.getSimpleName();
    private static MyResourcesService resourcesService;
    private GetClient getClient;
    private MyResourceServiceListener listener;
    private Context context;

    public static MyResourcesService getInstance() {
        if (resourcesService == null) {
            resourcesService = new MyResourcesService();
        }
        return resourcesService;
    }

    public void downloadAllExternalLinks(Context context, MyResourceServiceListener listener, String purpose) {
        this.listener = listener;
        this.context = context;
        if (AppSessionManager.getInstance().getExternalLinksList().isEmpty()) {
            String url = BuildConfig.myctca_server + context.getString(R.string.myctca_external_links);
            getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else {
            listener.notifyFetchSuccess(AppSessionManager.getInstance().getExternalLinksList());
        }
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            ExternalLink[] externalLinks = new Gson().fromJson(parseSuccess, ExternalLink[].class);
            AppSessionManager.getInstance().setExternalLinksList(new LinkedList<>(Arrays.asList(externalLinks)));
            Log.d(TAG, "External links retrieved: " + AppSessionManager.getInstance().getExternalLinksList().size());
            listener.notifyFetchSuccess(new LinkedList<>(Arrays.asList(externalLinks)));
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            listener.notifyFetchError(context.getString(R.string.error_400));
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", noNull(error.getMessage()));
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_external_links);
        CTCAAnalyticsManager.createEvent("MyResources:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context));
    }

    public interface MyResourceServiceListener {
        void notifyFetchSuccess(List<ExternalLink> externalLinks);

        void notifyFetchError(String message);
    }

}
