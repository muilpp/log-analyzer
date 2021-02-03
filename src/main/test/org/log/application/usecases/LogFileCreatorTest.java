package org.log.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.log.domain.entities.Filter;
import org.log.infrastructure.FilePersistor;

import java.util.List;

public class LogFileCreatorTest {

    private final FilePersistor filePersistor = new FilePersistor();
    private static final String FILTER_NAME = "newFilter";
    private static final String FILTER_DATA = "filterData";

    @AfterEach
    public void deleteFilter() {
        filePersistor.delete(FILTER_NAME);
    }

    @Test
    public void newFilterIsCreated() {


        boolean isCreated = filePersistor.create(FILTER_NAME, FILTER_DATA);
        Assertions.assertTrue(isCreated);

        List<Filter> filterList = filePersistor.findAll();
        boolean isNewFilterFound = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(FILTER_NAME)
                    && filter.getFilterData().equalsIgnoreCase(FILTER_DATA)) {
                isNewFilterFound = true;
                break;
            }
        }
        Assertions.assertTrue(isNewFilterFound, "Filter " + FILTER_NAME + " should have been created, but it's not in the list");
    }

    @Test
    public void filterIsNotCreatedIfAlreadyExists() {
        boolean isFilterCreated = filePersistor.create(FILTER_NAME, FILTER_DATA);
        Assertions.assertTrue(isFilterCreated);

        boolean isDuplicatedCreated = filePersistor.create(FILTER_NAME, FILTER_DATA);
        Assertions.assertFalse(isDuplicatedCreated);

        List<Filter> filterList = filePersistor.findAll();
        boolean isFilterFound = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(FILTER_NAME)
                    && filter.getFilterData().equalsIgnoreCase(FILTER_DATA)) {
                isFilterFound = true;
                break;
            }
        }
        Assertions.assertTrue(isFilterFound, "Filter " + FILTER_NAME + " should be in the list, but was not found");
    }
}
