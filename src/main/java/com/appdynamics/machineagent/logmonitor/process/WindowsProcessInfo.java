package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class WindowsProcessInfo implements ProcessInfo {
    private final Logger logger;
    private final String psCommandLine;
    private final String lsofCommandLine;
    private Configuration configuration;
    private String[] psOutputHeaderColumns;

    public WindowsProcessInfo( Configuration configuration ) {
        this.logger = configuration.getLogger();
        this.configuration=configuration;
        this.psCommandLine=configuration.getPsCommandLine();
        this.lsofCommandLine=configuration.getLsofCommandLine();
    }

    //use process explorer handle.exe to list open log files of a process, https://learn.microsoft.com/en-us/sysinternals/downloads/handle
    //  usage: handle.exe [[-a] [-u] | [-c <handle> [-l] [-y]] | [-s]] [-p <processname>|<pid>> [name]
    //use process explorer pslist.exe to list processes of a given name, https://learn.microsoft.com/en-us/sysinternals/downloads/pslist'
    //  usage: pslist.exe name

    @Override
    public List<ProcessDetails> getProcessList(String name) {
        List<ProcessDetails> processDetails = new ArrayList<>();
        RunCommand psCommand = new RunCommand("cmd", "/C", String.format("%s \"%s\"",this.psCommandLine, name));
        if( psCommand.isSuccess() ) {
            logger.debug("pslist.exe success output: "+ psCommand.getStdOut());
            for( String line : psCommand.getStdOut().split("\\n") ) {
                if( psOutputHeaderColumns == null ) {
                    String[] splitLine = line.trim().split("\\s+");
                    if( splitLine.length > 0 && "name".equals(splitLine[0].toLowerCase()) ) {
                        psOutputHeaderColumns=splitLine;
                    }
                    continue;
                } else {
                    ProcessDetails processDetail = new ProcessDetails(name, line.trim(), psOutputHeaderColumns);
                    processDetail.setOpenFileList(getOpenFiles(processDetail.getPid()));
                    processDetails.add(processDetail);  //say this five times fast
                }
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
        RunCommand psCommand = new RunCommand("cmd", "/C", String.format(this.lsofCommandLine, pid));
        if( psCommand.isSuccess() ) {
            logger.debug("handle.exe success output: " + psCommand.getStdOut());
            for (String line : psCommand.getStdOut().split("\\n")) {
                if( line.toLowerCase().endsWith(".log") ) {
                    logger.debug("processing possible log file: "+ line);
                    try {
                        OpenFile openFile = new OpenFile(line.trim().split("\\s+"));
                        logger.debug(openFile);
                        openFileList.add(openFile);
                    }catch (FileNotFoundException fileNotFoundException) {
                        logger.warn(String.format("Skipping log file %s, got a file not found exception on open: %s",line,fileNotFoundException));
                    }
                }
            }
        }
        return openFileList;
    }
}
