package com.appdynamics.machineagent.logmonitor.process;

import java.util.List;

public interface ProcessInfo {
    public List<ProcessDetails> getProcessList( String name );
    public List<OpenFile> getOpenFiles( long pid );
}
