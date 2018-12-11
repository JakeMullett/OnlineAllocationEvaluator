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
public class OnlineBatch extends AllocationAlgorithm {

    public OnlineBatch() {
        super("Online Batch");
    }

    @Override
    protected Map<Person, List<Task>> determineAllocations(Group group) {
        List<Person> personList = new ArrayList<>(group.getPersonTasksMap().keySet());
        List<Task> unallocatedTasks = group.getUnallocatedTasks();
        IMIP mip = new MIP();
        mip.setObjectiveMax(false);
        Variable[][] x = new Variable[personList.size()][unallocatedTasks.size()];
        Variable[][] x0 = new Variable[personList.size()][unallocatedTasks.size()];
        Variable[][] x1 = new Variable[personList.size()][unallocatedTasks.size()];
        double[][] v = new double[personList.size()][unallocatedTasks.size()];
        for (int i = 0; i < personList.size(); i++) {
            for (int l = 0; l < unallocatedTasks.size(); l++) {
                x[i][l] = new Variable("x" + i + "," + l, VarType.DOUBLE, 0, 1);
                x0[i][l] = new Variable("x0" + i + "," + l, VarType.BOOLEAN, 0, 1);
                x1[i][l] = new Variable("x1" + i + "," + l, VarType.BOOLEAN, 0, 1);
                v[i][l] = personList.get(i).getPreference(unallocatedTasks.get(l).getType());
                mip.add(x[i][l]);
                mip.add(x0[i][l]);
                mip.add(x1[i][l]);
                mip.addObjectiveTerm(1, x[i][l]);
            }
        }
        for (int i = 0; i < personList.size(); i++) {
            for (int j = 0; j < personList.size(); j++) {
                Constraint constraint = new Constraint(CompareType.GEQ, 0);
                for (int l = 0; l < unallocatedTasks.size(); l++) {
                    constraint.addTerm(-1 * v[i][l], x[i][l]);
                    constraint.addTerm(v[i][l], x[j][l]);
                }
                mip.add(constraint);
            }
        }
        for (int l = 0; i < unallocatedTasks.size(); l++) {
            Constraint constraint = new Constraint(CompareType.EQ, 1);
            for (int i = 0; i < personList.size(); i++) {
                constraint.addTerm(1, x[i][l]);
            }
            mip.add(constraint);
        }
        for (int i = 0; i < personList.size(); i++) {
            for (int l = 0; l < unallocatedTasks.size(); l++) {
                Constraint c0 = new Constraint(CompareType.LEQ, 0);
                Constraint c1 = new Constraint(CompareType.LEQ, 1);
                c0.addTerm(1, x0[i][l]);
                c0.addTerm(-1, x[i][l]);
                c1.addTerm(1, x[i][l]);
                c1.addTerm(1, x1[i][l]);
                mip.add(c0);
                mip.add(c1);
            }
        }
        for (int i = 0; i < personList.size(); i++) {
            Constraint constraint = new Constraint(CompareType.GEQ, unallocatedTasks.size() - 2);
            for (int l = 0; l < unallocatedTasks.size(); l++) {
                constraint.addTerm(1, x0[i][l]);
                constraint.addTerm(1, x1[i][l]);
            }
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
