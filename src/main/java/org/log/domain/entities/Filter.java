package org.log.domain.entities;

public class Filter {
    private final String filterName;
    private final String filterData;

    public Filter(String name, String dataToFilter) {
        this.filterName = name;
        this.filterData = dataToFilter;
    }

    public String getFilterName() {
        return filterName;
    }

    public String getFilterData() {
        return filterData;
    }
}
