package org.log.ports.logfile;

import java.util.List;

public interface LogFileOpener {

    List<String> openFile(String fileName);

}
