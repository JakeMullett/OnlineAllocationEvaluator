package algorithms;

import edu.harvard.econcs.jopt.solver.mip.*;
import objects.Person;
import objects.Task;

import java.util.List;

/**
 * Finds nearly envy-free integral allocation.
 */
public class EgalitarianEquivalentLPSolver extends LPSolver {

    public EgalitarianEquivalentLPSolver() {
        super("Egalitarian Equivalent");
    }

    @Override
    protected void buildMIPHelper() {
        mip.setObjectiveMax(false);
        Variable u = new Variable("u", VarType.DOUBLE, 0, MIP.MAX_VALUE);
        mip.add(u);
        mip.addObjectiveTerm(1, u);
        for (int l = 0; l < m; l++) {
            Constraint constraint = new Constraint(CompareType.EQ, 1);
            for (int i = 0; i < n; i++) {
                constraint.addTerm(1, x[i][l]);
            }
            mip.add(constraint);
        }
        for (int i = 0; i < n; i++) {
            Constraint constraint = new Constraint(CompareType.EQ, 0);
            double totalDisutility = 0;
            for (int l = 0; l < m; l++) {
                constraint.addTerm(v[i][l], x[i][l]);
                totalDisutility -= v[i][l];
            }
            constraint.addTerm(totalDisutility, u);
            mip.add(constraint);
        }
    }
}
