package org.log.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.log.domain.entities.Filter;
import org.log.infrastructure.FilePersistor;

import java.util.List;

public class LogFileUpdaterTest {
    private final FilePersistor filePersistor = new FilePersistor();
    private final String filterName = "testFilterName";
    private final String filterData = "testFilterData";

    @AfterEach
    public void deleteFilter() {
        filePersistor.delete(filterName);
    }

    @Test
    public void filterIsUpdatedCorrectly() {
        filePersistor.create(filterName, filterData);

        boolean isFilterUpdated = filePersistor.update(filterName, filterData+"updated");
        Assertions.assertTrue(isFilterUpdated);

        List<Filter> filterList = filePersistor.findAll();
        boolean isFilterFound = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(filterName)
                    && filter.getFilterData().equalsIgnoreCase(filterData+"updated")) {
                isFilterFound = true;
                break;
            }
        }
        Assertions.assertTrue(isFilterFound, "Filter " + filterName + " should have been updated, but it was not");
    }
}
