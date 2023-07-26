package com.appdynamics.machineagent.logmonitor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigurationModel {
    private String analyticsDirectory=null, psCommand="ps -ef", lsofCommand="lsof -p %d", grokDirectory="./grok", jobDirectory="./jobs", procDirectory="/proc";
    private List<String> process, logs, excludeLogs;
    public ConfigurationModel() {}

    public String getAnalyticsDirectory() {
        return analyticsDirectory;
    }

    public void setAnalyticsDirectory(String analyticsDirectory) {
        this.analyticsDirectory = analyticsDirectory;
    }

    public String getPsCommand() {
        return psCommand;
    }

    public void setPsCommand(String psCommand) {
        this.psCommand = psCommand;
    }

    public String getLsofCommand() {
        return lsofCommand;
    }

    public void setLsofCommand(String lsofCommand) {
        this.lsofCommand = lsofCommand;
    }

    public List<String> getProcess() {
        return process;
    }

    public void setProcess(List<String> process) {
        this.process = process;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public String getGrokDirectory() {
        return grokDirectory;
    }

    public void setGrokDirectory(String grokDirectory) {
        this.grokDirectory = grokDirectory;
    }

    public String getJobDirectory() {
        return jobDirectory;
    }

    public void setJobDirectory(String jobDirectory) {
        this.jobDirectory = jobDirectory;
    }

    public String getProcDirectory(){ return procDirectory; }

    public void setProcDirectory (String procDirectory) {
        this.procDirectory = procDirectory;
    }

    public List<String> getExcludeLogs() {
        return excludeLogs;
    }

    public void setExcludeLogs(List<String> excludeLogs) {
        this.excludeLogs = excludeLogs;
    }

    public List<Pattern> getExcludeLogsRegexList() {
        List<Pattern> patternList = new ArrayList<>();
        if( excludeLogs == null || excludeLogs.size()==0 ) return patternList;
        for( String patternString : this.excludeLogs ) {
            patternList.add( Pattern.compile(patternString));
        }
        return patternList;
    }
}
