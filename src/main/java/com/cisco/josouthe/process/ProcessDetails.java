package com.cisco.josouthe.process;

import com.cisco.josouthe.Utility;

import java.util.ArrayList;
import java.util.List;

public class ProcessDetails {
    public String name;
    public Long pid;
    public String command;
    public List<OpenFile> openFileList = new ArrayList<>();

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

    public String toString() {
        return String.format("Name: %s PID: %d Command: '%s' Files: %d", name, pid, command, openFileList.size());
    }
}
