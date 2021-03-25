package org.log.domain.ports.logfile;

import javafx.scene.text.Text;

import java.util.List;

public interface LogFileOpener {

    List<Text> openFile(String fileName);

}
