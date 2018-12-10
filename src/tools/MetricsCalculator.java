package tools;

import objects.Person;
import objects.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MetricsCalculator {

    // Returns the envy that person 1 has on person 2's bundle versus theirs.
    public static double calculateEnvy(Person p1, Person p2, HashMap<Person, List<Task>> allocations) {
        return calculateBothEnvies(p1, p2, allocations)[0];
    }

    public static double[] calculateBothEnvies(Person p1, Person p2, HashMap<Person, List<Task>> allocations) {
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
        return new double[] {p2Disutil1 - p1Disutil1, p2Disutil2 - p1Disutil2};
    }

    public static double calculateTotalEnvy(HashMap<Person, List<Task>> allocations) {
        Person[] people = getPeople(allocations);
        double totalEnvy = 0.0;
        for (int i = 0; i<people.length-1; i++) {
            for (int j = i+i; j<people.length; j++) {
                Person p1 = people[i], p2 = people[j];
                double[] bothEnvies = calculateBothEnvies(p1, p2, allocations);
                totalEnvy += bothEnvies[0] + bothEnvies[1];
            }
        }
        return totalEnvy;
    }

    public static double calculateTotalDisutility(HashMap<Person, List<Task>> allocations) {
        Person[] people = getPeople(allocations);
        double disUtility = 0.0;
        for (Person p : people) {
            for (Task t : allocations.getOrDefault(p, new ArrayList<>())) {
                disUtility += p.getPreference(t.getType());
            }
        }
        return disUtility;
    }


    private static Person[] getPeople(HashMap<Person, List<Task>> allocations) {
        Set<Person> peopleSet = allocations.keySet();
        int len = peopleSet.size();
        return peopleSet.toArray(new Person[len]);
    }
}
