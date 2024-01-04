import java.util.HashSet;
import java.util.Set;

public class C {
    private Set<String> set = new HashSet<>();
    private double E;
    private double Var;
    private int j;
    private double prob;

    public C(Set<String> set, double E, double Var, int j, double prob){
        this.set = set;
        this.E = E;
        this.Var = Var;
        this.j = j;
        this.prob = prob;
    }

    public Set<String> getSet(){
        return this.set;
    }

    public double getE(){
        return this.E;
    }

    public double getVar(){
        return this.Var;
    }

    public int getJ(){
        return this.j;
    }

    public double getProb(){
        return this.prob;
    }

    public String toString(){
        return "Set: " + this.set.toString() + ", Exception: " + this.E + ", Variance: " + this.Var + ", Threshold: " + this.j + ", Probability: " + this.prob ;
    }
}
