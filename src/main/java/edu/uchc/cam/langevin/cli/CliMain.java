package edu.uchc.cam.langevin.cli;

import picocli.CommandLine;

@CommandLine.Command(
        name = "langevin",
        mixinStandardHelpOptions = true,
        version = "langevin 1.0",
        description = "Langevin solver and utilities.",
        subcommands = {
                RunCommand.class
        })
public class CliMain {
    public static void main(String[] args) {
        int exitCode = -1;
        try{
            CommandLine commandLine = new CommandLine(new CliMain());
            exitCode = commandLine.execute(args);
        } catch (Throwable t){
            t.printStackTrace();
        } finally {
            System.exit(exitCode);
        }
    }
}
