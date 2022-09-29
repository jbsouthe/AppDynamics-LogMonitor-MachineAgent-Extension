package com.cisco.josouthe.process;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class RunCommandTest extends TestCase {

    public RunCommandTest() {}

    @Test
    public void testRunCommand() {
        RunCommand psCommand = new RunCommand("/bin/bash","-c",String.format("/bin/ps -ef | grep %s","java"));
        assert psCommand.isSuccess();
        psCommand = new RunCommand("/bin/bash","-c",String.format("/bin/ps -ef | greppppp %s","java"));
        assert !psCommand.isSuccess();
        assert "/bin/bash: greppppp: command not found".equals(psCommand.getErrOut());
        //System.out.println("Errors: "+ psCommand.getErrOut());
    }

}