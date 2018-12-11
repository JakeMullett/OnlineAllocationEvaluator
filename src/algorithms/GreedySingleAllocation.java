package algorithms;

import objects.Group;
import objects.Person;
import objects.Task;
import tools.MetricsCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Greedy algorithm that looks at unassigned tasks one at a time, and assigns each in the way that minimizes the maximum envy among all members in the group.
 */
public class GreedySingleAllocation extends AllocationAlgorithm {

    public GreedySingleAllocation() {
        super("Greedy Single");
    }

    @Override
    protected Map<Person, List<Task>> determineAllocations(Group group) {
        List<Person> personList = new ArrayList(group.getPersonTasksMap().keySet());
        Map<Person, List<Task>> personAllocationMap = new HashMap();
        double[][] envyGraph = MetricsCalculator.calculateEnvyGraph(group);
        for (Task task : group.getUnallocatedTasks()) {
            int bestPerson = -1;
            double bestMaxEnvy = -1;
            double[] disutilities = new double[personList.size()];
            for (int i = 0; i < personList.size(); i++) {
                disutilities[i] = personList.get(i).getPreference(task.getType());
            }
            for (int i = 0; i < personList.size(); i++) {
                for (int j = 0; j < personList.size(); j++) {
                    envyGraph[i][j] += disutilities[i];
                    envyGraph[j][i] -= disutilities[j];
                }
                double maxEnvy = getMaxEnvy(envyGraph);
                if (bestPerson == -1 || maxEnvy < bestMaxEnvy) {
                    bestPerson = i;
                    bestMaxEnvy = maxEnvy;
                }
                for (int j = 0; j < personList.size(); j++) {
                    envyGraph[i][j] -= disutilities[i];
                    envyGraph[j][i] += disutilities[j];
                }
            }
            for (int j = 0; j < personList.size(); j++) {
                envyGraph[bestPerson][j] += disutilities[bestPerson];
                envyGraph[j][bestPerson] -= disutilities[j];
            }
            Person person = personList.get(bestPerson);
            if (!personAllocationMap.containsKey(person)) {
                personAllocationMap.put(person, new ArrayList<>());
            }
            personAllocationMap.get(person).add(task);
        }
        return personAllocationMap;
    }

    private double getMaxEnvy(double[][] envyGraph) {
        double max = envyGraph[0][0];
        for (int i = 0; i < envyGraph.length; i++) {
            for (int j = 0; j < envyGraph[i].length; j++) {
                max = max < envyGraph[i][j] ? max : envyGraph[i][j];
            }
        }
        return max;
    }
}
