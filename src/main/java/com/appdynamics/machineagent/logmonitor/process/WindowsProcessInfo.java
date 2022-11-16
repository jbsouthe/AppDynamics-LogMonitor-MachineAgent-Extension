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
        this.psCommandLine="wmic.exe process get processid,commandline /Format:csv"; //configuration.getPsCommandLine();
        this.lsofCommandLine="handle.exe -p %d"; //configuration.getLsofCommandLine();
        fixEULAProblem();
    }

    //use process explorer handle.exe to list open log files of a process, https://learn.microsoft.com/en-us/sysinternals/downloads/handle
    //  usage: handle.exe [[-a] [-u] | [-c <handle> [-l] [-y]] | [-s]] [-p <processname>|<pid>> [name]
    //use process explorer pslist.exe to list processes of a given name, https://learn.microsoft.com/en-us/sysinternals/downloads/pslist'
    //  usage: pslist.exe name

    @Override
    public List<ProcessDetails> getProcessList(String name) {
        List<ProcessDetails> processDetails = new ArrayList<>();
        //RunCommand psCommand = new RunCommand("cmd", "/C", String.format("%s \"%s\"",this.psCommandLine, name));
        RunCommand psCommand = new RunCommand(String.format("cmd /C %s",this.psCommandLine).split("\\s"));
        if( psCommand.isSuccess() ) {
            logger.debug("pslist.exe success output: "+ psCommand.getStdOut());
            for( String line : psCommand.getStdOut().split("\\n+") ) {
                if( psOutputHeaderColumns == null ) {
                    String[] splitLine = line.trim().split(",");
                    if( splitLine.length > 0 && "node".equals(splitLine[0].toLowerCase()) ) {
                        psOutputHeaderColumns=splitLine;
                    }
                    continue;
                } else if( line.trim().length() > 0 && line.toLowerCase().contains(name.toLowerCase()) ) {
                    ProcessDetails processDetail = new ProcessDetails(name, line.trim().split(","), psOutputHeaderColumns);
                    processDetail.setOpenFileList(getOpenFiles(processDetail.getPid()));
                    processDetails.add(processDetail);  //say this five times fast
                }
            }
        } else {
            logger.warn(String.format("Error running %s, error: %s", psCommand.toString(), psCommand.getStdOut()));
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
                        OpenFile openFile = new OpenFile(pid, line.trim().split("\\s+"));
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

    private void fixEULAProblem() {
        RunCommand regEdit = new RunCommand( "reg ADD HKCU\\Software\\Sysinternals\\PSexec /v EulaAccepted /t REG_DWORD /d 1 /f".split("\\s+"));
        if(regEdit.isSuccess()) {
            logger.debug("Fixed windows registry issue where pslist.exe asks for EULA acceptance on first run");
        } else {
            logger.warn("Tried to fix EULA acceptance for pslist.exe, but was not successful, this will not run, please report the issue and and environment specifications to the maintainer");
        }
        regEdit = new RunCommand( "reg ADD HKCU\\Software\\Sysinternals\\Handle /v EulaAccepted /t REG_DWORD /d 1 /f".split("\\s+"));
        if(regEdit.isSuccess()) {
            logger.debug("Fixed windows registry issue where handle.exe asks for EULA acceptance on first run");
        } else {
            logger.warn("Tried to fix EULA acceptance for handle.exe, but was not successful, this will not run, please report the issue and and environment specifications to the maintainer");
        }
    }
}
