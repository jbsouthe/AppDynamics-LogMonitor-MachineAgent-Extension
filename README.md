# AppDynamics Log Monitor Machine Agent Extension

This extension allows customers to use the Machine Agent along with the Machine Agent Analytics Agent Extension to automatically detect and monitor log files. It is intended to support any Unix operating system as well as Windows (Windows support may vary).

* identify open log files of a process with a given string match, or files in a given directory matching a string
* test a sample of the log file found to see which type of file it is 
* create a new Job file for the log file in the Analytics Agent Job directory
* This job file will inform the analytics agent to begin monitoring based on those rules
* When the Machine Agent shuts down, all generated job files are deleted automatically.  This allows for re-detection of application processes and associated log files and updated log file message format matching

note: the monitored log files won't be displayed anywhere on the controller, but the logs and their data will be seen in the Analytics Log Schema "select * from logs" will show them.

## Configuration Steps:
1. install the AppDynamics Machine Agent
2. enable and configure the analytics agent extension in the ./monitors/analytics-agent
3. enable and configure this extension in the ./monitors/LogMonitor directory by editing the monitor.xml and changing the default-config.yml in the same directory

## Configuration File Details

### monitor.xml

the monitor.xml file is used for the machine agent config, it contains:

    <monitor>
        <name>AppDynamics Log Monitor Automation Extension</name>
        <type>managed</type>
        <enabled>true</enabled>
        <description>AppDynamics Log Monitor Automation Extension</description>
        <display-name>AppDynamics Log Monitor Automation Extension</display-name>
        <monitor-configuration>
        </monitor-configuration>
        <monitor-run-task>
            <task-arguments>
                <argument name="config-file" is-required="true" default-value="./default-config.yml" />
            </task-arguments>
            <type>java</type>
            <execution-style>scheduled</execution-style>
            <java-task>
                <impl-class>
                    com.appdynamics.machineagent.logmonitor.ProcessLogMonitor
                </impl-class>
                <classpath>lib</classpath>
                <load-jars-in-classpath-dirs>true</load-jars-in-classpath-dirs>
            </java-task>
        </monitor-run-task>
    </monitor>


### default-config.yml

the default-config.yml should be copied and edited for customer environment

    #default configuration, this can be edited, and should be copied to a new file in case of upgrade
    grokDirectory: ./grok
    jobDirectory: ./jobs

    #we will create the job files here: {analyticsDirectory}/conf/job
    #this is optional, we will try and determine this automatically
    #analyticsDirectory: ../analytics-agent

    #operating system specific commands
    psCommand: "/bin/ps -ef"
    lsofCommand: "/usr/sbin/lsof -p %d"

    #windows would have these:
    #psCommand: pslist.exe
    #lsofCommand: "handle.exe -p %d"

    process:
    - java
    - -Dappdynamics.com

    logs:
    - /var/log/*.log
