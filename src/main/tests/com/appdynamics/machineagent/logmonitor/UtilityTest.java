package com.appdynamics.machineagent.logmonitor;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

public class UtilityTest extends TestCase {
    public void setUp() throws Exception {
        Configurator.setAllLevels("", Level.ALL);
    }
    public void testListFiles() {
        File[] files = Utility.listFiles("/var/log/system.log");
        assert files != null;
        assert files.length == 1;
        assert "/var/log/system.log".equals(files[0].toString());
        System.out.println("0: logfile: "+ files[0].toString());

        files = Utility.listFiles("test/logDir/logfile.log");
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