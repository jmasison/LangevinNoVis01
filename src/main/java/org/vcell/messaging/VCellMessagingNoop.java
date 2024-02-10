package org.vcell.messaging;

public class VCellMessagingNoop implements VCellMessaging {
    @Override
    public void sendWorkerEvent(WorkerEvent event) {
    }

    @Override
    public void close() {
    }
}
