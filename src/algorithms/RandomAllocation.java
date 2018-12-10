package algorithms;

import objects.Group;
import objects.Person;
import objects.Task;

import java.util.*;

public class RandomAllocation extends AllocationAlgorithm {
    private String name;

    public RandomAllocation(String algName) {
        super(algName);
    }

    protected Map<Person, List<Task>> determineAllocations(Group group) {
        if (group.getPersonTasksMap() == null || group.getPersonTasksMap().isEmpty() || group.getUnallocatedTasks().isEmpty()) {
            return null;
        }
        List<Person> personList = new ArrayList(group.getPersonTasksMap().keySet());
        Map<Person, List<Task>> personAllocationsMap = new HashMap();
        Random rand = new Random();
        for (Task task : group.getUnallocatedTasks()) {
            Person person = personList.get(rand.nextInt(personList.size()));
            if (!personAllocationsMap.containsKey(person)) {
                personAllocationsMap.put(person, new ArrayList());
            }
            personAllocationsMap.get(person).add(task);
        }
        return personAllocationsMap;
    }
}