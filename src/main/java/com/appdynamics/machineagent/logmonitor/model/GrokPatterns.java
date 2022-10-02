package com.appdynamics.machineagent.logmonitor.model;

import java.util.List;

public class GrokPatterns {
    private List<String> patterns;
    public GrokPatterns() {}
    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
