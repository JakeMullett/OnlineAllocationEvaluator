package algorithms;

import objects.Group;
import objects.Person;
import objects.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AllocationAlgorithm {
    private String name;

    public AllocationAlgorithm(String algName) {
        this.name = name;
    }

    public boolean assignTasks(Group group) {
        Map<Person, List<Task>> personAllocationsMap = determineAllocations(group);
        if (personAllocationsMap == null) {
            personAllocationsMap = new HashMap();
        }
        group.updateAllocations(personAllocationsMap);
        return group.getUnallocatedTasks().isEmpty();
    }

    protected abstract Map<Person, List<Task>> determineAllocations(Group group);

}
