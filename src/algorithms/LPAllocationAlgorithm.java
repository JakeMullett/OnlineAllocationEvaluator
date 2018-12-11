package algorithms;

import objects.Group;
import objects.Person;
import objects.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses a specified linear programming method to solve the fractional allocation, and then rounds randomly in a fair manner based on that allocation.
 */
public class LPAllocationAlgorithm extends AllocationAlgorithm {
    private LPSolver lpSolver;

    public LPAllocationAlgorithm(LPSolver lpSolver) {
        super("LP - " + lpSolver.getName());
        lpSolver = lpSolver;
    }

    @Override
    protected Map<Person, List<Task>> determineAllocations(Group group) {
        List<Person> personList = new ArrayList<>(group.getPersonTasksMap().keySet());
        List<Task> unallocatedTasks = group.getUnallocatedTasks();
        lpSolver.buildMIP(personList, unallocatedTasks);
        double[][] fractionalAllocations = lpSolver.solveFractionalAllocations();
        int[] integralAllocations = integralAllocations(fractionalAllocations);

        Map<Person, List<Task>> personAllocationsMap = new HashMap<>();
        for (int i = 0; i < unallocatedTasks.size(); i++) {
            Task t = unallocatedTasks.get(i);
            Person p = personList.get(integralAllocations[i]);
            if(!personAllocationsMap.containsKey(p)) {
                personAllocationsMap.put(p, new ArrayList<>());
            }
            personAllocationsMap.get(p).add(t);
        }
        return personAllocationsMap;
    }

    protected int[] integralAllocations(double[][] fractionalAllocations) {
        int[] integralAllocations = new int[fractionalAllocations[0].length];
        for (int l = 0; l < fractionalAllocations[0].length; l++) {
            double random = Math.random();
            double sum = 0;
            for (int i = 0; i < fractionalAllocations.length; i++) {
                sum += fractionalAllocations[i][l];
                if (sum > random) {
                    integralAllocations[l] = i;
                }
            }
        }
        return integralAllocations;
    }

}
