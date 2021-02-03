package org.log.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogFilterTest {
    private final LogFileFilterImpl logFileFilter = new LogFileFilterImpl();
    private static final List<String> originalList = new ArrayList<>();
    private final List<String> wordsToInclude = new ArrayList<>();
    private final List<String> wordsToExclude = new ArrayList<>();

    @BeforeAll
    public static void addWordsToOriginalList() {
        originalList.addAll(Arrays.asList("New sip request","Rtp received","ServerConnectivity check","TB_REQUEST","Connection timeout"));
    }

    @AfterEach
    public void clearLists() {
        originalList.clear();
        wordsToInclude.clear();
        wordsToExclude.clear();
    }

    @Test
    public void filterIncludeReturnsAllIncludedWords() {
        wordsToInclude.addAll(Arrays.asList("sip","rtp","check"));
        List<String> filteredList = logFileFilter.filterListBy(originalList, wordsToInclude, wordsToExclude);

        for (String lineToInclude : wordsToInclude) {
            boolean isWordIncluded = false;
            for (String fileLine : filteredList) {
                if (fileLine.toLowerCase().contains(lineToInclude.toLowerCase())) {
                    isWordIncluded = true;
                    break;
                }
            }
            Assertions.assertTrue(isWordIncluded, "There's a word missing in the list: " + filteredList.toString());
        }
    }

    @Test
    public void filterExcludeDoesNotReturnsExcludedWords() {
        wordsToExclude.addAll(Arrays.asList("connecti","request"));
        List<String> filteredList = logFileFilter.filterListBy(originalList, wordsToInclude, wordsToExclude);

        for (String lineToExclude : wordsToExclude) {
            boolean isWordExcluded = true;
            for (String fileLine : filteredList) {
                if (fileLine.toLowerCase().contains(lineToExclude.toLowerCase())) {
                    isWordExcluded = false;
                    break;
                }
            }
            Assertions.assertTrue(isWordExcluded, "There's a word included in the list and it shouldn't: " + filteredList.toString());
        }
    }

    @Test
    public void filterIncludesReturnsWordsAndFilterExcludeFiltersThem() {

        wordsToInclude.addAll(Arrays.asList("sip","rtp","check"));
        wordsToExclude.addAll(Arrays.asList("connecti","request"));
        List<String> filteredList = logFileFilter.filterListBy(originalList, wordsToInclude, wordsToExclude);

        for (String lineToExclude : wordsToExclude) {
            boolean isWordExcluded = true;
            for (String fileLine : filteredList) {
                if (fileLine.toLowerCase().contains(lineToExclude.toLowerCase())) {
                    isWordExcluded = false;
                    break;
                }
            }

            Assertions.assertTrue(isWordExcluded, "There's a word included in the list and it shouldn't: " + filteredList.toString());
        }

        for (String fileLine : filteredList) {
            boolean isWordIncluded = false;
            for (String lineToInclude : wordsToInclude) {
                if (fileLine.toLowerCase().contains(lineToInclude.toLowerCase())) {
                    isWordIncluded = true;
                    break;
                }
            }
            Assertions.assertTrue(isWordIncluded, "There's a word missing in the list: " + filteredList.toString());
        }
    }

    @Test
    public void nothingIsFilteredIfFiltersAreEmpty() {
        List<String> filteredList = logFileFilter.filterListBy(originalList, wordsToInclude, wordsToExclude);

        Assertions.assertEquals(filteredList.size(), originalList.size(), "Nothing should be filtered when filters are empty!");
    }

    @Test
    public void logLinesWithoutTimestampAreRemoved() {
        List<String> testList = new ArrayList<>(originalList);
        testList.addAll(Arrays.asList("2021-02-01 20:03:56,790 DEBUG> [MCPTT 7193] MCPTT_GEORED_LIB ConnectivityInteractorImpl.onGeoredCheck: [GeoredConnectionStatus] oldIp=mcptt1.myvzw.com, newIp=mcptt1.myvzw.com, checktime02-01 20:03:56.789, isServerIpSwitchedfalse",
                "Interface [13] rmnet_data1, host address= 2600:100c:d2c3:5c87:9c03:d144:bf95:ffef",
                "Interface [13] rmnet_data1, host address= 10.169.243.182",
                "",
                "2021-02-01 20:03:56,790 DEBUG> [MCPTT 7193] MCPTT_CONN_LIB ConnectivityInteractorImpl.setCurrentIp: mcptt1.myvzw.com",
                "Log:",
                "2021-02-01 20:03:56,791 DEBUG> [MCPTT 7193] MCPTT_TAG_GEORED_INTEGRATION SpiService.onGeoredCheck called, GeoredConnectionStatus = [GeoredConnectionStatus] oldIp=mcptt1.myvzw.com, newIp=mcptt1.myvzw.com, checktime02-01 20:03:56.789, isServerIpSwitchedfalse"));

        testList = logFileFilter.removeLogsWithoutTimestamp(testList);

        Assertions.assertEquals(3, testList.size(), "The list without timestamps should have 3 elements, but it has " + testList.size() + " instead");
    }
}