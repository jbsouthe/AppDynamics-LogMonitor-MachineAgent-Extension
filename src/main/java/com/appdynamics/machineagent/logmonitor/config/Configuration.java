package com.appdynamics.machineagent.logmonitor.config;

import com.appdynamics.machineagent.logmonitor.model.ConfigurationModel;
import com.appdynamics.machineagent.logmonitor.process.ProcessInfo;
import com.appdynamics.machineagent.logmonitor.process.UnixProcessInfo;
import com.appdynamics.machineagent.logmonitor.process.WindowsProcessInfo;
import com.appdynamics.machineagent.logmonitor.jobs.JobDirectory;
import com.appdynamics.machineagent.logmonitor.jobs.JobFileException;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
need to specify processes and point to a directory to examine all logs files in, say for example, /var/log/*.log
 */
public class Configuration {
    private Logger logger = null;
    private File configFile;
    private String configFileName;
    private long configFileLastUpdate;
    private boolean isRunningOnWindows=false;
    private ProcessInfo processInfo;
    private File grokDir;
    private File jobDir;
    private File runDir;
    private File analyticsJobDir;
    private String psCommandLine = "ps -ef";
    private String lsofCommandLine = "lsof -p %d";
    private List<String> logfileList;
    private List<String> processList;
    private List<Pattern> excludeRegexList;
    private JobDirectory templateJobManager;

    public Configuration(String configFile, TaskExecutionContext context) throws TaskExecutionException {
        if( context == null ) {
            this.logger = LogManager.getFormatterLogger();
        } else {
            this.logger = context.getLogger();
        }
        String os = System.getProperty("os.name");
        logger.debug(String.format("Operating System: %s", os));
        if( os.toLowerCase().contains("windows") ) {
            isRunningOnWindows=true;
            this.processInfo = new WindowsProcessInfo(this);
        } else {
            this.processInfo = new UnixProcessInfo(this);
        }
        this.grokDir = new File("./resources/grok");
        this.jobDir = new File("./resources/jobs");
        if( context == null ) {
            this.runDir = new File(".");
        } else {
            this.runDir = new File(context.getTaskDir());
        }
        init(configFile);
    }

    public void init(String newConfigFileName) throws TaskExecutionException {
        if( newConfigFileName == null ) {
            logger.warn("Config file is null, assuming a unit test");
            return;
        }
        if( this.configFileName == null ) {
            this.configFileName = newConfigFileName;
            this.configFile = new File(newConfigFileName);
            this.configFileLastUpdate = this.configFile.lastModified();
            readConfigFile();
        } else {
            File newConfigFile = new File(newConfigFileName);
            if( !newConfigFileName.equals(this.configFileName) || newConfigFile.lastModified() > this.configFileLastUpdate ) {
                this.configFileName = newConfigFileName;
                this.configFile = newConfigFile;
                this.configFileLastUpdate = newConfigFile.lastModified();
                readConfigFile();
            }
        }
    }

    private void readConfigFile() throws TaskExecutionException {
        Yaml yaml = new Yaml(new Constructor(ConfigurationModel.class));
        try {
            ConfigurationModel configurationModel = yaml.load(new FileReader(this.configFile));
            this.grokDir = new File(this.runDir, configurationModel.getGrokDirectory());
            this.jobDir = new File(this.runDir, configurationModel.getJobDirectory());
            String analDir = configurationModel.getAnalyticsDirectory();
            if( analDir == null || "".equals(analDir) ) {
                this.analyticsJobDir = new File( this.runDir, "../analytics-agent/conf/job");
            } else {
                this.analyticsJobDir = new File(analDir, "conf/job");
            }
            this.psCommandLine = configurationModel.getPsCommand();
            this.lsofCommandLine = configurationModel.getLsofCommand();
            this.processList = configurationModel.getProcess();
            this.logfileList = configurationModel.getLogs();
            this.excludeRegexList = configurationModel.getExcludeLogsRegexList();
        } catch (Exception exception) {
            throw new TaskExecutionException(String.format("Error reading configuration file: %s Exception: %s",this.configFileName, exception));
        }

        StringBuilder errorMessages = new StringBuilder();
        if( !this.grokDir.isDirectory() ) errorMessages.append(String.format("Grok file directory does not exist: '%s'",grokDir.getAbsolutePath())).append("\n");
        if( !this.jobDir.isDirectory() ) errorMessages.append(String.format("Template Job file directory does not exist: '%s'",jobDir.getAbsolutePath())).append("\n");
        if( !this.analyticsJobDir.isDirectory() ) errorMessages.append(String.format("Analytics Agent Extention directory does not exist: '%s'",analyticsJobDir.getAbsolutePath())).append("\n");
        if( (processList == null || processList.size() == 0) && (logfileList == null || logfileList.size() == 0) )
            errorMessages.append("Both Log files and Process Listing are empty, nothing to do here").append("\n");

        if( errorMessages.length() > 0 ) throw new TaskExecutionException(errorMessages.toString());
    }

    public File[] getGrokFiles() {
        if( !this.grokDir.exists() || !this.grokDir.isDirectory() ) {
            logger.error("Grok file directory does not exist! "+ this.grokDir.getAbsolutePath() );
            return null;
        } else {
            return this.grokDir.listFiles( new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".grok");
                }
            });
        }
    }

    public File[] getJobFiles() {
        if( !this.jobDir.exists() || !this.jobDir.isDirectory() ) {
            logger.error("Job file directory does not exist! "+ this.jobDir.getAbsolutePath() );
            return null;
        } else {
            return this.jobDir.listFiles( new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".job");
                }
            });
        }
    }

    public boolean excludeLogFile( String filename ) {
        if( this.excludeRegexList == null || this.excludeRegexList.size() == 0 ) return false;
        for( Pattern pattern : this.excludeRegexList ) {
            if( pattern.matcher(filename).matches() ) return true;
        }
        return false;
    }

    public Logger getLogger() { return logger; }
    public boolean isWindows() { return isRunningOnWindows; }
    public ProcessInfo getProcessInfo() { return processInfo; }
    public String getPsCommandLine() { return psCommandLine; }
    public String getLsofCommandLine() { return lsofCommandLine; }
    public List<String> getLogfileList() { return logfileList; }
    public List<String> getProcessList() { return processList; }
    public JobDirectory getTemplateJobManager() throws JobFileException {
        if( this.templateJobManager == null ) this.templateJobManager = new JobDirectory(this.jobDir.getPath(), this);
        return this.templateJobManager;
    }
    public File getAnalyticsJobDir() { return this.analyticsJobDir; }
}
