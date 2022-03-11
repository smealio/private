package com.myctca.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.model.ActivityLog;
import com.myctca.model.ActivityLogItem;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.network.GetClient;
import com.myctca.util.MyCTCADateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.myctca.util.GeneralUtil.noNull;


public class ActivityLogsService implements GetListener {

    private static final String TAG = ActivityLogsService.class.getSimpleName();
    private static final String PURPOSE = "ACTIVITY_LOGS";
    private static final String ANDSTR = "\"and\"";
    private static ActivityLogsService activityLogsService;
    protected List<String> filterActivityLogs = new ArrayList<>();
    private ActivityLogsListener listener;
    private String removeFilters = "Remove Filters";
    private Context context;

    public static ActivityLogsService getInstance() {
        if (activityLogsService == null) {
            activityLogsService = new ActivityLogsService();
        }
        return activityLogsService;
    }

    public void downloadActivityLogs(Context context, ActivityLogsListener listener, int skip, int take, String purpose) {
        if (purpose.equals(removeFilters)) {
            filterActivityLogs.clear();
        }
        this.listener = listener;
        this.context = context;
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_activity_logs);
        Log.d(TAG, "URL: " + url);
        Map<String, String> params = new HashMap<>();
        params.put("skip", String.valueOf(skip));
        params.put("take", String.valueOf(take));
        if (!filterActivityLogs.isEmpty())
            params.put("filter", filterActivityLogs.toString());

        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, params, PURPOSE);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            ActivityLog activityLog = new Gson().fromJson(parseSuccess, ActivityLog.class);
            Date dateKey = null;
            AppSessionManager.getInstance().clearActivityLogs();

            for (int i = 0; i < activityLog.getData().size(); i++) {
                if (dateKey != null) {
                    Date newDateKey = MyCTCADateUtils.setTimeToMidnight(activityLog.getData().get(i).getDate());
                    Log.d(TAG, "newDateKey: " + newDateKey);
                    if (dateKey.equals(newDateKey)) {
                        List<ActivityLogItem> activityLogItems = AppSessionManager.getInstance().getActivityLogs().get(dateKey);
                        activityLogItems.add(activityLog.getData().get(i));
                        Log.d(TAG, "activityLogItems size: " + activityLogItems.size());
                    } else {
                        dateKey = newDateKey;
                        List<ActivityLogItem> activityLogItems = new ArrayList<>();
                        activityLogItems.add(activityLog.getData().get(i));

                        AppSessionManager.getInstance().getActivityLogs().put(dateKey, activityLogItems);
                    }
                } else {
                    Log.d(TAG, "DATEKEY IS NULL");
                    dateKey = MyCTCADateUtils.setTimeToMidnight(activityLog.getData().get(i).getDate());
                    Log.d(TAG, "dateKey: " + dateKey);
                    List<ActivityLogItem> activityLogItems = new ArrayList<>();
                    activityLogItems.add(activityLog.getData().get(i));
                    AppSessionManager.getInstance().getActivityLogs().put(dateKey, activityLogItems);
                }
            }
            // Build Dates Array and sort in descending order
            Set<Date> dateKeys = AppSessionManager.getInstance().getActivityLogs().keySet();
            AppSessionManager.getInstance().setmActivityLogsDates(new ArrayList<>(dateKeys));
            Collections.sort(AppSessionManager.getInstance().getmActivityLogsDates(), Collections.reverseOrder());
            Log.d(TAG, "ActivityLogs: size: " + AppSessionManager.getInstance().getActivityLogs().size());
            listener.notifyFetchSuccess(AppSessionManager.getInstance().getActivityLogs(), AppSessionManager.getInstance().getmActivityLogsDates());
        } catch (JsonParseException e) {
            listener.notifyFetchError(context.getString(R.string.error_400));
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_activity_logs);
        CTCAAnalyticsManager.createEvent("MoreActivityLogsFragment:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        Log.d("Error.Response", noNull(error.getMessage()));
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context));
    }

    public void applyFilterOnActivityLogs(String etFilterUsername, String etFilterMessage, String selectedDate, String nextDate) {
        filterActivityLogs.clear();

        if (!TextUtils.isEmpty(etFilterMessage)) {
            if (!filterActivityLogs.isEmpty())
                filterActivityLogs.add(ANDSTR);
            filterActivityLogs.add("[\"formattedMessage\",\"contains\",\"" + etFilterMessage + "\"]");
        }
        if (!TextUtils.isEmpty(etFilterUsername)) {
            if (!filterActivityLogs.isEmpty())
                filterActivityLogs.add(ANDSTR);
            filterActivityLogs.add("[\"userName\",\"contains\",\"" + etFilterUsername + "\"]");
        }
        if (!TextUtils.isEmpty(selectedDate)) {
            if (!filterActivityLogs.isEmpty())
                filterActivityLogs.add(ANDSTR);
            filterActivityLogs.add("[[\"timestamp\",\">=\",\"" + selectedDate + "\"]");
            filterActivityLogs.add(ANDSTR);
            filterActivityLogs.add("[\"timestamp\",\"<\",\"" + nextDate + "\"]]");
        }
    }

    public void getSelectedAndNextDate(ActivityLogsDatesListener datesListener, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        String selectedDate = sdf.format(c.getTime());
        c.add(Calendar.DATE, 1);
        String nextDate = sdf.format(c.getTime());
        datesListener.notifyDateSelected(selectedDate, nextDate);
    }


    public interface ActivityLogsListener {
        void notifyFetchSuccess(Map<Date, List<ActivityLogItem>> activityLogs, List<Date> getmActivityLogsDates);

        void notifyFetchError(String message);
    }

    public interface ActivityLogsDatesListener {
        void notifyDateSelected(String selectedDate, String nextDate);
    }
}
