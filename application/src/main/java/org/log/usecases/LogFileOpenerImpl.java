package org.log.usecases;

import org.log.ports.logfile.LogFileOpener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogFileOpenerImpl implements LogFileOpener {

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
            System.out.println("File not found: " + e);
        }
        return logList;
    }
}
