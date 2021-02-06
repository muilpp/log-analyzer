## Log Analyzer

Java desktop application to filter and analyze logs. Developed with JavaFX.

### Features
- Filter (include or exclude certain keywords). Separate with a pipe (|), to use multiple keywords.

- Create your own filters in the menu and use them with a simple click. *It will appear the next time you open a tab*

- Split screen, filtered file appears above, original file below. 

- Ctrl+F allows you to search in either of the panels.

- Sort logs by date and/or exclude lines without timestamp (lines starting with format **yyyy-dd-mm**).

- Multi tab window.

- Export the filtered panel to a file.


![alt text](https://raw.githubusercontent.com/muilpp/log-analyzer/main/Screenshot.png)

### Installation

Clone the repository and then:

- #### On Linux:

  Open ```target/LogAnalyzer_X.X_all.deb ```

- #### On Mac & Windows: 
  ```java -jar target/LogAnalyzer-X.X.jar ```

*Make sure to have the file *filters.csv* in the same directory from where you run the jar file.*

### Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License
[Apache License](https://github.com/muilpp/log-analyzer/blob/main/LICENSE)
