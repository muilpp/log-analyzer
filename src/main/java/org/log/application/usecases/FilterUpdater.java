package org.log.application.usecases;


import org.log.domain.ports.filter.FilterRepository;

public class FilterUpdater {
    private final FilterRepository filterRepository;

    public FilterUpdater(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    public boolean update(String filterName, String filterData) {
        return filterRepository.update(filterName, filterData);
    }
}
