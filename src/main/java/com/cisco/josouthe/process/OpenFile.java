package com.cisco.josouthe.process;

import com.cisco.josouthe.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class OpenFile {
    public String fileName,type;
    private long pid=0;
    private File file;

    public OpenFile(String fileName) {
        this.fileName=fileName;
        this.type="direct";
        this.file= new File(fileName);
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

    public boolean isReadable() { return file.canRead(); }
    public boolean isAccessible() { return file.exists(); }
    public long getPid() { return pid; }
    public File getFile() { return file; }

    public String toString() {
        return String.format("File: '%s' PID: %d Type: %s isReadable: %s isAccessible: %s", fileName, pid, type, isReadable(), isAccessible() );
    }

}
