package edu.uchc.cam.langevin.cli;

import com.google.gson.Gson;
import edu.uchc.cam.langevin.langevinnovis01.Global;
import edu.uchc.cam.langevin.langevinnovis01.MySystem;
import org.vcell.messaging.*;
import picocli.CommandLine;

import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "simulate", description = "Run a Langevin simulation.")
public class RunCommand implements Callable<Integer> {
    @CommandLine.Parameters(description = "Langevin model file", index = "0", type = File.class)
    private File modelFile = null;

    @CommandLine.Parameters(description = "run counter", index = "1", type = Integer.class)
    private Integer runCounter = null;

    @CommandLine.Option(names = {"--output-log"}, required = false, type = File.class, description = "output log file")
    private File logFile = null;

    private final String example_config = """
                {
                  "broker_host": "localhost",
                  "broker_port": 8165,
                  "broker_username": "msg_user",
                  "broker_password": "msg_pswd",
                  "compute_hostname": "localhost",
                  "vc_username": "vcell_user",
                  "simKey": "12334483837",
                  "taskID": 0,
                  "jobIndex": 0
                }
                """;
    @CommandLine.Option(names = {"--vc-send-status-config"}, required = false, type = File.class,
                        description = "json status message config file as:\n\n" + example_config)
    private File sendStatusConfig = null;

    @CommandLine.Option(names = {"--vc-print-status"}, required = false, type = Boolean.class, description = "print vcell status to stdout and stderr")
    private boolean printStatus = false;

    public RunCommand() {
    }

    public Integer call() {
        Global g;
        MySystem sys;
        VCellMessaging vcellMessaging = new VCellMessagingNoop();
        if (sendStatusConfig != null) {
            try {
                // the messagingConfigFile is a json structure corresponding to a MessagingConfig object.
                // create a MessagingConfig object by reading the messagingConfigFile
                Gson gson = new Gson();
                MessagingConfig messagingConfig = gson.fromJson(new FileReader(sendStatusConfig), MessagingConfig.class);
                vcellMessaging = new VCellMessagingRest(messagingConfig);
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        } else if (printStatus) {
            vcellMessaging = new VCellMessagingLocal();
        } else {
            vcellMessaging = new VCellMessagingNoop();
        }
        if (logFile == null) {
            g = new Global(modelFile);
            sys = new MySystem(g, runCounter, false, vcellMessaging);
        } else {
            g = new Global(modelFile, logFile);
            sys = new MySystem(g, runCounter, true, vcellMessaging);
        }

        sys.runSystem();
        // g.writeData("AlloInputData.txt");
        return 0;
    }
}
