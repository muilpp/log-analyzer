package org.log.infrastructure;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.log.domain.entities.Filter;
import org.log.domain.ports.filter.FilterRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FilePersistor implements FilterRepository {
    private static final String FILTERS_FILENAME = "filters.csv";

    @Override
    public List<Filter> findAll() {
        List<Filter> filterList = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(FILTERS_FILENAME));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {
                //if (csvRecord.size() == 2) {
                    String filterName = csvRecord.get(0);
                    String filterData = csvRecord.get(1);
                    filterList.add(new Filter(filterName, filterData));
                //}
            }
        } catch (IOException e) {
            System.out.println("Error fetching filters: " + e);
        }

        return filterList;
    }

    @Override
    public boolean create(String filterName, String filterData) {
        System.out.println("Create filter with name: " + filterName);

        boolean doesFilterAlreadyExists = findAll().stream().anyMatch(filter -> filter.getFilterName().equalsIgnoreCase(filterName));

        if (doesFilterAlreadyExists)
            return false;

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILTERS_FILENAME), APPEND, CREATE);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            csvPrinter.printRecord(filterName, filterData);
            csvPrinter.flush();

        } catch (IOException e) {
            System.out.println("Error creating filter: " + e);
            return false;
        }

        return true;
    }

    @Override
    public boolean update(String filterName, String filterData) {
        List<Filter> filterList = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(FILTERS_FILENAME));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {
                Filter filter;
                if (csvRecord.get(0).equalsIgnoreCase(filterName)) {
                    System.out.println("Found filter, edit with: " + filterData);
                    filter = new Filter(csvRecord.get(0), filterData);
                } else {
                    System.out.println("Filter not found yet...: " + csvRecord.get(0));
                    filter = new Filter(csvRecord.get(0), csvRecord.get(1));
                }
                filterList.add(filter);
            }
            return overrideFilterFile(filterList);
        } catch (IOException e) {
            System.out.println("Error updating filters: " + e);
        }
        return false;
    }

    private boolean overrideFilterFile(List<Filter> filterList) {
        try (FileWriter fileWriter = new FileWriter(FILTERS_FILENAME);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT)) {

            filterList.forEach(filter -> {
                try {
                    System.out.println("Adding to file: " + filter.getFilterName() + ": " + filter.getFilterData());
                    csvPrinter.printRecord(filter.getFilterName(), filter.getFilterData());
                    csvPrinter.flush();
                } catch (IOException e) {
                    System.out.println("Error saving to filter file: " + e);
                }
            });

            return true;
        } catch (IOException e) {
            System.out.println("Error overriding filter file: " + e);
            return false;
        }
    }

    @Override
    public boolean delete(String filterName) {
        final List<Filter> updatedFilterList = new ArrayList<>();

        findAll().forEach(filter -> {
            if (!filter.getFilterName().equalsIgnoreCase(filterName)) {
                updatedFilterList.add(filter);
            }
        });

        return overrideFilterFile(updatedFilterList);
    }
}
