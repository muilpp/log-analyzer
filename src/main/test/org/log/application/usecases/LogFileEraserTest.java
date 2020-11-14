package org.log.application.usecases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.log.domain.entities.Filter;
import org.log.infrastructure.FilePersistor;

import java.util.List;

public class LogFileEraserTest {
    private final FilePersistor filePersistor = new FilePersistor();
    private final String filterName = "testFilterName";
    private final String filterData = "testFilterData";

    @Test
    public void filterIsDeletedCorrectly() {
        filePersistor.create(filterName, filterData);

        boolean isFilterDeleted = filePersistor.delete(filterName);
        Assertions.assertTrue(isFilterDeleted);

        List<Filter> filterList = filePersistor.findAll();
        boolean isFilterFound = false;
        for (Filter filter : filterList) {
            if (filter.getFilterName().equalsIgnoreCase(filterName)) {
                isFilterFound = true;
                break;
            }
        }
        Assertions.assertFalse(isFilterFound, "Filter " + filterName + " should have been deleted, but it's still present");
    }
}