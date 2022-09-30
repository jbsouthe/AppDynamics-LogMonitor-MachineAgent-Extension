package com.cisco.josouthe.jobs;

import com.cisco.josouthe.config.Configuration;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class JobDirectoryTest extends TestCase {
    public JobDirectoryTest() {}

    @Before
    public void setUp() throws Exception {
        Configurator.setAllLevels("", Level.ALL);
    }

    @Test
    public void testJobDirectoryInit() throws Exception, JobFileException {
        JobDirectory jobDirectory = new JobDirectory("./test-jobdir", new Configuration(null, null));

        JobFile jobFile = jobDirectory.findBestMatch(new File("./test-jobdir/sample-apache-access-log.log"));
        assert jobFile.getJobFileHandle().getName().equals("sample-apache-httpserver-access-log.job");
    }
}