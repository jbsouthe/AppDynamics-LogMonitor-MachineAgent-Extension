package com.appdynamics.machineagent.logmonitor.jobs;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import com.appdynamics.machineagent.logmonitor.model.JobModel;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

/*
JobFile represents a physical file in the ./monitors/analytics-agent/conf/job/ directory, some may be in use, others simply templates
any can be copied and used again with a new file

we need to be able to interpret and change configuration within them, ie, control them
we will use this same class to manage the template job files and the active job files, without modifying those files a customer may have placed there
 */
public class JobFile {
    private static final Logger logger = LogManager.getFormatterLogger();
    private File jobFileHandle;
    private JobModel model;
    private GrokCompiler grok;
    private Configuration configuration;

    private JobFile( JobFile other, File file ) throws CloneNotSupportedException {
        this.configuration= other.configuration;
        this.model = (JobModel) other.model.clone();
        this.jobFileHandle = file;
    }

    public JobFile(File jobFile, JobModel jobModel, Configuration configuration) {
        this.configuration=configuration;
        this.model=jobModel;
        this.jobFileHandle=jobFile;
        this.grok = GrokCompiler.newInstance();
        grok.registerDefaultPatterns();
        if( model.getEventTimestamp() != null ) {
            grok.register( "eventTimestamp",model.getEventTimestamp().getPattern());
        }
        for( File grokFile : configuration.getGrokFiles() ) {
            try {
                grok.register(new FileReader(grokFile));
                //logger.debug("loaded grok file: "+ grokFile.getName());
            } catch (Exception exception) {
                logger.warn(String.format("error loading grok file %s Exception: %s",grokFile.getName(), exception));
            }
        }
    }

    public void setJobFileHandle( File file ) { this.jobFileHandle=file; }
    public File getJobFileHandle() { return this.jobFileHandle; }
    public String toString() {
        return String.format("Job File: %s Model: %s", (this.jobFileHandle == null ? "none" : this.jobFileHandle.getAbsolutePath()), model);
    }


    //returns a double from 0.0 to 100.0 giving an indication of matching compatibility
    public double testGrok( String lines ) {
        if( lines == null ) return 0.0d;
        long totalPossibleMatches = 0l;
        long totalActualMatches = 0l;
        logger.debug("%s testGrok() grok: '%s'", (this.jobFileHandle == null ? "none" : this.jobFileHandle.getName()),(model.getGrok()!= null ? model.getGrok().getPatterns().toString() : "null"));
        if ( model.getGrok() == null ) { //this happens sometimes, look at sample-osx-system-log.job
            return Double.MIN_VALUE; //try not to match on this
        }
        for( String grokPattern : model.getGrok().getPatterns() ) {
            try {
                logger.trace("Testing with grok: '%s' for job file %s", grokPattern, (this.jobFileHandle == null ? "none" : this.jobFileHandle.getName()));
                long possibleMatchesPerLine = countTotalPossibleMatches(grokPattern);
                Grok compiledPattern = grok.compile(grokPattern);
                logger.trace("compiled pattern: %s", compiledPattern.getNamedRegex());
                for (String line : lines.split("\\n")) {
                    totalPossibleMatches += possibleMatchesPerLine;
                    Match gm = compiledPattern.match(line);
                    Map<String, Object> groups = gm.capture();
                    totalActualMatches += countTotalActualMatches(groups);
                    logger.trace("input line '%s' grok pattern '%s' result: '%s'", line, grokPattern, groups);
                }
            } catch(IllegalArgumentException illegalArgumentException) {
                logger.debug("Bad Parser Job File %s Exception: %s",(this.jobFileHandle == null ? "none" : this.jobFileHandle.getName()), illegalArgumentException);
                return Double.MIN_VALUE;
            }
        }
        if( totalPossibleMatches == 0 ) totalPossibleMatches+=0.000000001;
        return totalActualMatches/totalPossibleMatches;
    }

    private long countTotalActualMatches(Map<String, Object> groups) {
        long total = 0;
        for( Object object : groups.values()) {
            if( object != null && !"".equals(String.valueOf(object))) total++;
        }
        return total;
    }

    private long countTotalPossibleMatches( String grokPattern ) {
        return Arrays.stream(grokPattern.split("\\%\\{")).count();
    }

    public JobFile copy( File targetDir, File logFile ) throws IOException, CloneNotSupportedException {
        String path = logFile.getParent();
        String nameGlob = logFile.getName();
        String basename = nameGlob.replaceAll("\\.", "-");
        File file = File.createTempFile(String.format("auto-%s-",basename), ".job", targetDir);
        logger.debug("Job File designated: "+ file.getAbsolutePath());
        if( !logger.isDebugEnabled() ) file.deleteOnExit();
        JobFile jobFile = new JobFile(this, file );
        jobFile.setSourceInfo(true, path, nameGlob);
        PrintWriter printWriter = new PrintWriter(file);
        Representer representer = new Representer( new DumperOptions()) {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                // if value of property is null, ignore it.
                if (propertyValue == null) {
                    return null;
                }
                else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };
        Yaml yaml = new Yaml( representer );
        yaml.dump(this.model, printWriter);
        return jobFile;
    }

    public void delete() { if( this.jobFileHandle != null) this.jobFileHandle.delete(); }

    private void setSourceInfo(boolean enabled, String path, String nameGlob) {
        this.model.setEnabled(enabled);
        this.model.getSource().put("path",path);
        this.model.getSource().put("nameGlob",nameGlob);
    }
}
