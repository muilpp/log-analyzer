package org.log.application.service;

import javafx.scene.text.Text;
import org.log.domain.ports.logfile.LogFileExporter;
import org.log.domain.ports.logfile.LogFileFilter;
import org.log.domain.ports.logfile.LogFileOpener;

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

    public List<Text> loadLogFile(String logFileName) {
        return logFileOpener.openFile(logFileName);
    }

    public List<Text> filterListBy(List<Text> originalList, List<String> wordsToInclude, List<String> wordsToExclude) {
        if (wordsToInclude.isEmpty() && wordsToExclude.isEmpty())
            return originalList;

        return logFileFilter.filterListBy(originalList, wordsToInclude, wordsToExclude);
    }

    public List<Text> removeLogsWithoutTimestamp(List<Text> list) {
        return logFileFilter.removeLogsWithoutTimestamp(list);
    }

    public boolean exportToLog(File file, List<String> logFileList) {
        return logFileExporter.exportLogToFile(file, logFileList);
    }
}