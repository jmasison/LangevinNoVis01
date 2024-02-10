package org.vcell.messaging;

//import okhttp3.HttpUrl;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VCellMessagingRestTest {
//    private MockWebServer mockWebServer;

//    @BeforeEach
//    public void setUp() throws IOException {
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();
//    }

//    @AfterEach
//    public void tearDown() throws IOException {
//        mockWebServer.shutdown();
//    }

//    @Test
//    public void testSendWorkerEvent_starting() throws Exception {
//        // Arrange
//        mockWebServer.enqueue(new MockResponse().setBody("OK"));
//
//        HttpUrl url = mockWebServer.url("/");
//
//        MessagingConfig config = new MessagingConfig (
//                url.host(),
//                url.port(),
//                "msg_user",
//                "msg_pswd",
//                "localhost",
//                "schaff",
//                "12334483837",
//                0,
//                0
//                );
//
//        VCellMessagingRest vCellMessagingRest = new VCellMessagingRest(config);
//
//        String expectedPath =
//                "/api/message/workerEvent" +
//                        "?type=queue" +
//                        "&JMSPriority=5" +
//                        "&JMSTimeToLive=600000" +
//                        "&JMSDeliveryMode=persistent" +
//                        "&MessageType=WorkerEvent" +
//                        "&UserName=schaff" +
//                        "&HostName=localhost" +
//                        "&SimKey=12334483837" +
//                        "&TaskID=0" +
//                        "&JobIndex=0" +
//                        "&WorkerEvent_Status=JOB_STARTING" +
//                        "&WorkerEvent_StatusMsg=Starting+Job" +
//                        "&WorkerEvent_Progress=0.0" +
//                        "&WorkerEvent_TimePoint=0.0";
//
//        vCellMessagingRest.sendWorkerEvent(WorkerEvent.startingEvent("Starting Job"));
//
//        RecordedRequest request = mockWebServer.takeRequest();
//        assertEquals("POST", request.getMethod());
//        assertEquals(expectedPath, request.getPath());
//        assertEquals("", request.getBody().readUtf8());
//    }

    @Test
    public void testMessagingConfig() throws IOException {
        MessagingConfig config = new MessagingConfig(
                "localhost",
                8165,
                "msg_user",
                "msg_pswd",
                "localhost",
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
                compute_hostname=localhost
                vc_username=vcell_user
                simKey=12334483837
                taskID=0
                jobIndex=0
                """;

        Properties props = new Properties();
        props.load(new StringReader(properties_expected));
        MessagingConfig config2 = new MessagingConfig(props);

        // test the parsed json is equal to the original config
        assertEquals(config, config2);
    }
}