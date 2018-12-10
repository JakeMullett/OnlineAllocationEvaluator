package objects;

import java.util.Map;

public class Person {
    private String name;
    private Map<TaskType, Double> preferences;

    public Person(String newName, Map<TaskType, Double> prefs) {
        name = newName;
        preferences = prefs;
    }

    public String getName() {
        return name;
    }

    public Double getPreference(TaskType task) {
        return preferences.get(task);
    }

}
