package org.log.service;

import org.log.ports.in.LogFileFilter;
import org.log.ports.in.LogFileOpener;
import org.log.ports.out.LogFileExporter;

import java.io.File;
import java.util.List;

public class LogFileInteractor {

    private final LogFileFilter logFileFilter;
    private final LogFileOpener logFileOpener;
    private final LogFileExporter logFileExporter;

    public LogFileInteractor(LogFileFilter logFileFilter, LogFileOpener logFileOpener, LogFileExporter logFileExporter) {
        this.logFileFilter = logFileFilter;
        this.logFileOpener = logFileOpener;
        this.logFileExporter = logFileExporter;
    }

    public List<String> loadLogFile(String logFileName) {
        return logFileOpener.openFile(logFileName);
    }

    public List<String> filterListBy(List<String> originalList, List<String> wordsToFilter) {
        if (wordsToFilter.isEmpty())
            return originalList;

        return logFileFilter.filterListBy(originalList, wordsToFilter);
    }

    public boolean exportToLog(File file, List<String> logFileList) {
        return logFileExporter.exportLogToFile(file, logFileList);
    }
}