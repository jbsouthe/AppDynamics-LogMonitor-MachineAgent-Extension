package com.cisco.josouthe.jobs;

import com.cisco.josouthe.config.Configuration;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;


import java.io.File;


public class JobDirectoryTest extends TestCase {
    private JobDirectory jobDirectory;
    private JobFile jobFile;

    public JobDirectoryTest() {}

    public void setUp() throws Exception {
        Configurator.setAllLevels("", Level.ALL);
    }

    public void testJobDirectoryInit() throws Exception, JobFileException {
        jobDirectory = new JobDirectory("./test-jobdir", new Configuration(null, null));

        jobFile = jobDirectory.findBestMatch(new File("./test-jobdir/sample-apache-access-log.log"));
        assert jobFile.getJobFileHandle().getName().equals("sample-apache-httpserver-access-log.job");

        JobFile copyJobFile = jobFile.copy(new File("./target"), "someDir", "nameGlobThing");
    }
}