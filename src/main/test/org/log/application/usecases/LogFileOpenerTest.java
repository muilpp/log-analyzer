package org.log.application.usecases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.log.domain.ports.logfile.LogFileOpener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class LogFileOpenerTest {
    private static final Logger logger = LogManager.getLogger(LogFileOpenerTest.class);
    private final String FILE_NAME = "logFile.txt";
    private final LogFileOpener logFileOpener = new LogFileOpenerImpl();
    private List<String> logList;

    @BeforeEach
    public void createFilterFile() {
        try {
            logList = Arrays.asList("2020-11-11 00:00:23,659 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator.processRequest: BYE  from user:sip:healthcheckerint@ericsson.net",
            "2020-11-11 00:00:23,659 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator: BYE out-of-dialog, sending 481",
            "2020-11-11 00:00:26,121 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator: processRequest OPTIONS sip:healthcheckerint@ericsson.net",
            "2020-11-11 00:00:26,121 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator.processRequest: OPTIONS  from user:sip:healthcheckerint@ericsson.net",
            "2020-11-11 00:00:26,121 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator: processRequest INVITE sip:healthcheckerint@ericsson.net");

            PrintStream fileStream = new PrintStream(new File(FILE_NAME));

            for (String logLine : logList) {
                fileStream.println(logLine);
            }

            fileStream.close();
        } catch (IOException e) {
            logger.error("LogFileOpenerTest.createFilterFile:: An error occurred while trying to write to file: " + e);
        }
    }

    @AfterEach
    public void deleteTestFile() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_NAME));
    }

    @Test
    public void fileIsOpenedAndContentCorrectlyProcessedWhenFileIsNotEmpty() {
        List<String> fileLogList = logFileOpener.openFile(FILE_NAME);
        Assertions.assertEquals(logList.size(), fileLogList.size());
        logger.debug(fileLogList);
    }

    @Test
    public void fileIsOpenedAndContentCorrectlyProcessedWhenFileIsEmpty() throws FileNotFoundException {
        new PrintStream(new File(FILE_NAME));
        List<String> fileLogList = logFileOpener.openFile(FILE_NAME);
        Assertions.assertEquals(0, fileLogList.size());
        logger.debug(fileLogList);
    }

    @Test
    public void fileIsOpenedAndContentCorrectlyProcessedWhenFileDoesNotExist() throws IOException {
        deleteTestFile();

        List<String> fileLogList = logFileOpener.openFile(FILE_NAME);
        Assertions.assertEquals(0, fileLogList.size());
    }
}
