package com.appdynamics.machineagent.logmonitor.process;

import com.appdynamics.machineagent.logmonitor.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LinuxProcessInfo implements ProcessInfo {
    private final Configuration configuration;
    private final Logger logger;
    private final File procDirectory;
    private boolean procDirNotValid;

    public LinuxProcessInfo(Configuration configuration) {
        this.configuration = configuration;
        this.logger = configuration.getLogger();
        this.procDirectory = configuration.getProcDirectory();
        this.procDirNotValid = false;
        if (!procDirectory.exists() || !procDirectory.isDirectory()) {
            logger.error(procDirectory.getAbsolutePath() + " does not exist or is not a directory, we can not query processes or files open");
            this.procDirNotValid=true;
        } else {
            logger.info("Starting Linux Process Info Collector");
        }
    }

    @Override
    public List<ProcessDetails> getProcessList (String name) {
        if (procDirNotValid) return null;
        File[] processDirectories = procDirectory.listFiles();
        if (processDirectories == null) {
            logger.warn("No processes found in " + procDirectory.getAbsolutePath() + " directory.");
            return null;
        }

        List<ProcessDetails> processDetailsList = new ArrayList<>();
        for (File processDirectory : processDirectories) {
            if (processDirectory.isDirectory() && processDirectory.getName().matches("\\d+")) {
                try {
                    String commandLine = readProcessCommandLine(processDirectory);
                    if (commandLine.contains(name)) {
                        long pid = Long.parseLong(processDirectory.getName());
                        ProcessDetails processDetails = new ProcessDetails(name, pid, commandLine);
                        processDetails.setOpenFileList(getOpenFiles(pid));
                        processDetailsList.add(processDetails);
                    }
                } catch (IOException e) {
                    logger.warn(String.format("Error reading process details from %s, Exception: '%s'", processDirectory.getAbsolutePath(), e),e);
                }
            }
        }
        return processDetailsList;
    }

    private static String readProcessCommandLine(File processDirectory) throws IOException {
        String commandLineFilePath = processDirectory.getAbsolutePath() + "/cmdline";

        try (BufferedReader reader = new BufferedReader(new FileReader(commandLineFilePath))) {
            StringBuilder commandLine = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                commandLine.append(line);
                commandLine.append(" ");
            }
            return commandLine.toString().trim();
        }
    }

    @Override
    public List<OpenFile> getOpenFiles (long pid) {
        if (procDirNotValid) return null;
        File procFileDir = new File(this.procDirectory.getAbsolutePath(), String.format("%d/fd/", pid));
        if (!procFileDir.exists() || !procFileDir.isDirectory()) {
            logger.warn("Process File Handle references does not exist or is not a directory: "+ procFileDir.getAbsolutePath() );
            return null;
        }

        File[] files = procFileDir.listFiles();
        if (files == null || files.length == 0) {
            logger.debug("No open files found for process ID " + pid);
            return null;
        }
        List<OpenFile> openFileList = new ArrayList<>();
        for (File file : files) {
            try {
                String fileName = getFileName(file);
                if (fileName.endsWith(".log")) {
                    logger.debug(String.format("Adding file %s for pid %d", fileName, pid));
                    openFileList.add(new OpenFile(fileName, "process", pid));
                } else {
                    logger.trace(String.format("Ignoring file %s for pid %d", fileName, pid));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return openFileList;
    }

    private String getFileName(File file) throws IOException {
        String link = file.getAbsolutePath();
        File linkFile = new File(link);
        String canonicalPath = linkFile.getCanonicalPath();
        return canonicalPath;
    }
}
