package org.vcell.messaging;

public record WorkerEvent(
        WorkerStatus status,
        double progress,
        double timepoint,
        String eventMessage
) {
    public static WorkerEvent dataEvent(double timepoint) {
        return new WorkerEvent(WorkerStatus.JOB_DATA, 0.0, timepoint, "");
    }
    public static WorkerEvent progressEvent(double progress) {
        return new WorkerEvent(WorkerStatus.JOB_PROGRESS, progress, 0.0, "");
    }
    public static WorkerEvent startingEvent(String eventMessage) {
        return new WorkerEvent(WorkerStatus.JOB_STARTING, 0.0, 0.0, eventMessage);
    }
    public static WorkerEvent completedEvent() {
        return new WorkerEvent(WorkerStatus.JOB_COMPLETED, 0.0, 0.0, "");
    }
    public static WorkerEvent failureEvent(String eventMessage) {
        return new WorkerEvent(WorkerStatus.JOB_FAILURE, 0.0, 0.0, eventMessage);
    }
    public static WorkerEvent workerAliveEvent() {
        return new WorkerEvent(WorkerStatus.JOB_WORKER_ALIVE, 0.0, 0.0, "");
    }
}
