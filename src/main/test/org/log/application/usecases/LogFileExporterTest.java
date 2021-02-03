package org.log.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.log.domain.ports.logfile.LogFileExporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogFileExporterTest {

    private LogFileExporter logFileExporter = new LogFileExporterImpl();
    private static List<String> logList;
    private final String FILE_NAME = "exportedLog.txt";

    @BeforeAll
    public static void initLogList() {
        logList = Arrays.asList("2020-11-11 00:00:23,659 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator.processRequest: BYE  from user:sip:healthcheckerint@ericsson.net",
                "2020-11-11 00:00:23,659 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator: BYE out-of-dialog, sending 481",
                "2020-11-11 00:00:26,121 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator: processRequest OPTIONS sip:healthcheckerint@ericsson.net",
                "2020-11-11 00:00:26,121 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator.processRequest: OPTIONS  from user:sip:healthcheckerint@ericsson.net",
                "2020-11-11 00:00:26,121 INFO  [net.genaker.pocserver.poc.PocRoleDeterminator] PocRoleDeterminator: processRequest INVITE sip:healthcheckerint@ericsson.net");
    }

    @AfterEach
    public void deleteTestFile() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_NAME));
    }

    @Test
    public void logIsCorrectlyExportedWhenLogIsNotEmpty() {
        boolean isExported = logFileExporter.exportLogToFile(new File(FILE_NAME), logList);
        Assertions.assertTrue(isExported);
    }

    @Test
    public void logIsCorrectlyExportedWhenLogIsEmpty() {
        boolean isExported = logFileExporter.exportLogToFile(new File(FILE_NAME), Collections.emptyList());
        Assertions.assertTrue(isExported);
    }

    @Test
    public void logIsNotExportedIfFileDoesNotExist() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            logFileExporter.exportLogToFile(null, logList);
        });
    }
}