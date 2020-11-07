package org.log.ports.out;

import java.io.File;
import java.util.List;

public interface LogFileExporter {
    boolean exportLogToFile(File file, List<String> logFileList);
}
