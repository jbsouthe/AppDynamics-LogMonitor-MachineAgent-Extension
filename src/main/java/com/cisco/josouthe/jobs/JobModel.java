package com.cisco.josouthe.jobs;

import java.util.List;
import java.util.Map;
import com.cisco.josouthe.jobs.model.*;

public class JobModel {
    private int version;
    private boolean enabled;
    private Map<String,Object> source;
    private Multiline multiline;
    private Map<String,String> fields;
    private Grok grok;
    private EventTimestamp eventTimestamp;
    private RequestGuid requestGuid;

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    private Transform transform;

    public KeyValue getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    private KeyValue keyValue;

    public JobModel() {}

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String,Object> getSource() {
        return source;
    }

    public void setSource(Map<String,Object> source) {
        this.source = source;
    }

    public Multiline getMultiline() {
        return multiline;
    }

    public void setMultiline(Multiline multiline) {
        this.multiline = multiline;
    }

    public Map<String,String> getFields() {
        return fields;
    }

    public void setFields(Map<String,String> fields) {
        this.fields = fields;
    }

    public Grok getGrok() {
        return grok;
    }

    public void setGrok(Grok grok) {
        this.grok = grok;
    }

    public EventTimestamp getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(EventTimestamp eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public RequestGuid getRequestGuid() {
        return requestGuid;
    }

    public void setRequestGuid(RequestGuid requestGuid) {
        this.requestGuid = requestGuid;
    }
}
