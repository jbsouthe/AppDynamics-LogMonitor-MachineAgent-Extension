package com.appdynamics.machineagent.logmonitor.model;

public class Multiline {
    private String startsWith, regex;

    public Multiline() {}

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
