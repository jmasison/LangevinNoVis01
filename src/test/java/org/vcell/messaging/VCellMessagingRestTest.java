package org.vcell.messaging;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VCellMessagingRestTest {
    private MockWebServer mockWebServer;
    private VCellMessagingRest vCellMessagingRest;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        HttpUrl url = mockWebServer.url("/");

        MessagingConfig config = new MessagingConfig(
                url.host(),
                url.port(),
                "msg_user",
                "msg_pswd",
                InetAddress.getLocalHost().getHostName(),
                "vcell_user",
                "12334483837",
                0,
                0
        );
        vCellMessagingRest = new VCellMessagingRest(config);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testSendWorkerEvent_starting() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        String expectedPath_start =
                "/api/message/workerEvent" +
                        "?type=queue" +
                        "&JMSPriority=5" +
                        "&JMSTimeToLive=600000" +
                        "&JMSDeliveryMode=persistent" +
                        "&MessageType=WorkerEvent" +
                        "&UserName=vcell_user" +
                        "&HostName=" + InetAddress.getLocalHost().getHostName() +
                        "&SimKey=12334483837" +
                        "&TaskID=0" +
                        "&JobIndex=0" +
                        "&WorkerEvent_Status=999" +
                        "&WorkerEvent_StatusMsg=Starting+Job" +
                        "&WorkerEvent_Progress=0.0" +
                        "&WorkerEvent_TimePoint=0.0";

        vCellMessagingRest.sendWorkerEvent(WorkerEvent.startingEvent("Starting Job"));

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals(expectedPath_start, request.getPath());
        assertEquals("", request.getBody().readUtf8());
    }

    @Test
    public void testSendWorkerEvent_progress() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setBody("OK"));

        String expectedPath_progress =
                "/api/message/workerEvent" +
                        "?type=queue" +
                        "&JMSPriority=5" +
                        "&JMSTimeToLive=60000" +
                        "&JMSDeliveryMode=nonpersistent" +
                        "&MessageType=WorkerEvent" +
                        "&UserName=vcell_user" +
                        "&HostName=" + InetAddress.getLocalHost().getHostName() +
                        "&SimKey=12334483837" +
                        "&TaskID=0" +
                        "&JobIndex=0" +
                        "&WorkerEvent_Status=1001" +
//                        "&WorkerEvent_StatusMsg=Starting+Job" +
                        "&WorkerEvent_Progress=0.4" +
                        "&WorkerEvent_TimePoint=2.0";

        vCellMessagingRest.sendWorkerEvent(WorkerEvent.progressEvent(0.4, 2.0));

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals(expectedPath_progress, request.getPath());
        assertEquals("", request.getBody().readUtf8());
    }

    @Test
    public void testMessagingConfig() throws IOException {
        MessagingConfig config = new MessagingConfig(
                "localhost",
                8165,
                "msg_user",
                "msg_pswd",
                InetAddress.getLocalHost().getHostName(),
                "vcell_user",
                "12334483837",
                0,
                0
        );

        String properties_expected = """
                broker_host=localhost
                broker_port=8165
                broker_username=msg_user
                broker_password=msg_pswd
                vc_username=vcell_user
                simKey=12334483837
                taskID=0
                jobIndex=0
                """;
        // note that compute_hostname in MessageConfig is computed dynamically
        // from InetAddress.getLocalHost().getHostName()

        Properties props = new Properties();
        props.load(new StringReader(properties_expected));
        MessagingConfig config2 = new MessagingConfig(props);

        // test the parsed json is equal to the original config
        assertEquals(config, config2);
    }
}