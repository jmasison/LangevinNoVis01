package org.vcell.messaging;

public interface VCellMessaging {
    void sendWorkerEvent(WorkerEvent event);
    void close();
}
