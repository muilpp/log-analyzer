package org.log.persistance;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.log.entities.Filter;
import org.log.ports.filter.FilterRepository;

import java.io.BufferedWriter;
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
                String filterName = csvRecord.get(0);
                String filterData = csvRecord.get(1);

//                System.out.println("Record No - " + csvRecord.getRecordNumber());
//                System.out.println("---------------");
//                System.out.println("Name : " + filterName);
//                System.out.println("Data : " + filterData);
//                System.out.println("---------------\n\n");

                filterList.add(new Filter(filterName, filterData));
            }
        } catch (IOException e) {
            System.out.println("Error creating filter: " + e);
        }

        return filterList;
    }

    @Override
    public boolean create(String filterName, String filterData) {
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
    public boolean delete(String filterName) {
        return false;
    }
}
