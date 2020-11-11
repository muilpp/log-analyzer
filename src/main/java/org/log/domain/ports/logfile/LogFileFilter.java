package org.log.domain.ports.logfile;

import java.util.List;

public interface LogFileFilter {

    List<String> filterListBy(List<String> originalList, List<String> wordsToInclude, List<String> wordsToExclude);
}
