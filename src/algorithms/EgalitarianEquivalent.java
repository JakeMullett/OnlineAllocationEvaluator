package algorithms;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.*;
import objects.Group;
import objects.Person;
import objects.Task;

import java.util.*;

/**
 * Finds nearly envy-free integral allocation.
 */
public class EgalitarianEquivalent extends AllocationAlgorithm {

    public EgalitarianEquivalent() {
        super("Egalitarian Equivalent Batch");
    }

    @Override
    protected Map<Person, List<Task>> determineAllocations(Group group) {
        List<Person> personList = new ArrayList<>(group.getPersonTasksMap().keySet());
        List<Task> unallocatedTasks = group.getUnallocatedTasks();
        IMIP mip = new MIP();
        mip.setObjectiveMax(false);
        Variable u = new Variable("u", VarType.DOUBLE, 0, MIP.MAX_VALUE);
        mip.add(u);
        mip.addObjectiveTerm(1, u);
        Variable[][] x = new Variable[personList.size()][unallocatedTasks.size()];
        double[][] v = new double[personList.size()][unallocatedTasks.size()];
        for (int i = 0; i < personList.size(); i++) {
            for (int l = 0; l < unallocatedTasks.size(); l++) {
                x[i][l] = new Variable("x" + i + "," + l, VarType.DOUBLE, 0, 1);
                v[i][l] = personList.get(i).getPreference(unallocatedTasks.get(l).getType());
                mip.add(x[i][l]);
                mip.addObjectiveTerm(1, x[i][l]);
            }
        }
        for (int l = 0; l < unallocatedTasks.size(); l++) {
            Constraint constraint = new Constraint(CompareType.EQ, 1);
            for (int i = 0; i < personList.size(); i++) {
                constraint.addTerm(1, x[i][l]);
            }
            mip.add(constraint);
        }
        for (int i = 0; i < personList.size(); i++) {
            Constraint constraint = new Constraint(CompareType.EQ, 0);
            double totalDisutility = 0;
            for (int l = 0; l < unallocatedTasks.size(); l++) {
                constraint.addTerm(v[i][l], x[i][l]);
                totalDisutility -= v[i][l];
            }
            constraint.addTerm(totalDisutility, u);
            mip.add(constraint);
        }
        ;
        IMIPResult mipResult = new SolverClient().solve(mip);
        Random rand = new Random();
        Map<Person, List<Task>> personAllocationMap = new HashMap<>();
        for (int l = 0; l < unallocatedTasks.size(); l++) {
            List<Person> fractionalPersons = new ArrayList<>();
            for (int i = 0; i < personList.size(); i++) {
                double val = mipResult.getValue(x[i][l]);
                if (val > 0)
                    fractionalPersons.add(personList.get(i));
            }
            int index = rand.nextInt(fractionalPersons.size());
            Person chosenPerson = fractionalPersons.get(index);
            if (!personAllocationMap.containsKey(chosenPerson)) {
                personAllocationMap.put(chosenPerson, new ArrayList<>());
            }
            personAllocationMap.get(chosenPerson).add(unallocatedTasks.get(l));
        }
        return personAllocationMap;
    }
}
