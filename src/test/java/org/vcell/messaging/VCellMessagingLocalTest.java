package org.vcell.messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class VCellMessagingLocalTest {

    PrintStream stdout;
    ByteArrayOutputStream boas_stdout;
    PrintStream stderr;
    ByteArrayOutputStream boas_stderr;

    @BeforeEach
    public void setUp() {
        boas_stdout = new ByteArrayOutputStream();
        stdout = new PrintStream(boas_stdout);
        boas_stderr = new ByteArrayOutputStream();
        stderr = new PrintStream(boas_stderr);
    }

    @AfterEach
    public void tearDown() {
        boas_stdout.reset();
        boas_stderr.reset();
    }

    private String getStdout() {
        return boas_stdout.toString();
    }

    private String getStderr() {
        return boas_stderr.toString();
    }

    @Test
    public void testVCellMessagingLocal_normal() throws InterruptedException {
        long progressInterval_ms = 1;
        VCellMessagingLocal vcellMessaging = new VCellMessagingLocal(stdout, stderr, progressInterval_ms);

        // test sendWorkerEvent
        vcellMessaging.sendWorkerEvent(WorkerEvent.startingEvent("Starting Job"));
        vcellMessaging.sendWorkerEvent(WorkerEvent.dataEvent(0.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(0.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.dataEvent(1.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(0.4));
        Thread.sleep(2*progressInterval_ms);
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(0.5));
        vcellMessaging.sendWorkerEvent(WorkerEvent.dataEvent(2.0));
        Thread.sleep(2*progressInterval_ms);
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(1.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.completedEvent());

        try {
            Thread.sleep(2*progressInterval_ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stderr.flush();
        stdout.flush();

        // compare the stdout to the expected stdout
        String expected_stdout = """
                Starting Job
                [[[data:0.0]]]
                [[[progress:0.0]]]
                [[[data:1.0]]]
                [[[progress:50.0]]]
                [[[data:2.0]]]
                [[[progress:100.0]]]
                """;
        Assertions.assertEquals(expected_stdout, getStdout());

        // compare the stderr to the expected stderr
        String expected_stderr = """
                Simulation Complete in Main() ...
                """;
        Assertions.assertEquals(expected_stderr, getStderr());
    }

    @Test
    public void testVCellMessagingLocal_failure() throws InterruptedException {
        long progressInterval_ms = 1;
        VCellMessagingLocal vcellMessaging = new VCellMessagingLocal(stdout, stderr, progressInterval_ms);

        // test sendWorkerEvent
        vcellMessaging.sendWorkerEvent(WorkerEvent.startingEvent("Starting Job"));
        vcellMessaging.sendWorkerEvent(WorkerEvent.dataEvent(0.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(0.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.dataEvent(1.0));
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(0.5));
        Thread.sleep(2*progressInterval_ms);
        vcellMessaging.sendWorkerEvent(WorkerEvent.progressEvent(0.6));
        vcellMessaging.sendWorkerEvent(WorkerEvent.failureEvent("Failure"));


        stderr.flush();
        stdout.flush();

        // compare the stdout to the expected stdout
        String expected_stdout = """
                Starting Job
                [[[data:0.0]]]
                [[[progress:0.0]]]
                [[[data:1.0]]]
                [[[progress:60.0]]]
                """;
        Assertions.assertEquals(expected_stdout, getStdout());

        // compare the stderr to the expected stderr
        String expected_stderr = """
                Failure
                """;
        Assertions.assertEquals(expected_stderr, getStderr());
    }
}
