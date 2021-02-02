package org.log.application.usecases;

import org.log.domain.ports.logfile.LogFileFilter;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogFileFilterImpl implements LogFileFilter {

    @Override
    public List<String> filterListBy(List<String> originalList, List<String> wordsToInclude, List<String> wordsToExclude) {
        LogFilterPredicate includeFilterPredicate = new LogFilterPredicate(wordsToInclude, true);
        LogFilterPredicate excludeFilterPredicate = new LogFilterPredicate(wordsToExclude, false);

        System.out.println("Words to include:");
        wordsToInclude.forEach(System.out::println);

        System.out.println("Words to exclude:");
        wordsToExclude.forEach(System.out::println);

        if (wordsToInclude.isEmpty()) {
            return originalList.stream().filter(excludeFilterPredicate).collect(Collectors.toList());
        } else if (wordsToExclude.isEmpty()) {
            return originalList.stream().filter(includeFilterPredicate).collect(Collectors.toList());
        } else {
            return originalList.stream().filter(includeFilterPredicate).filter(excludeFilterPredicate).collect(Collectors.toList());
        }
    }

    @Override
    public List<String> removeLogsWithoutTimestamp(List<String> list) {
        return list.stream().filter(new TimestampLogPredicate()).collect(Collectors.toList());
    }

    private static class LogFilterPredicate implements Predicate<String> {
        private final List<String> filterList;
        private final boolean isFilterInclude;

        protected LogFilterPredicate(List<String> wordsToFilter, boolean isFilterInclude) {
            this.filterList = wordsToFilter;
            this.isFilterInclude = isFilterInclude;
        }

        public boolean test(String strings) {
            for (String filter : filterList) {
                if (strings.toLowerCase().contains(filter.toLowerCase())) {
                    return isFilterInclude;
                }
            }
            return !isFilterInclude;
        }
    }

    public static class TimestampLogPredicate implements Predicate<String> {
        @Override
        public boolean test(String log) {
            log = log.trim();
            if (log == null || log.isBlank() || log.isEmpty() || log.length() < 10) {
                return false;
            }

            Pattern p = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\d");
            Matcher m = p.matcher(log.trim().substring(0,10));

            return m.find();
        }
    }
}
