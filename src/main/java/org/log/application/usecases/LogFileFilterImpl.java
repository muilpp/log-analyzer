package org.log.application.usecases;

import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.log.domain.ports.logfile.LogFileFilter;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogFileFilterImpl implements LogFileFilter {
    private Logger logger = LogManager.getLogger(LogFileFilterImpl.class);

    @Override
    public List<Text> filterListBy(List<Text> originalList, List<String> wordsToInclude, List<String> wordsToExclude) {
        LogFilterPredicate includeFilterPredicate = new LogFilterPredicate(wordsToInclude, true);
        LogFilterPredicate excludeFilterPredicate = new LogFilterPredicate(wordsToExclude, false);

        logger.info("Words to include: " + wordsToInclude.toString());
        logger.info("Words to exclude: " + wordsToExclude.toString());

        if (wordsToInclude.isEmpty()) {
            return originalList.stream().filter(excludeFilterPredicate).collect(Collectors.toList());
        } else if (wordsToExclude.isEmpty()) {
            return originalList.stream().filter(includeFilterPredicate).collect(Collectors.toList());
        } else {
            return originalList.stream().filter(includeFilterPredicate).filter(excludeFilterPredicate).collect(Collectors.toList());
        }
    }

    @Override
    public List<Text> removeLogsWithoutTimestamp(List<Text> list) {
        return list.stream().filter(new TimestampLogPredicate()).collect(Collectors.toList());
    }

    private static class LogFilterPredicate implements Predicate<Text> {
        private final List<String> filterList;
        private final boolean isFilterInclude;

        protected LogFilterPredicate(List<String> wordsToFilter, boolean isFilterInclude) {
            this.filterList = wordsToFilter;
            this.isFilterInclude = isFilterInclude;
        }

        public boolean test(Text strings) {
            for (String filter : filterList) {
                if (strings.getText().toLowerCase().contains(filter.toLowerCase())) {
                    return isFilterInclude;
                }
            }
            return !isFilterInclude;
        }
    }

    public static class TimestampLogPredicate implements Predicate<Text> {
        @Override
        public boolean test(Text logText) {
            String log = logText.getText();
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
