package org.log.usecases;

import org.log.ports.out.LogFileExporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LogFileExporterImpl implements LogFileExporter {

    @Override
    public boolean exportLogToFile(File file, List<String> logFileList) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);

            for (String logLine : logFileList) {
                writer.println(logLine);
            }

            writer.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Could not save log to file: " + ex);
            return false;
        }
    }
}
