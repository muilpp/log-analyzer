package org.log.ports.filter;

import org.log.entities.Filter;

import java.util.List;

public interface FilterRepository {
    List<Filter> findAll();
    boolean create(String filterName, String filterData);
    boolean update(String filterName, String filterData);
    boolean delete(String filterName);
}
