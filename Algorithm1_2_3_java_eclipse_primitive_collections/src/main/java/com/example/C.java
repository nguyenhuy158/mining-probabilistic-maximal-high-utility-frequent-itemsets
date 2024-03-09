package com.example;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

/**
 * The {@code C} class represents an object that stores a mutable set of strings
 * along with associated numerical values.
 *
 * <p>
 * This class uses Eclipse Collections for efficient management of the set.
 * </p>
 *
 * <p>
 * It stores a mutable set of strings, along with numerical values representing
 * exception (E), variance (Var), threshold (j), and probability (prob).
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * MutableSet&lt;String&gt; stringSet = Sets.mutable.with("item1", "item2", "item3");
 * C myObject = new C(stringSet, 5.0, 2.0, 10, 0.8);
 * System.out.println(myObject);
 * </pre>
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
     * Constructs a {@code C} object with the specified parameters.
     *
     * @param set  a mutable set of strings
     * @param E    exception value
     * @param Var  variance value
     * @param j    threshold value
     * @param prob probability value
     */
    public C(MutableSet<String> set, double E, double Var, int j, double prob) {
        this.set = set;
        this.E = E;
        this.Var = Var;
        this.j = j;
        this.prob = prob;
    }

    /**
     * Returns the mutable set of strings stored in this {@code C} object.
     *
     * @return the mutable set of strings
     */
    public MutableSet<String> getSet() {
        return this.set;
    }

    /**
     * Returns the exception value stored in this {@code C} object.
     *
     * @return the exception value
     */
    public double getE() {
        return this.E;
    }

    /**
     * Returns the variance value stored in this {@code C} object.
     *
     * @return the variance value
     */
    public double getVar() {
        return this.Var;
    }

    /**
     * Returns the threshold value stored in this {@code C} object.
     *
     * @return the threshold value
     */
    public int getJ() {
        return this.j;
    }

    /**
     * Returns the probability value stored in this {@code C} object.
     *
     * @return the probability value
     */
    public double getProb() {
        return this.prob;
    }

    /**
     * Returns a string representation of this {@code C} object.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "Set: " + this.set.toString() +
                ", Exception: " + this.E +
                ", Variance: " + this.Var +
                ", Threshold: " + this.j +
                ", Probability: " + this.prob;
    }
}
