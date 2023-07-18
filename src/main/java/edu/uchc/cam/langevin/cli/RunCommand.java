package edu.uchc.cam.langevin.cli;

import edu.uchc.cam.langevin.langevinnovis01.Global;
import edu.uchc.cam.langevin.langevinnovis01.MySystem;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "simulate", description = "Run a Langevin simulation.")
public class RunCommand implements Callable<Integer> {
    @CommandLine.Parameters(description = "Langevin model file", index = "0", type = File.class)
    private File modelFile = null;

    @CommandLine.Parameters(description = "run counter", index = "1", type = Integer.class)
    private Integer runCounter = null;

    @CommandLine.Option(names = {"--output-log"}, required = false, type = File.class, description = "output log file")
    private File logFile = null;

    public RunCommand() {
    }

    public Integer call() {
        Global g;
        MySystem sys;
        if (logFile == null) {
            g = new Global(modelFile);
            sys = new MySystem(g, runCounter, false);
        } else {
            g = new Global(modelFile, logFile);
            sys = new MySystem(g, runCounter, true);
        }

        sys.runSystem();
        // g.writeData("AlloInputData.txt");
        return 0;
    }
}
