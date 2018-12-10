package objects;

import java.util.Map;

// this may be overly-OOP but made this its own class for now
public class Preferences {
    private Map<TaskType, Double> preferences;

    public Preferences(Map<TaskType, Double> pmap) {
        preferences = pmap;
    }

    public Double getPreference(TaskType task) {
        return preferences.get(task);
    }

}
