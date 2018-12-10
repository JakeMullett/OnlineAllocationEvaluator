package objects;

import tools.CSVReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Group {

    private Map<Person, List<Task>> allocatedTasks;
    private List<Task> unallocatedTasks;

    public Group(String preferencesCSV) {
        CSVReader.digestCSV(preferencesCSV);
    }

    public void addTasks(List<Task> newTasks) {
        unallocatedTasks.addAll(newTasks);
    }

    public void updateAllocations(Map<Person, List<Task>> newAllocations) {
        for (Person p : newAllocations.keySet()) {
            List<Task> tasks = allocatedTasks.getOrDefault(p, new ArrayList<>());
            tasks.addAll(newAllocations.getOrDefault(p, new ArrayList<>()));
        }
    }

    public List<Task> getUnallocatedTasks() {
        return unallocatedTasks;
    }

    public Map<Person, List<Task>> getAllocatedTasks() {
        return allocatedTasks;
    }

}
