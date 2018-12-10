package objects;

import tools.CSVReader;

import java.util.List;
import java.util.Map;

public class Group {
    private Map<Person, List<Task>> personTasksMap;
    private List<Task> unallocatedTasks;
    private String groupName;

    public Group(String preferencesCSV) {
        CSVReader.digestCSV(preferencesCSV);
    }

    public void addTasks(List<Task> newTasks) {
        unallocatedTasks.addAll(newTasks);
    }

    public void updateAllocations(Map<Person, List<Task>> personAllocationsMap) {
        for (Person p : personTasksMap.keySet()) {
            if (!personAllocationsMap.containsKey(p)) {
                continue;
            }
            unallocatedTasks.removeAll(personAllocationsMap.get(p));
            personTasksMap.get(p).addAll(personAllocationsMap.get(p));
        }
    }

    public Map<Person, List<Task>> getPersonTasksMap() {
        return personTasksMap;
    }

    public List<Task> getUnallocatedTasks() {
        return unallocatedTasks;
    }


    public String getGroupName() {
        return groupName;
    }
}
