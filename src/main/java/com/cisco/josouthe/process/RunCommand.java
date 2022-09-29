package com.cisco.josouthe.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunCommand {
    private static final Logger logger = LogManager.getFormatterLogger();
    private int returnCode;
    private StringBuilder out, err;
    private Process process;
    private boolean isProcessRunning=false;

    public RunCommand( String... args ) {
        out = new StringBuilder();
        err = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(args);
            logger.debug("Process started: "+ process.toString());
            isProcessRunning=true;
            String line;
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while( (line=stderr.readLine()) != null ) err.append(line);
            logger.debug("Process finished writing error output");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while( (line=stdout.readLine()) != null ) out.append(line).append("\n");
            logger.debug("Process finished writing standard output");
            this.returnCode = process.waitFor();
            logger.debug("Process ended, return code "+ this.returnCode);
            isProcessRunning=false;
            stderr.close();
            stdout.close();
        } catch (Exception exception) {
            if( err.length() > 0 ) err.append("\n");
            err.append(String.format("Error running command '%s' with arguments '%s', Exception: %s",args[0], printArgs(args,1), exception));
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
    public String getErrOut() { return err.toString(); }
}
