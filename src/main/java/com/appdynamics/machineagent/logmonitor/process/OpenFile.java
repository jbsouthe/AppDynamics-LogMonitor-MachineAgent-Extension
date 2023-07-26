package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.Utility;
import com.appdynamics.machineagent.logmonitor.jobs.JobFile;

import java.io.File;
import java.io.FileNotFoundException;

public class OpenFile {
    public String fileName,type;
    private long pid=0;
    private File file;
    private JobFile jobFile;

    public OpenFile(String fileName, String type, Long pid) {
        this.fileName = fileName;
        this.type = type;
        this.pid = pid;
        this.file = new File(fileName);
    }

    public OpenFile(String line, String[] lsofOutputHeaderColumns) throws FileNotFoundException {
        String[] lineArray = line.trim().split("\\s+");
        for( int i=0; i<lsofOutputHeaderColumns.length; i++) {
            if( lsofOutputHeaderColumns[i].toLowerCase().equals("type") ) this.type=lineArray[i];
            if( lsofOutputHeaderColumns[i].toLowerCase().equals("name") ) this.fileName= Utility.removeFirstElements(i,lineArray);
            if( lsofOutputHeaderColumns[i].toLowerCase().equals("pid") ) this.pid= Long.parseLong(lineArray[i]);
        }
        this.file = new File(fileName);
    }

    public OpenFile( long pid, String[] lineArray ) throws FileNotFoundException { //windows handle.exe output log file lines
        this.pid=pid;
        this.type=lineArray[1];
        this.fileName=Utility.removeFirstElements(3, lineArray);
        this.file = new File(fileName);
    }

    public boolean isReadable() { return file.canRead(); }
    public boolean isAccessible() { return file.exists(); }
    public long getPid() { return pid; }
    public File getFile() { return file; }
    public JobFile getJobFile() { return jobFile; }
    public void setJobFile(JobFile jobFile) { this.jobFile = jobFile; }

    public String toString() {
        return String.format("File: '%s' PID: %d Type: %s isReadable: %s isAccessible: %s", fileName, pid, type, isReadable(), isAccessible() );
    }

}
