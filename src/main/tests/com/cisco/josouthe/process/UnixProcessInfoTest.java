package com.cisco.josouthe.process;

import com.cisco.josouthe.Utility;
import com.cisco.josouthe.config.Configuration;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.List;

public class UnixProcessInfoTest extends TestCase {

    Configuration configuration;

    public UnixProcessInfoTest() {

    }

    public void setUp() throws Exception {
        Configurator.setAllLevels("", Level.ALL);
        this.configuration = new Configuration(null, null);
    }

    public void testUnixProcessListing() {
        RunCommand psHeader = new RunCommand("/bin/bash", "-c", "/bin/ps -ef |head -1");
        String[] psOutputHeaderColumns = psHeader.getStdOut().trim().split("\\s+");
        System.out.println(String.format("psOutputHeader size: %d elements: '%s'",psOutputHeaderColumns.length, Utility.toString(psOutputHeaderColumns)));

        UnixProcessInfo unixProcessInfo = new UnixProcessInfo( configuration );
        List<ProcessDetails> processDetails = unixProcessInfo.getProcessList("java");
        for( ProcessDetails pd : processDetails ) {
            System.out.println("Process: "+ pd);
        }
    }
}