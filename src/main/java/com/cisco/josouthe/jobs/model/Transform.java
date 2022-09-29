package com.cisco.josouthe.jobs.model;

import java.util.List;

public class Transform {
    private List<TransformField> fields;

    public Transform() {}

    public List<TransformField> getFields() {
        return fields;
    }

    public void setFields(List<TransformField> fields) {
        this.fields = fields;
    }
}
