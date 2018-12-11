package algorithms;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.VarType;
import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * Uses linear programming to determine proper fractional allocations of items in a batch.
 */
public class EnvyFreeLPSolver extends LPSolver {

    public EnvyFreeLPSolver() {
        super("Envy Free");
    }

    @Override
    protected void buildMIPHelper() {
        mip.setObjectiveMax(false);
        Variable[][] x0 = new Variable[n][m];
        Variable[][] x1 = new Variable[n][m];
        for (int i = 0; i < n; i++) {
            for (int l = 0; l < m; l++) {
                x0[i][l] = new Variable("x0" + i + "," + l, VarType.BOOLEAN, 0, 1);
                x1[i][l] = new Variable("x1" + i + "," + l, VarType.BOOLEAN, 0, 1);
                mip.add(x0[i][l]);
                mip.add(x1[i][l]);
                mip.addObjectiveTerm(1, x[i][l]);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Constraint constraint = new Constraint(CompareType.GEQ, 0);
                for (int l = 0; l < m; l++) {
                    constraint.addTerm(-1 * v[i][l], x[i][l]);
                    constraint.addTerm(v[i][l], x[j][l]);
                }
                mip.add(constraint);
            }
        }
        for (int l = 0; l < m; l++) {
            Constraint constraint = new Constraint(CompareType.EQ, 1);
            for (int i = 0; i < n; i++) {
                constraint.addTerm(1, x[i][l]);
            }
            mip.add(constraint);
        }
        for (int i = 0; i < n; i++) {
            for (int l = 0; l < m; l++) {
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
        for (int i = 0; i < n; i++) {
            Constraint constraint = new Constraint(CompareType.GEQ, m - 2);
            for (int l = 0; l < m; l++) {
                constraint.addTerm(1, x0[i][l]);
                constraint.addTerm(1, x1[i][l]);
            }
            mip.add(constraint);
        }
    }
}
