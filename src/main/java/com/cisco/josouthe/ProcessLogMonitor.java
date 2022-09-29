package com.cisco.josouthe;

import com.cisco.josouthe.config.Configuration;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ProcessLogMonitor extends AManagedMonitor {
    private static final Logger logger = LogManager.getFormatterLogger();
    Configuration configuration = null;

    public ProcessLogMonitor() {

    }

    @Override
    public TaskOutput execute(Map<String, String> map, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if( configuration == null ) {
            this.configuration = new Configuration(map.get("config-file"), taskExecutionContext.getLogger());
        } else {
            this.configuration.init(map.get("config-file"));
        }
        StringBuilder taskOutputStringBuilder = new StringBuilder("Process Log Monitor");




        return new TaskOutput(taskOutputStringBuilder.toString());
    }
}
