package algorithms;

import objects.Group;
import objects.Person;
import objects.Task;

import java.util.*;

public class RandomAllocation extends AllocationAlgorithm {
    public RandomAllocation() {
        super("Random");
    }

    @Override
    protected Map<Person, List<Task>> determineAllocations(Group group) {
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