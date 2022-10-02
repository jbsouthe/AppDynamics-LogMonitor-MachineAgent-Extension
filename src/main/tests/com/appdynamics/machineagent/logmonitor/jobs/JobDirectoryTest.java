package com.appdynamics.machineagent.logmonitor.jobs;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;


import java.io.File;


public class JobDirectoryTest extends TestCase {
    private JobDirectory jobDirectory;
    private JobFile jobFile;
    private String testGrokDirName = "./resources/jobs";

    public JobDirectoryTest() {}

    public void setUp() throws Exception {
        Configurator.setAllLevels("", Level.ALL);
    }

    public void testJobDirectoryInit() throws Exception, JobFileException {
        jobDirectory = new JobDirectory(testGrokDirName, new Configuration(null, null));

        File logSample = new File(testGrokDirName+"/sample-apache-httpserver-access.log");
        jobFile = jobDirectory.findBestMatch(logSample);
        assert jobFile.getJobFileHandle().getName().equals("sample-apache-httpserver-access-log.job");

        JobFile copyJobFile = jobFile.copy(new File("./target"), logSample);
    }
}