package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.Utility;

import java.util.ArrayList;
import java.util.List;

public class ProcessDetails {
    private String name;
    private Long pid;
    private String command;
    private List<OpenFile> openFileList = new ArrayList<>();

    public ProcessDetails(String name, String psLine, String[] psOutputHeaderColumns) {
        this.name=name;
        String[] psLineArray = psLine.trim().split("\\s+");
        for( int i=0; i< psOutputHeaderColumns.length; i++) {
            if( psOutputHeaderColumns[i].toLowerCase().equals("pid") ) {
                this.pid = Long.parseLong(psLineArray[i]);
            }
            if( psOutputHeaderColumns[i].toLowerCase().equals("cmd") ) {
                this.command = Utility.removeFirstElements(i,psLineArray);
            }
        }
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
    }

    public String toString() {
        return String.format("Name: %s PID: %d Command: '%s' Files: %d", name, pid, command, openFileList.size());
    }
}
