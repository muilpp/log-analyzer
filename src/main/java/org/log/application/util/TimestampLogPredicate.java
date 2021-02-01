package org.log.application.util;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimestampLogPredicate implements Predicate<String> {
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
