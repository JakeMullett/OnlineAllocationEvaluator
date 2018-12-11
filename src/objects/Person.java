package objects;

import java.util.Map;

public class Person {
    private String name;
    private Map<TaskType, Double> taskTypeDisutilityMap;

    public Person() {}

    public Person(String newName, Map<TaskType, Double> prefs) {
        name = newName;
        taskTypeDisutilityMap = prefs;
    }

    public String getName() {
        return name;
    }

    public Map<TaskType, Double> getPreferences() {
        return preferences;
    }

    public String toString() {return name;}

    public Double getPreference(TaskType task) {
        return taskTypeDisutilityMap.get(task);
    }

}
