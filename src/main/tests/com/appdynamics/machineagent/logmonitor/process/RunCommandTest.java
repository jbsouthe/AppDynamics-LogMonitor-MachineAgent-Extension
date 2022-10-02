package com.appdynamics.machineagent.logmonitor.process;

import junit.framework.TestCase;


public class RunCommandTest extends TestCase {

    public RunCommandTest() {}

    public void testRunCommand() {
        RunCommand psCommand = new RunCommand("/bin/bash","-c",String.format("/bin/ps -ef | grep %s","java"));
        assert psCommand.isSuccess();
        psCommand = new RunCommand("/bin/bash","-c",String.format("/bin/ps -ef | greppppp %s","java"));
        assert !psCommand.isSuccess();
        assert "/bin/bash: greppppp: command not found".equals(psCommand.getErrOut());
        //System.out.println("Errors: "+ psCommand.getErrOut());
    }

}