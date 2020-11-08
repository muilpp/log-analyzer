package org.log.usecases;

import org.log.ports.filter.FilterRepository;

public class FilterEraser {
    private final FilterRepository filterRepository;

    public FilterEraser(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    public boolean delete(String filterName) {
        return filterRepository.delete(filterName);
    }
}
