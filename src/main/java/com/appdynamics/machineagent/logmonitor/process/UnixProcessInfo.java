package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class UnixProcessInfo implements ProcessInfo {
    //ps for process listings
    //lsof for open files
    private String[] psOutputHeaderColumns;
    private String psCommandLine = "/bin/ps -ef";
    private String lsofCommandLine = "/usr/sbin/lsof -p %d";

    private final Logger logger;
    private Configuration configuration;
    public UnixProcessInfo( Configuration configuration ) {
        this.logger = configuration.getLogger();
        this.configuration=configuration;
        RunCommand psHeader = new RunCommand("/bin/bash", "-c", String.format("%s |head -1", this.psCommandLine));
        this.psOutputHeaderColumns = psHeader.getStdOut().trim().split("\\s+");
        //logger.debug(String.format("psOutputHeader size: %d elements: '%s'",this.psOutputHeaderColumns.length, Utility.toString(this.psOutputHeaderColumns)));
    }

    @Override
    public List<ProcessDetails> getProcessList( String name ) {
        List<ProcessDetails> processDetails = new ArrayList<>();
        RunCommand psCommand = new RunCommand("/bin/bash", "-c", String.format("%s | grep -v grep| grep \"%s\"",this.psCommandLine, name));
        if( psCommand.isSuccess() ) {
            logger.debug("ps success output: "+ psCommand.getStdOut());
            for( String line : psCommand.getStdOut().split("\\n") ) {
                ProcessDetails processDetail = new ProcessDetails(name, line.trim(), psOutputHeaderColumns);
                processDetail.setOpenFileList( getOpenFiles( processDetail.getPid() ) );
                processDetails.add(processDetail);  //say this five times fast
            }
        } else {
            logger.warn("Error running ps command, error: "+ psCommand.getErrOut());
            return null;
        }

        return processDetails;
    }

    @Override
    public List<OpenFile> getOpenFiles( long pid ) {
        List<OpenFile> openFileList = new ArrayList<>();
        RunCommand lsOfCommand = new RunCommand("/bin/bash", "-c", String.format(lsofCommandLine,pid));
        String[] lsofOutputHeaderColumns = null;
        for( String line : lsOfCommand.getStdOut().split("\\n") ) {
            if( lsofOutputHeaderColumns == null ) {
                lsofOutputHeaderColumns = line.trim().split("\\s+");
                continue;
            }
            if( line.toLowerCase().endsWith(".log") ) {
                logger.debug("processing possible log file: "+ line);
                try {
                    OpenFile openFile = new OpenFile(line.trim(), lsofOutputHeaderColumns);
                    logger.debug(openFile);
                    openFileList.add(openFile);
                }catch (FileNotFoundException fileNotFoundException) {
                    logger.warn(String.format("Skipping log file %s, got a file not found exception on open: %s",line,fileNotFoundException));
                }
            }
        }
        return openFileList;
    }
}
