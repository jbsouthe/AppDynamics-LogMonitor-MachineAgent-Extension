package com.cisco.josouthe.process;

import com.cisco.josouthe.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class OpenFile {
    public String fileName,type;
    private File file;
    private RandomAccessFile randomAccessFile;
    public Long lastReadByte;

    public OpenFile(String line, String[] lsofOutputHeaderColumns) throws FileNotFoundException {
        String[] lineArray = line.trim().split("\\s+");
        for( int i=0; i<lsofOutputHeaderColumns.length; i++) {
            if( lsofOutputHeaderColumns[i].toLowerCase().equals("type") ) this.type=lineArray[i];
            if( lsofOutputHeaderColumns[i].toLowerCase().equals("name") ) this.fileName= Utility.removeFirstElements(i,lineArray);
        }
        lastReadByte=-1l;
        this.file = new File(fileName);
        this.randomAccessFile = new RandomAccessFile(file, "r");
    }

    public boolean isReadable() { return file.canRead(); }
    public boolean isAccessible() { return file.exists(); }

    public String toString() {
        return String.format("File: '%s' Type: %s lastReadByte: %d isReadable: %s isAccessible: %s", fileName, type, lastReadByte, isReadable(), isAccessible() );
    }

}
