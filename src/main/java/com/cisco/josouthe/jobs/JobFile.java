package com.cisco.josouthe.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


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

    public JobFile(File jobFile, JobModel jobModel) {
        this.model=jobModel;
        this.jobFileHandle=jobFile;
    }

    public void setJobFileHandle( File file ) { this.jobFileHandle=file; }
    public File getJobFileHandle() { return this.jobFileHandle; }
    public String toString() {
        return String.format("Job File: %s Model: %s", this.jobFileHandle.getAbsolutePath(), model);
    }


    //returns a double from 0.0 to 100.0 giving an indication of matching compatibility
    public double testGrok( String lines ) {
        if( lines == null ) return 0.0d;
        double score = 0.0d;
        logger.warn("%s testGrok() grok: '%s'", this.jobFileHandle.getName(),(model.getGrok()!= null ? model.getGrok().getPatterns().toString() : "null"));
        //TODO the magic here

        return score;
    }


}
