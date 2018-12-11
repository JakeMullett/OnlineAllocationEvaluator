package objects;

import tools.FormParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    private Map<Person, List<Task>> personTasksMap;
    private List<Task> unallocatedTasks;
    private String groupName;

    public Group(String preferencesCSV, String name) throws IOException {
        personTasksMap = new HashMap<>();
        for (Person p : FormParser.getPreferencesFromCSV(preferencesCSV)) {
            personTasksMap.put(p, new ArrayList<>());
        }
        groupName = name;
        unallocatedTasks = new ArrayList<>();
    }

    public Group() {}

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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setPersonTasksMap(Map<Person, List<Task>> personTasksMap) {
        this.personTasksMap = personTasksMap;
    }

    public void setUnallocatedTasks(List<Task> unallocatedTasks) {
        this.unallocatedTasks = unallocatedTasks;
    }

    public String getGroupName() {
        return groupName;
    }
}
