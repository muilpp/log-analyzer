package org.log.application.usecases;


import org.log.domain.ports.filter.FilterRepository;

public class FilterCreator {

    private final FilterRepository filterRepository;

    public FilterCreator(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    public boolean create(String filterName, String filterData) {
        return filterRepository.create(filterName, filterData);
    }
}
