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

*Skip to Usage if you are not interested in developing anything in this repo and only need to use it.*

Clone the repository and run maven inside the directory to generate the jar:

```bash
mvn package
```
### Usage

Make sure to have the file *filters.csv* in the same directory as the jar file. Then run the artifact:
```bash
java -jar LogAnalyzer-X.X.jar
```

### Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License
[Apache License](https://github.com/muilpp/log-analyzer/blob/main/LICENSE)
