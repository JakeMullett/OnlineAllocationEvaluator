package tools;

import com.opencsv.CSVReader;
import objects.Person;
import objects.Task;
import objects.TaskType;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FormParser {

    private static final int NAME_COL = 0; // for reading from a google form
    private static final int DATE_COL = 0; // for getting tasks from a csv
    private static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static List<Person> getPreferencesFromCSV(String filename) throws IOException {
        ArrayList<Person> preferences = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(filename));
        String[] columnNames = reader.readNext();
        Map<TaskType, Integer> columnMapping = mapColumnsToTaskTypes(columnNames);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            String name = nextLine[NAME_COL];
            HashMap<TaskType, Double> personPrefs = new HashMap<>();
            for (Map.Entry<TaskType, Integer> col : columnMapping.entrySet()) {
                personPrefs.put(col.getKey(), Double.parseDouble(nextLine[col.getValue()]));
            }
            preferences.add(new Person(name, personPrefs));
        }
        return preferences;
    }

    public static List<Task> getAllTasksFromCSV(String filename)  throws IOException, ParseException {
        ArrayList<Task> tasks = new ArrayList<>();
        for (List<Task> taskList : getBatchedTasksfromCSV(filename)) {
            tasks.addAll(taskList);
        }
        return tasks;
    }

    public static List<List<Task>> getBatchedTasksfromCSV(String filename) throws IOException, ParseException {
        List<List<Task>> tasks = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(filename));
        String[] columnNames = reader.readNext();
        Map<TaskType, Integer> columnMapping = mapColumnsToTaskTypes(columnNames);
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            ArrayList<Task> batchOfTasks = new ArrayList<>();
            Date date = CSV_DATE_FORMAT.parse(nextLine[DATE_COL]);
            for (Map.Entry<TaskType, Integer> col : columnMapping.entrySet()) {
                int numTask = Integer.parseInt(nextLine[col.getValue()]);
                for (int i = 0; i < numTask; i++)
                    batchOfTasks.add(new Task(col.getKey(), date));
            }
            tasks.add(batchOfTasks);
        }
        return tasks;
    }

    // this is horrible but I wanted to make it dynamic and easy to add new tasktypes in the enum
    private static Map<TaskType, Integer> mapColumnsToTaskTypes(String[] columnHeaders) {
        HashMap<TaskType, Integer> columnMapping = new HashMap<>();
        HashMap<String, TaskType> colNameToTType =  new HashMap<>();
        List<TaskType> taskTypes = Arrays.asList(TaskType.values());
        for (TaskType t : taskTypes)
            colNameToTType.put(t.getFormName(), t);

        for (int i = 0; i < columnHeaders.length; i++) {
            String cellName = columnHeaders[i];
            if (colNameToTType.containsKey(cellName)) {
                columnMapping.put(colNameToTType.get(cellName), i);
            }
        }
        return columnMapping;
    }
}
