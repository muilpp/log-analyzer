package org.log.application.usecases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.log.domain.ports.logfile.LogFileOpener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogFileOpenerImpl implements LogFileOpener {
    private static final Logger logger = LogManager.getLogger(LogFileOpenerImpl.class);

    @Override
    public List<String> openFile(String fileName) {
        List<String> logList = new ArrayList<>();
        try {
            File openedFile = new File(fileName);
            Scanner logReader = new Scanner(openedFile);

            while (logReader.hasNextLine()) {
                String logLine = logReader.nextLine();
                logList.add(logLine);
            }
            logReader.close();
        } catch (FileNotFoundException e) {
            logger.error("LogFileOpenerImpl.openFile:: File not found: " + e);
        }
        return logList;
    }
}
