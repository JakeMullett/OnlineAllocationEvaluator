package tools;

import objects.Group;
import objects.Person;
import objects.Task;

import java.util.*;

public class MetricsCalculator {

    // Returns the envy that person 1 has on person 2's bundle versus theirs.
    public static double calculateEnvy(Person p1, Person p2, Group group) {
        return calculateBothEnvies(p1, p2, group)[0];
    }

    public static double[] calculateBothEnvies(Person p1, Person p2, Group group) {
        Map<Person, List<Task>> allocations = group.getPersonTasksMap();
        List<Task> p1Tasks = allocations.getOrDefault(p1, new ArrayList<>());
        List<Task> p2Tasks = allocations.getOrDefault(p2, new ArrayList<>());
        double p1Disutil1 = 0.0, p2Disutil1 = 0.0, p1Disutil2 = 0.0, p2Disutil2 = 0.0;
        for (Task task : p1Tasks) {
            p1Disutil1 += p1.getPreference(task.getType());
            p1Disutil2 += p2.getPreference(task.getType());
        }
        for (Task task : p2Tasks) {
            p2Disutil2 += p1.getPreference(task.getType());
            p2Disutil2 += p2.getPreference(task.getType());
        }
        return new double[] {p1Disutil1 - p2Disutil1, p1Disutil2 - p2Disutil2};
    }

    public static double calculateTotalEnvy(Group group) {
        Map<Person, List<Task>> allocations = group.getPersonTasksMap();
        Person[] people = getPeople(allocations);
        double totalEnvy = 0.0;
        for (int i = 0; i<people.length-1; i++) {
            for (int j = i+i; j<people.length; j++) {
                Person p1 = people[i], p2 = people[j];
                double[] bothEnvies = calculateBothEnvies(p1, p2, group);
                totalEnvy += bothEnvies[0] + bothEnvies[1];
            }
        }
        return totalEnvy;
    }

    public static double calculateTotalDisutility(Group group) {
        Map<Person, List<Task>> allocations = group.getPersonTasksMap();
        Person[] people = getPeople(allocations);
        double disUtility = 0.0;
        for (Person p : people) {
            for (Task t : allocations.getOrDefault(p, new ArrayList<>())) {
                disUtility += p.getPreference(t.getType());
            }
        }
        return disUtility;
    }

    public static double[][] calculateEnvyGraph(Group group) {
        Map<Person, List<Task>> allocations = group.getPersonTasksMap();
        Person[] people = MetricsCalculator.getPeople(allocations);
        double[][] zValues = new double[people.length-1][people.length-1];
        for (int i = 0; i<people.length; i++) {
            for (int j = i; j<people.length; j++) {
                Person p1 = people[i], p2 = people[j];
                double[] bothEnvies = MetricsCalculator.calculateBothEnvies(p1, p2, group);
                zValues[i][j] = bothEnvies[0];
                zValues[j][i] = bothEnvies[1];
            }
        }
        return zValues;
    }

    public static Person[] getPeople(Map<Person, List<Task>> allocations) {
        Set<Person> peopleSet = allocations.keySet();
        int len = peopleSet.size();
        return peopleSet.toArray(new Person[len]);
    }
}
