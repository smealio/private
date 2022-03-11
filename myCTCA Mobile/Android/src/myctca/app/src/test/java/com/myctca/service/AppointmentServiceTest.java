package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentForm;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class AppointmentServiceTest {

    private static final String JSON_APPTS = "appointments.json";
    private static final String APPT_UPCOMING = "APPT_UPCOMING";
    private static final String APPT_PAST = "APPT_PAST";
    private static final String APPT_ALL = "APPTs";
    @InjectMocks
    private AppointmentService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private AppointmentService.AppointmentServiceGetListener appointmentServiceGetListener;
    @Mock
    private AppointmentService.AppointmentServicePostListener appointmentServicePostListener;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getAppointments()).thenReturn(getMockedAppointments());
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        AppointmentService appointmentService = service.getInstance();
        assertThat(appointmentService).isNotNull();
        assertThat(appointmentService).isInstanceOf(AppointmentService.class);
    }

    @Test
    public void getAppointments() {
        service.getAppointments(context, APPT_UPCOMING, appointmentServiceGetListener);
        verify(appointmentServiceGetListener).notifyFetchSuccess(Collections.EMPTY_MAP, Collections.EMPTY_LIST, APPT_UPCOMING);

        service.getAppointments(context, APPT_PAST, appointmentServiceGetListener);
        verify(appointmentServiceGetListener).notifyFetchSuccess(Collections.EMPTY_MAP, Collections.EMPTY_LIST, APPT_PAST);
    }


    @Test
    public void clearAppointments() {
        assertThat(appSessionManager.getAppointments()).isNotEmpty();
        service.clearAppointments();
        assertThat(appSessionManager.getAppointments()).isEmpty();
    }

    @Test
    public void isAppointmentsEmpty() {
        boolean isApptEmpty = service.isAppointmentsEmpty();
        if (appSessionManager.getAppointments().isEmpty())
            assertThat(isApptEmpty).isTrue();
        else
            assertThat(isApptEmpty).isFalse();
    }

    @Test
    public void dateTimeIsValid() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //if it is current date, then should return false.
        assertThat(service.dateTimeIsValid(calendar, calendar)).isFalse();

        //if it is current date, and time is less than 24 hours, return false
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 23);
        assertThat(service.dateTimeIsValid(calendar, calendar)).isFalse();

        //if it is current date, and time is greater than 24 hours, return true
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 25);
        assertThat(service.dateTimeIsValid(calendar, calendar)).isTrue();

        //if it is day after current date, then return true
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        assertThat(service.dateTimeIsValid(calendar, calendar)).isTrue();
    }

    @Test
    public void notifyFetchSuccess() {
        assertThat(getMockedAppointments()).isNotEmpty();
        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getAppointments()), APPT_ALL);
        assertThat(appSessionManager.getAppointments()).isNotEmpty();
    }

    @Test
    public void buildUpcomingApptData() {
        when(appSessionManager.getUpcomingAppointments()).thenReturn(getAppointmentsMap());
        service.buildUpcomingApptData(getMockedAppointments());
        assertThat(appSessionManager.getUpcomingAppointments()).isNotEmpty();
    }

    @Test
    public void buildPastApptData() {
        when(appSessionManager.getPastAppointments()).thenReturn(getAppointmentsMap());
        service.buildPastApptData(appSessionManager.getAppointments());
        assertThat(appSessionManager.getPastAppointments()).isNotEmpty();
    }

    @Test
    public void returnAppointmentsData() {
        service.returnAppointmentsData(APPT_PAST);
        verify(appointmentServiceGetListener, times(1))
                .notifyFetchSuccess(Collections.EMPTY_MAP,
                        Collections.EMPTY_LIST,
                        APPT_PAST);

        service.returnAppointmentsData(APPT_UPCOMING);
        verify(appointmentServiceGetListener, times(1))
                .notifyFetchSuccess(Collections.EMPTY_MAP,
                        Collections.EMPTY_LIST,
                        APPT_UPCOMING);
    }

    @Test
    public void notifyPostSuccess() {
        service.notifyPostSuccess(new Gson().toJson(new AppointmentForm()), 1);
        verify(appointmentServicePostListener, times(1))
                .notifyPostSuccess(false, "");
        service.notifyPostSuccess("{ error:\"Error occurred. Try later.\" }", 1);
        verify(appointmentServicePostListener, times(1))
                .notifyPostSuccess(false, "");
    }

    @Test
    public void notifyPostError() {
        service.notifyPostError(volleyError, "message", 1);
        verify(appointmentServicePostListener, times(1))
                .notifyPostError(volleyError, "message", 1);
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, APPT_UPCOMING);
        verify(appointmentServiceGetListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), APPT_UPCOMING);

        service.notifyFetchError(volleyError, APPT_PAST);
        verify(appointmentServiceGetListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), APPT_PAST);
    }

    private List<Appointment> getMockedAppointments() {
        List<Appointment> appointments;
        ClassLoader classLoader = this.getClass().getClassLoader();
        Appointment[] apptArray = new Appointment[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_APPTS).
                    toURI())).parallel().collect(Collectors.joining());
            apptArray = new Gson().fromJson(json, Appointment[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        appointments = new LinkedList<>(Arrays.asList(apptArray));

        return appointments;
    }

    private Map<String, List<Appointment>> getAppointmentsMap() {
        return new HashMap<>();
    }
}