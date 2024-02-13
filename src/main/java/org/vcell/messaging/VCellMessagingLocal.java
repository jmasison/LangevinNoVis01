package org.vcell.messaging;

import java.io.*;


public class VCellMessagingLocal implements VCellMessaging {
    private final BufferedWriter stdout_writer;
    private final BufferedWriter stderr_writer;
    
    public VCellMessagingLocal() {
        this(System.out, System.err);
    } 
    
    // constructor - especially for testing
    public VCellMessagingLocal(PrintStream stdout, PrintStream stderr) {
        stdout_writer = new BufferedWriter(new PrintWriter(stdout));
        stderr_writer = new BufferedWriter(new PrintWriter(stderr));
    }
    @Override
    public void sendWorkerEvent(WorkerEvent event) {
        try {
            switch (event.status()) {
                case JOB_DATA:
                    stdout_writer.write("[[[data:"+event.timepoint()+"]]]\n");
                    break;
                case JOB_PROGRESS:
                    stdout_writer.write("[[[progress:"+(event.progress() * 100.0)+"]]]\n");
                    break;
                case JOB_STARTING:
                    stdout_writer.write(event.eventMessage() + "\n");
                    break;
                case JOB_COMPLETED:
                    stderr_writer.write("Simulation Complete in Main() ...\n"); // out of band message
                    break;
                case JOB_FAILURE:
                    stderr_writer.write(event.eventMessage()+"\n"); // out of band message
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            stdout_writer.flush();
            stderr_writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("error closing output stream for log", e);
        }
    }
}
