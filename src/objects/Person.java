package objects;

import java.util.Map;

public class Person {
    private String name;
    private Map<TaskType, Double> preferences;

    public Person() {}

    public Person(String newName, Map<TaskType, Double> prefs) {
        name = newName;
        preferences = prefs;
    }

    public String getName() {
        return name;
    }

    public Map<TaskType, Double> getPreferences() {
        return preferences;
    }

    public String toString() {return name;}

    public Double getPreference(TaskType task) {
        return preferences.get(task);
    }

}
