package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.ActivityLog;
import com.myctca.model.ActivityLogItem;
import com.myctca.util.MyCTCADateUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class ActivityLogsServiceTest {
    private static final String JSON_ACTIVITY_LOGS = "activitylogs.json";
    private static final String PURPOSE = "ACTIVITY_LOGS";
    private static final String JSON_APPTS = "appointments.json";

    @InjectMocks
    private ActivityLogsService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private ActivityLogsService.ActivityLogsListener activityLogsListener;
    @Mock
    private ActivityLogsService.ActivityLogsDatesListener activityLogsDatesListener;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;
    @Mock
    private Date date;
    @Mock
    private MyCTCADateUtils dateUtils;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getActivityLogs()).thenReturn(getActivityLogsMap());
        dateUtils.setTimeToMidnight(date);
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        ActivityLogsService activityLogsService = service.getInstance();
        assertThat(activityLogsService).isNotNull();
        assertThat(activityLogsService).isInstanceOf(ActivityLogsService.class);
    }

    private Map<Date, List<ActivityLogItem>> getActivityLogsMap() {
        return new HashMap<>();
    }

    private ActivityLog getMockedActivityLogs() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ActivityLog activityLog = new ActivityLog();
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_ACTIVITY_LOGS).
                    toURI())).parallel().collect(Collectors.joining());
            activityLog = new Gson().fromJson(json, ActivityLog.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return activityLog;
    }

    @Test
    public void notifyFetchSuccess() {
        assertThat(getMockedActivityLogs()).isNotNull();
        service.notifyFetchSuccess(new Gson().toJson(getMockedActivityLogs()), PURPOSE);
        assertThat(appSessionManager.getActivityLogs()).isNotEmpty();
        verify(activityLogsListener).notifyFetchSuccess(appSessionManager.getActivityLogs(), new ArrayList<>());
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, PURPOSE);
        verify(activityLogsListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
    }

    @Test
    public void applyFilterOnActivityLogs() {
        final Calendar today = Calendar.getInstance();
        Calendar nextDate = today;
        nextDate.add(Calendar.DATE, 1);
        service.applyFilterOnActivityLogs("tanja.test", "chicago", new SimpleDateFormat().format(today.getTime()), new SimpleDateFormat().format(nextDate.getTime()));
        assertThat(service.filterActivityLogs).isNotEmpty();
        assertThat(service.filterActivityLogs.size()).isEqualTo(7);
    }

}