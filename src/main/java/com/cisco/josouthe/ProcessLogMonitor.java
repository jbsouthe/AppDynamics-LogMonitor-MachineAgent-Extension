package com.cisco.josouthe;

import com.cisco.josouthe.config.Configuration;
import com.cisco.josouthe.jobs.JobDirectory;
import com.cisco.josouthe.jobs.JobFile;
import com.cisco.josouthe.jobs.JobFileException;
import com.cisco.josouthe.process.OpenFile;
import com.cisco.josouthe.process.ProcessDetails;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessLogMonitor extends AManagedMonitor {
    private static final Logger logger = LogManager.getFormatterLogger();
    Configuration configuration = null;
    private Map<String, JobFile> openFilesMap = new HashMap<>();
    JobDirectory templateJobManager;

    public ProcessLogMonitor() {

    }

    @Override
    public TaskOutput execute(Map<String, String> map, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if( configuration == null ) {
            this.configuration = new Configuration(taskExecutionContext.getTaskDir()+"/"+map.get("config-file"), taskExecutionContext);
        } else {
            this.configuration.init(taskExecutionContext.getTaskDir()+"/"+map.get("config-file"));
        }
        StringBuilder taskOutputStringBuilder = new StringBuilder("Process Log Monitor");

        try {
            templateJobManager = configuration.getTemplateJobManager();
        } catch (JobFileException jobFileException) {
            return new TaskOutput(String.format("Error building the Job File Directory Manager: %s",jobFileException));
        }

        for( String filePattern : configuration.getLogfileList() ) {
            for(File logFile : Utility.listFiles(filePattern) ) {
                taskOutputStringBuilder.append( initializeLogFileMonitoring(logFile) );
            }
        }
        for( String processPattern : configuration.getProcessList() ) {
            for(ProcessDetails process : configuration.getProcessInfo().getProcessList(processPattern) ) {
                for( OpenFile openFile : configuration.getProcessInfo().getOpenFiles(process.getPid()) ) {
                    File logFile = openFile.getFile();
                    taskOutputStringBuilder.append( initializeLogFileMonitoring(logFile) );
                }
            }
        }



        return new TaskOutput(taskOutputStringBuilder.toString());
    }

    private String initializeLogFileMonitoring(File logFile) {
        StringBuilder taskOutputStringBuilder = new StringBuilder();
        if(openFilesMap.containsKey(logFile.getName())) return "";
        try {
            JobFile templateJobFile = templateJobManager.findBestMatch(logFile);
            openFilesMap.put(logFile.getName(), templateJobFile.copy(configuration.getAnalyticsJobDir() , logFile.getParent(), logFile.getName()));
        } catch (Exception e) {
            logger.warn(String.format("Job File Generation Exception for file %s, Exception: %s", logFile.getAbsolutePath(), e),e);
            taskOutputStringBuilder.append(String.format("Job File Generation Exception for file %s, Exception: %s\n", logFile.getAbsolutePath(), e));
        }
        return taskOutputStringBuilder.toString();
    }
}
