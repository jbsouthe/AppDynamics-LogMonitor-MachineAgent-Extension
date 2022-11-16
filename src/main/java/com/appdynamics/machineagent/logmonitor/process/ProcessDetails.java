package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProcessDetails {
    private static final Logger logger = LogManager.getFormatterLogger();
    private String name;
    private Long pid;
    private String command;
    private List<OpenFile> openFileList = new ArrayList<>();

    public ProcessDetails(String name, String[] psLineArray, String[] psOutputHeaderColumns) {
        setName(name);
        for( int i=0; i< psOutputHeaderColumns.length; i++) {
            if( psOutputHeaderColumns[i].toLowerCase().equals("pid") || psOutputHeaderColumns[i].toLowerCase().equals("processid")) {
                setPid(Long.parseLong(psLineArray[i]));
            }
            if( psOutputHeaderColumns[i].toLowerCase().equals("cmd") || psOutputHeaderColumns[i].toLowerCase().equals("commandline") ) {
                setCommand(Utility.removeFirstElements(i,psLineArray));
            }
        }
        logger.debug(String.format("Initialized Process Detail: %s", this));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<OpenFile> getOpenFileList() {
        return openFileList;
    }

    public void setOpenFileList(List<OpenFile> openFileList) {
        this.openFileList = openFileList;
        logger.debug("ProcessDetail.setOpenFileList() on %s",this);
    }

    public String toString() {
        return String.format("Name: %s PID: %d Command: '%s' Files: %d", name, pid, command, openFileList.size());
    }
}
