package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RunCommand {
    private static final Logger logger = LogManager.getFormatterLogger();
    private int returnCode=0;
    private StringBuilder out;
    private Process process;
    private boolean isProcessRunning=false;
    private String commandAndArguments;

    public RunCommand( String... args ) {
        this.commandAndArguments = printArgs(args,0);
        out = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            logger.debug("Process started: "+ process.toString()+" command: "+ Utility.toString(args));
            isProcessRunning=true;
            //this.returnCode = process.waitFor();

            String line;
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while( (line=stdout.readLine()) != null ) {
                //logger.debug("line: "+ line.trim());
                out.append(line.trim());
                if( !line.endsWith("\\n")) out.append("\n");
            }
            logger.trace("Process finished writing standard output: "+ out.toString());
            stdout.close();
            this.returnCode = process.waitFor();
            isProcessRunning=false;
            logger.debug("Process ended, return code "+ this.returnCode);
        } catch (Exception exception) {
            logger.warn(String.format("Error running command '%s' with arguments '%s', Exception: %s",args[0], printArgs(args,1), exception));
        }
    }

    private String printArgs( String[] args, int startPosition) {
        StringBuilder sb = new StringBuilder();
        for( int i=startPosition; i< args.length; i++)
            sb.append(args[i]).append(" ");
        return sb.toString();
    }

    public void kill() {
        if( isProcessRunning ) {
            this.process.destroy();
            this.isProcessRunning=false;
        }
    }
    public boolean isSuccess() { return this.returnCode == 0; }
    public boolean isRunning() { return this.isProcessRunning; }
    public int getReturnCode() { return returnCode; }
    public String getStdOut() { return out.toString(); }

    public String toString() {
        return String.format("RunCommand: '%s' Running: %s Return Code: %d",this.commandAndArguments, isRunning(), (isRunning() ? "N/A" :getReturnCode()));
    }
}
