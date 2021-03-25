package org.log.domain.ports.logfile;

import javafx.scene.text.Text;

import java.util.List;

public interface LogFileFilter {

    List<Text> filterListBy(List<Text> originalList, List<String> wordsToInclude, List<String> wordsToExclude);
    List<Text> removeLogsWithoutTimestamp(List<Text> originalList);
}
