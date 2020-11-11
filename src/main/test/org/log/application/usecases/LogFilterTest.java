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
}