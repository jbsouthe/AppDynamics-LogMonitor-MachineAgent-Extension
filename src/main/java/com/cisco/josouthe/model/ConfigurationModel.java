package com.cisco.josouthe.model;

import java.util.List;

public class ConfigurationModel {
    private String analyticsDirectory=null, psCommand="ps -ef", lsofCommand="lsof -p %d", grokDirectory="./grok", jobDirectory="./jobs";
    private List<String> process, logs;
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
}
