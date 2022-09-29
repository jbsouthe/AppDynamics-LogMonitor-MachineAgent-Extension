package com.cisco.josouthe.jobs.model;

import java.util.List;

public class Grok {
    private List<String> patterns;
    public Grok() {}
    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
