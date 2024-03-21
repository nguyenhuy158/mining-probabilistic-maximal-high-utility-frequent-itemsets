package mining.frequentitemsets;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

/**
 *  The class C represents a data structure of Candidate
 */
public class C {
    // Mutable set of strings
    private MutableSet<String> set = Sets.mutable.empty();

    // Numerical values
    private double E;
    private double Var;
    private int j;
    private double prob;

    /**
     * Constructs with full parameters.
     *
     * @param set   The set of candidate.
     * @param E     The expected value.
     * @param Var   The variance.
     * @param j     The threshold value.
     * @param prob  The probability.
     */
    public C(MutableSet<String> set, double E, double Var, int j, double prob) {
        this.set = set;
        this.E = E;
        this.Var = Var;
        this.j = j;
        this.prob = prob;
    }

    // Getter and Setter
    public MutableSet<String> getSet() {
        return this.set;
    }

    public void setSet(MutableSet<String> set) {
        this.set = set;
    }

    public double getE() {
        return this.E;
    }

    public void setE(double e) {
        this.E = e;
    }

    public double getVar() {
        return this.Var;
    }

    public void setVar(double var) {
        this.Var = var;
    }

    public int getJ() {
        return this.j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public double getProb() {
        return this.prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    // Override toString()
    @Override
    public String toString() {
        return "Set: " + this.set.toString() +
                ", Exception: " + this.E +
                ", Variance: " + this.Var +
                ", Threshold: " + this.j +
                ", Probability: " + this.prob;
    }
}
