package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.Mail;
import com.myctca.model.MailBoxTask;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class MailServiceTest {
    private static final String MAIL_SENT = "MAIL_SENT";
    private static final String MAIL_INBOX = "MAIL_INBOX";
    private static final String MAIL_ARCHIVED = "MAIL_ARCHIVED";
    private static final String JSON_MAILS = "mails.json";

    @InjectMocks
    private MailService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private MailService.MailServiceGetListener getListener;
    @Mock
    private MailService.MailServicePostListener postListener;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        MailService mailService = service.getInstance();
        assertThat(mailService).isNotNull();
        assertThat(mailService).isInstanceOf(MailService.class);
    }

    @Test
    public void downloadMail() {
        Map<String, String> params = new HashMap<>();
        params.put("mailFolder", "SentItems");
        when(appSessionManager.getmSentbox()).thenReturn(getMockedMails());
        service.downloadMail(context, params, MAIL_SENT, getListener);
    }

    @Test
    public void shouldDownloadMail() {
        when(appSessionManager.getmSentbox()).thenReturn(Collections.EMPTY_LIST);
        boolean response = service.shouldDownloadMail(MAIL_SENT);
        assertThat(response).isTrue();

        when(appSessionManager.getmSentbox()).thenReturn(getMockedMails());
        response = service.shouldDownloadMail(MAIL_SENT);
        assertThat(response).isFalse();

        when(appSessionManager.getmInbox()).thenReturn(Collections.EMPTY_LIST);
        response = service.shouldDownloadMail(MAIL_INBOX);
        assertThat(response).isTrue();

        when(appSessionManager.getmInbox()).thenReturn(getMockedMails());
        response = service.shouldDownloadMail(MAIL_INBOX);
        assertThat(response).isFalse();

        when(appSessionManager.getmArchivebox()).thenReturn(Collections.EMPTY_LIST);
        response = service.shouldDownloadMail(MAIL_ARCHIVED);
        assertThat(response).isTrue();

        when(appSessionManager.getmArchivebox()).thenReturn(getMockedMails());
        response = service.shouldDownloadMail(MAIL_ARCHIVED);
        assertThat(response).isFalse();
    }

    private List<Mail> getMockedMails() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Mail[] mailList = new Mail[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_MAILS).
                    toURI())).parallel().collect(Collectors.joining());
            mailList = new Gson().fromJson(json, Mail[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(mailList));
    }

    @Test
    public void clearMails() {
        when(appSessionManager.getmSentbox()).thenReturn(getMockedMails());
        assertThat(appSessionManager.getmSentbox()).isNotEmpty();
        service.clearMails(MAIL_SENT);
        assertThat(appSessionManager.getmSentbox()).isEmpty();

        when(appSessionManager.getmInbox()).thenReturn(getMockedMails());
        assertThat(appSessionManager.getmInbox()).isNotEmpty();
        service.clearMails(MAIL_INBOX);
        assertThat(appSessionManager.getmSentbox()).isEmpty();

        when(appSessionManager.getmArchivebox()).thenReturn(getMockedMails());
        assertThat(appSessionManager.getmArchivebox()).isNotEmpty();
        service.clearMails(MAIL_ARCHIVED);
        assertThat(appSessionManager.getmArchivebox()).isEmpty();
    }

    @Test
    public void checkInteger() {
        boolean isInt = service.checkInteger("\"0\"");
        assertThat(isInt).isTrue();

        isInt = service.checkInteger("random string");
        assertThat(isInt).isFalse();
    }

    @Test
    public void notifyFetchSuccess() {
        when(appSessionManager.getmSentbox()).thenReturn(getMockedMails());
        service.notifyFetchSuccess(new Gson().toJson(getMockedMails()), MAIL_SENT);
        assertThat(appSessionManager.getmSentbox()).isNotEmpty();
        verify(getListener).notifyFetchSuccess(appSessionManager.getmSentbox(), MAIL_SENT);

        when(appSessionManager.getmArchivebox()).thenReturn(getMockedMails());
        service.notifyFetchSuccess(new Gson().toJson(getMockedMails()), MAIL_ARCHIVED);
        assertThat(appSessionManager.getmArchivebox()).isNotEmpty();
        verify(getListener).notifyFetchSuccess(appSessionManager.getmArchivebox(), MAIL_ARCHIVED);

        when(appSessionManager.getmInbox()).thenReturn(getMockedMails());
        service.notifyFetchSuccess(new Gson().toJson(getMockedMails()), MAIL_INBOX);
        assertThat(appSessionManager.getmInbox()).isNotEmpty();
        verify(getListener).notifyFetchSuccess(appSessionManager.getmInbox(), MAIL_INBOX);

        service.notifyFetchSuccess(new Gson().toJson(getMockedMails()), "NONE");
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, MAIL_SENT);
        verify(getListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), MAIL_SENT);

        service.notifyFetchError(volleyError, MAIL_ARCHIVED);
        verify(getListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), MAIL_ARCHIVED);

        service.notifyFetchError(volleyError, MAIL_INBOX);
        verify(getListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), MAIL_INBOX);
    }

    @Test
    public void notifyPostSuccess() {
        service.notifyPostSuccess("\"0\"", MailBoxTask.MARK_AS_READ);
        verify(postListener).notifyPostSuccess(true, "\"0\"", MailBoxTask.MARK_AS_READ);

        service.notifyPostSuccess("random string", MailBoxTask.MARK_AS_READ);
        verify(postListener).notifyPostSuccess(false, "random string", MailBoxTask.MARK_AS_READ);

        service.notifyPostSuccess("\"Success\"", MailBoxTask.ARCHIVE);
        verify(postListener).notifyPostSuccess(true, "\"Success\"", MailBoxTask.ARCHIVE);

        service.notifyPostSuccess("\"Failure\"", MailBoxTask.ARCHIVE);
        verify(postListener).notifyPostSuccess(false, "\"Failure\"", MailBoxTask.ARCHIVE);

        service.notifyPostSuccess("\"Success\"", MailBoxTask.SEND_NEW);
        verify(postListener).notifyPostSuccess(true, "\"Success\"", MailBoxTask.SEND_NEW);

        service.notifyPostSuccess("\"Failure\"", MailBoxTask.SEND_NEW);
        verify(postListener).notifyPostSuccess(false, "\"Failure\"", MailBoxTask.SEND_NEW);
    }

    @Test
    public void notifyPostError() {
        service.notifyPostError(volleyError, "error occurred", MailBoxTask.MARK_AS_READ);
        verify(postListener).notifyPostError(volleyError, "error occurred", MailBoxTask.MARK_AS_READ);

        service.notifyPostError(volleyError, "error occurred", MailBoxTask.ARCHIVE);
        verify(postListener).notifyPostError(volleyError, "error occurred", MailBoxTask.ARCHIVE);

        service.notifyPostError(volleyError, "error occurred", MailBoxTask.SEND_NEW);
        verify(postListener).notifyPostError(volleyError, "error occurred", MailBoxTask.SEND_NEW);
    }
}