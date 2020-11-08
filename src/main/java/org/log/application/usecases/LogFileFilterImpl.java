package org.log.application.usecases;

import org.log.domain.ports.logfile.LogFileFilter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogFileFilterImpl implements LogFileFilter {

    @Override
    public List<String> filterListBy(List<String> originalList, List<String> wordsToFilter) {
        LogFilterPredicate logFilterPredicate = new LogFilterPredicate(wordsToFilter);

        System.out.println("Words to filter:");
        wordsToFilter.forEach(System.out::println);

        return originalList.stream().filter(logFilterPredicate).collect(Collectors.toList());
    }

    private static class LogFilterPredicate implements Predicate<String> {
        private final List<String> filterList;

        protected LogFilterPredicate(List<String> filterList) {
            this.filterList = filterList;
        }

        public boolean test(String strings) {
            for (String filter : filterList) {
                if (strings.toLowerCase().contains(filter.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }
    }
}
