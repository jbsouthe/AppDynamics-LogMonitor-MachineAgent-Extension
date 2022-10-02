package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WindowsProcessInfo implements ProcessInfo {
    private final Logger logger;
    private Configuration configuration;
    public WindowsProcessInfo( Configuration configuration ) {
        this.logger = configuration.getLogger();
        this.configuration=configuration;
    }

    //use process explorer handle.exe to list open log files of a process, https://learn.microsoft.com/en-us/sysinternals/downloads/handle
    //  usage: handle.exe [[-a] [-u] | [-c <handle> [-l] [-y]] | [-s]] [-p <processname>|<pid>> [name]
    //use process explorer pslist.exe to list processes of a given name, https://learn.microsoft.com/en-us/sysinternals/downloads/pslist'
    //  usage: pslist.exe name

    @Override
    public List<ProcessDetails> getProcessList(String name) {
        return null;
    }

    @Override
    public List<OpenFile> getOpenFiles( long pid ) {
        List<OpenFile> openFileList = new ArrayList<>();

        return openFileList;
    }
}
