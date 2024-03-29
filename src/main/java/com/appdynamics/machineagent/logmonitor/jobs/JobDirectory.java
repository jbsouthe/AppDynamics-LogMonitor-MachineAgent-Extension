package com.appdynamics.machineagent.logmonitor.jobs;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import com.appdynamics.machineagent.logmonitor.model.JobModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
scan the directory containing job files
 */
public class JobDirectory {
    private static final Logger logger = LogManager.getFormatterLogger();

    private File directoryFile;
    private Map<String,JobFile> fileMap;
    private JobFile greedyJobFile;
    Yaml yaml;
    private FilenameFilter jobFileNameFilter;
    private long lastScanTime;
    private Configuration configuration;

    public JobDirectory( String dirName, Configuration configuration) throws JobFileException {
        this.configuration=configuration;
        directoryFile = new File(dirName);
        this.yaml = new Yaml(new Constructor(JobModel.class, new LoaderOptions()));
        if( !directoryFile.exists() || !directoryFile.isDirectory() ) throw new JobFileException("Directory does not exist: "+ dirName);
        try {
            this.greedyJobFile = new JobFile(null, yaml.load(getClass().getResourceAsStream("/GreedyCatchAll.job")), configuration);
        } catch (Exception exception) {
            logger.warn("Could not load greedy job file from jar: "+exception);
            throw new JobFileException("Could not load greedy job file from jar: "+exception);
        }
        this.jobFileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".job");
            }
        };
        scanDirectory(true);
    }

    public void scanDirectory() { scanDirectory(false); }
    public void scanDirectory( boolean init ) {
        if( init ) {
            this.fileMap = new HashMap<>();
        }
        for( File file : this.directoryFile.listFiles(this.jobFileNameFilter)) {
            logger.debug("Parsing %s Job File",file.getName());
            if( !fileMap.containsKey(file.getName())
                    || (fileMap.containsKey(file.getName()) && file.lastModified() > this.lastScanTime )
            ) {
                try {
                    JobModel jobModel = yaml.load(new FileInputStream(file));
                    fileMap.put(file.getName(), new JobFile(file, jobModel, this.configuration));
                } catch (FileNotFoundException fileNotFoundException) {
                    logger.warn(String.format("File was just here?! %s exception %s",file.getAbsolutePath(), fileNotFoundException));
                }
            }
        }
    }

    public JobFile findBestMatch( File logFile ) throws JobFileException {
        if(configuration.excludeLogFile(logFile.getAbsolutePath())) {
            logger.debug(String.format("Excluding this logfile because it is matched to an exclusion regex: '%s'",logFile.getAbsolutePath()));
            return null;
        }
        String sampleLines = readSampleLines(logFile);
        JobFile bestMatchSoFar = null;
        double bestTestResult = 0.0d;
        for( JobFile jobFile : this.fileMap.values() ) {
            double testResult = jobFile.testGrok(sampleLines);
            logger.debug(String.format("Test %s result %f",jobFile.getJobFileHandle().getName(), testResult));
            if( testResult > bestTestResult ) {
                bestTestResult=testResult;
                bestMatchSoFar=jobFile;
            }
        }
        if( bestMatchSoFar == null ) {
            bestMatchSoFar = greedyJobFile;
            logger.debug("Best match so far is pretty bad, so setting to the built in greedy job file");
        }
        logger.info(String.format("Best Match for %s is %s", logFile.getAbsolutePath(), bestMatchSoFar.getJobFileHandle().getName()));
        return bestMatchSoFar;
    }

    private String readSampleLines( File logFile ) throws JobFileException {
        StringBuilder sample = new StringBuilder();
        try (Scanner scanner = new Scanner(logFile)) {
            int count = 0;
            while (scanner.hasNextLine()) {
                sample.append(scanner.nextLine()).append(System.lineSeparator());
                count++;
                if (count == 10) break;
            }
            logger.debug("read %d lines from %s log file",count, logFile.getName());
        } catch (FileNotFoundException fileNotFoundException) {
            throw new JobFileException("Could not load sample log file lines, File no longer exists: " + logFile.getAbsolutePath());
        }
        return sample.toString();
    }
}
