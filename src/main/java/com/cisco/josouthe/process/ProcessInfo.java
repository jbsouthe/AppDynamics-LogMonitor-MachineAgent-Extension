package com.cisco.josouthe.process;

import java.util.List;

public interface ProcessInfo {
    public List<ProcessDetails> getProcessList( String name );
    public List<OpenFile> getOpenFiles( long pid );
}
