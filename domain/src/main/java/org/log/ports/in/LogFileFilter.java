package org.log.ports.in;

import java.util.List;

public interface LogFileFilter {

    List<String> filterListBy(List<String> originalList, List<String> wordsToFilter);
}
