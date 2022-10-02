package com.appdynamics.machineagent.logmonitor.model;

import java.util.List;

public class KeyValue {
    private List<String> source;
    private String split, separator;
    private List<String> include;
    private List<String> trim;

    public KeyValue() {}

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    public List<String> getTrim() {
        return trim;
    }

    public void setTrim(List<String> trim) {
        this.trim = trim;
    }
}
