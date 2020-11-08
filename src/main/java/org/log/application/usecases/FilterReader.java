package org.log.application.usecases;

import org.log.domain.entities.Filter;
import org.log.domain.ports.filter.FilterRepository;

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
