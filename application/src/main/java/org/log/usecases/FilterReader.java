package org.log.usecases;

import org.log.entities.Filter;
import org.log.ports.filter.FilterRepository;

import java.util.List;

public class FilterReader {
    private final FilterRepository filterRepository;

    public FilterReader(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    public List<Filter> read() {
        return filterRepository.findAll();
    }
}
