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
 * Applies online allocation for one item at a time, using potential minimization function.
 */
public class OnlineSingleAllocation extends AllocationAlgorithm {
    private int T;
    private int n;
    private double lambda;
    private double s;
    private double C;

    public OnlineSingleAllocation(int maxTimeSteps, int numPeople) {
        super("Online Single");
        T = maxTimeSteps;
        n = numPeople;
        lambda = 10 * Math.sqrt(T * Math.log(n) / n);
        s = Math.sqrt(2 * Math.log(1 + (n * Math.log(n) / T)));
        C = 1 + (Math.exp(s) + Math.exp(-1 * s) - 2) / n;
    }

    @Override
    protected Map<Person, List<Task>> determineAllocations(Group group) {
        List<Person> personList = new ArrayList<>(group.getPersonTasksMap().keySet());
        int t = 0;
        for (Person p : group.getPersonTasksMap().keySet()) {
            t += group.getPersonTasksMap().get(p).size();
        }
        if (t > T) {
            T *= 2;
        }
        Map<Person, List<Task>> personAllocationMap = new HashMap<>();
        double[][] envyGraph = MetricsCalculator.calculateEnvyGraph(group);
        for (Task task : group.getUnallocatedTasks()) {
            int bestPerson = -1;
            double bestPotential = -1;
            double[] disutilities = new double[personList.size()];
            for (int i = 0; i < personList.size(); i++) {
                disutilities[i] = personList.get(i).getPreference(task.getType());
            }
            t += 1;
            if (t > T) {
                T *= 2;
            }
            for (int i = 0; i < personList.size(); i++) {
                for (int j = 0; j < personList.size(); j++) {
                    envyGraph[i][j] += disutilities[i];
                    envyGraph[j][i] -= disutilities[j];
                }
                double potential = calcPotential(envyGraph, t);
                if (bestPerson == -1 || potential < bestPotential) {
                    bestPerson = i;
                    bestPotential = potential;
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

    private double calcPotential(double[][] envyGraph, int t) {
        double potential = 0;
        for (int i = 0; i < envyGraph.length; i++) {
            for (int j = 0; j < envyGraph[i].length; j++) {
                if (i == j) {
                    continue;
                }
                double pairwisePotential = Math.pow(C, T - t) * Math.exp(s * (envyGraph[i][j] - lambda));
                potential += pairwisePotential;
            }
        }
        return potential;
    }
}
