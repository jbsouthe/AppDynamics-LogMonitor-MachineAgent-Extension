package com.appdynamics.machineagent.logmonitor;

import junit.framework.TestCase;

import java.io.File;

public class UtilityTest extends TestCase {

    public void testListFiles() {
        File[] files = Utility.listFiles("test/logDir/logfile.log");
        assert files != null;
        assert files.length == 1;
        assert "test/logDir/logfile.log".equals(files[0].toString());
        System.out.println("1: logfile: "+ files[0].toString());

        files = Utility.listFiles("test/logDir/logfil?.log");
        assert files != null;
        assert files.length == 1;
        assert "test/logDir/logfile.log".equals(files[0].toString());
        System.out.println("2: logfile: "+ files[0].toString());

        files = Utility.listFiles("test/logDir/log*.log");
        assert files != null;
        assert files.length == 1;
        assert "test/logDir/logfile.log".equals(files[0].toString());
        System.out.println("3: logfile: "+ files[0].toString());

        files = Utility.listFiles("test/logDir/first/*/*.log");
        assert files != null;
        assert files.length == 3;
        //assert "test/logDir/logfile.log".equals(files[0].toString());
        System.out.print("4: logfiles: ");
        for( File file : files ) System.out.print(file.toString()+", ");
        System.out.println();
    }

}