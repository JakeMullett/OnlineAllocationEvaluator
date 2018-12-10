package objects;

import java.util.Map;

public class Preferences {
    private Map<TaskType, Double> preferences;

    public Double getPreference(TaskType task) {
        return preferences.get(task);
    }


}
