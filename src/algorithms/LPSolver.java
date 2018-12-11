package algorithms;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.*;
import objects.Person;
import objects.Task;

import java.util.List;

/**
 * Uses linear programming to determine proper fractional allocations of items in a batch. Abstract class to allow different LP builds.
 */
public abstract class LPSolver {
    private String name;

    protected int n; // number of people
    protected int m; // number of items in batch
    protected double[][] v; // v[i][l] is disutility of item l for person i
    protected Variable[][] x; // v[i][l] is fractional amount to allocated to person l
    protected IMIP mip;

    public LPSolver(String lpName) {
        name = lpName;
    }

    public void buildMIP(List<Person> personList, List<Task> unallocatedTasks) {
        n = personList.size();
        m = unallocatedTasks.size();
        v = new double[n][m];
        x = new Variable[n][m];
        mip = new MIP();
        for (int i = 0; i < n; i++) {
            for (int l = 0; l < m; l++) {
                x[i][l] = new Variable("x" + i + "," + l, VarType.DOUBLE, 0, 1);
                v[i][l] = personList.get(i).getPreference(unallocatedTasks.get(l).getType());
                mip.add(x[i][l]);
            }
        }
        buildMIPHelper();
    }

    protected abstract void buildMIPHelper();

    public double[][] solveFractionalAllocations() {
        double[][] resultAllocations = new double[n][m];
        IMIPResult mipResult = new SolverClient().solve(mip);
        for (int i = 0; i < n; i++) {
            for (int l = 0; l < m; l++) {
                resultAllocations[i][l] = mipResult.getValue(x[i][l]);
            }
        }
        return resultAllocations;
    }

    public String getName() {
        return name;
    }
}
