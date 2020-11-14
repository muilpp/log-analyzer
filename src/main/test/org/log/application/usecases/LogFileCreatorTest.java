package org.log.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.log.domain.entities.Filter;
import org.log.infrastructure.FilePersistor;

import java.util.List;

public class LogFileCreatorTest {

    private final FilePersistor filePersistor = new FilePersistor();
    private final String filterName = "testFilterName";
    private final String filterData = "testFilterData";

    @AfterEach
    public void deleteFilter() {
        filePersistor.delete(filterName);
    }

    @Test
    public void newFilterIsCreated() {
        boolean isCreated = filePersistor.create(filterName, filterData);
        Assertions.assertTrue(isCreated);

        List<Filter> filterList = filePersistor.findAll();
        boolean isNewFilterFound = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(filterName)
                    && filter.getFilterData().equalsIgnoreCase(filterData)) {
                isNewFilterFound = true;
                break;
            }
        }
        Assertions.assertTrue(isNewFilterFound, "Filter " + filterName + " should have been created, but it's not in the list");
    }

    @Test
    public void filterIsNotCreatedIfAlreadyExists() {
        filePersistor.create(filterName, filterData);

        boolean isCreated = filePersistor.create(filterName, filterData);
        Assertions.assertFalse(isCreated);

        List<Filter> filterList = filePersistor.findAll();
        boolean isFilterFound = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(filterName)
                    && filter.getFilterData().equalsIgnoreCase(filterData)) {
                isFilterFound = true;
                break;
            }
        }
        Assertions.assertTrue(isFilterFound, "Filter " + filterName + " should be in the list, but was not found");
    }
}
