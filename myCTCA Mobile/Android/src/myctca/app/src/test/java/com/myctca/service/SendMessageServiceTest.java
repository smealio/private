package com.myctca.service;

import com.myctca.common.AppSessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class SendMessageServiceTest {

    private static final String JSON_ALERTS = "homeAlerts.json";
    private static final String JSON_VERSIONS = "applicationVersions.json";
    private static final String HOME_ALERTS = "HOME_ALERTS";
    @InjectMocks
    private SendMessageService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private SendMessageService.SendMessageInterface sendMessageInterface;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        SendMessageService sendMessageService = service.getInstance();
        assertThat(sendMessageService).isNotNull();
        assertThat(sendMessageService).isInstanceOf(SendMessageService.class);
    }

    @Test
    public void notifyPostSuccess() {
        service.notifyPostSuccess("success", 12);
        verify(sendMessageInterface).notifyPostSuccess("success");
        service.notifyPostSuccess("error", 12);
        verify(sendMessageInterface).notifyPostSuccess("error");
    }
}