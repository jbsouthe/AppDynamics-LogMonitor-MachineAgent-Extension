package com.cisco.josouthe.model;

public class Source implements Cloneable {
    private String type, path, nameGlob;
    private boolean startAtEnd;

    public Source() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNameGlob() {
        return nameGlob;
    }

    public void setNameGlob(String nameGlob) {
        this.nameGlob = nameGlob;
    }

    public boolean isStartAtEnd() {
        return startAtEnd;
    }

    public void setStartAtEnd(boolean startAtEnd) {
        this.startAtEnd = startAtEnd;
    }

    protected Object clone() throws CloneNotSupportedException { return super.clone(); }
}
