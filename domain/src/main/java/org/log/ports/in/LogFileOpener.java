package org.log.ports.in;

import java.util.List;

public interface LogFileOpener {

    List<String> openFile(String fileName);

}
