package com.cisco.josouthe.config;

import com.cisco.josouthe.process.ProcessInfo;
import com.cisco.josouthe.process.UnixProcessInfo;
import com.cisco.josouthe.process.WindowsProcessInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;

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

    public Configuration(String configFile, Logger parentLogger) {
        if( parentLogger == null ) {
            this.logger = LogManager.getFormatterLogger();
        } else {
            this.logger = parentLogger;
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
        init(configFile);
    }

    public void init(String newConfigFileName) {
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

    private void readConfigFile() {
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

    public Logger getLogger() { return logger; }
    public boolean isWindows() { return isRunningOnWindows; }
    public ProcessInfo getProcessInfo() { return processInfo; }
}
