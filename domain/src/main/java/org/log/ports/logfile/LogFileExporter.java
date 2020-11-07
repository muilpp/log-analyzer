package org.log.ports.logfile;

import java.io.File;
import java.util.List;

public interface LogFileExporter {
    boolean exportLogToFile(File file, List<String> logFileList);
}
