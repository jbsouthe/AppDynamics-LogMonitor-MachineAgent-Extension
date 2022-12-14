package com.appdynamics.machineagent.logmonitor;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import com.appdynamics.machineagent.logmonitor.process.OpenFile;
import com.appdynamics.machineagent.logmonitor.process.ProcessDetails;
import com.appdynamics.machineagent.logmonitor.jobs.JobDirectory;
import com.appdynamics.machineagent.logmonitor.jobs.JobFile;
import com.appdynamics.machineagent.logmonitor.jobs.JobFileException;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
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
            File[] fileList = Utility.listFiles(filePattern);
            if( fileList != null )
                for(File logFile : fileList ) {
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
            if( templateJobFile != null )
                openFilesMap.put(logFile.getName(), templateJobFile.copy(configuration.getAnalyticsJobDir() , logFile));
        } catch (Exception e) {
            logger.warn(String.format("Job File Generation Exception for file %s, Exception: %s", logFile.getAbsolutePath(), e),e);
            taskOutputStringBuilder.append(String.format("Job File Generation Exception for file %s, Exception: %s\n", logFile.getAbsolutePath(), e));
        }
        return taskOutputStringBuilder.toString();
    }
}
