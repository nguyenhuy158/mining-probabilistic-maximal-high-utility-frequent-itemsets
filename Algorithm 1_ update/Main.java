import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    // CGEB algorithms
    public static List<C> CGEBFucntion(UD UD, int minsup, double minpro) {
        List<Set<String>> F = new ArrayList<>();
        Set<String> elements = new HashSet<>();

        // Seperate the prob out of UD
        List<Set<String>> D = UD.removeProbFromUD();
        System.out.println(D);

        // Get unique data
        for (Set<String> transaction : D) {
            for (String item : transaction) {
                elements.add(item);
            }
        }

        // Init the first set from the unique data (F)
        for (String e : elements) {
            Set<String> itemSet = new HashSet<>();
            itemSet.add(e);
            F.add(itemSet);
        }

        List<C> result = new ArrayList<>();
        Set<String> varList = new HashSet<>();

        // Start checking (F) if it contains in database
        while (true) {
            List<Set<String>> L = new ArrayList<>();
            for (Set<String> f : F) {
                int count = 0;
                double E = 0;
                int j = 0;
                double var = 0;
                double prob = 1;
                for (Set<String> transaction : D) {
                    if (transaction.containsAll(f)) {
                        E += UD.getProb(j);
                        var += UD.getProb(j) * (1 - UD.getProb(j));
                        prob *= UD.getProb(j);
                        count++;
                    }
                    // System.out.println(f + " " + transaction + " " + E + " " + count);
                    // If meet requirements, add this set to result (minsup and lb(E(f)))
                    if (count >= minsup && E >= lb(minsup, minpro)) {
                        // System.out.println("true");
                        // Set<String> ff = f;
                        // String tempS = String.format("%.2f", prob*(1-prob));
                        // System.out.println(tempS);
                        // ff.add(tempS);
                        result.add(new C(f, Double.parseDouble(String.format("%.2f", E)), var, j, prob));
                        varList.add(String.format("%.5f", var));
                        break;
                    }
                    j++;
                }
                L.add(f);
            }

            // If L not empty, start union each other between L and unique set
            if (L.isEmpty()) {
                return result;
            } else {
                F = generateSet(L, elements);
            }
        }
        // result.add(varList);

    }

    private static double lb(double minsup, double minpro) {
        return (2 * minsup - Math.log(minpro)
                - Math.sqrt(Math.pow(Math.log(minpro), 2) - 8 * minpro * Math.log(minpro))) / 2;
    }

    private static double ub(double minsup, double minpro) {
        return (minsup - Math.log(1 - minpro)
                + Math.sqrt(Math.pow(Math.log(1 - minpro), 2) - 2 * minpro * Math.log(1 - minpro)));
    }

    // APFI-MAX
    private static List<C> APFI_MAX(UD UD, int minsup, double minpro) {
        System.out.println("Lowerbound: " + Double.toString(lb(minsup, minpro)));
        System.out.println("Upperbound: " + Double.toString(ub(minsup, minpro)));

        List<C> C = CGEBFucntion(UD, minsup, minpro);

        List<C> res = new ArrayList<>();
        List<Set<String>> Fre_Cur = new ArrayList<>();
        List<Set<String>> Fre_Pre = new ArrayList<>();

        for (int i = C.size() - 1; i >= 0; i--) {
            Set<String> X = C.get(i).getSet();
            for (String string : X) {
                if (X.contains(string) && Fre_Pre.containsAll(X)) {
                    Fre_Cur.add(X);
                }
                if (FM(minsup, minpro, C.get(i).getE())) {
                    if (!res.contains(C.get(i))) {
                        res.add(C.get(i));
                    }
                    Fre_Cur.add(X);
                }
            }
            Fre_Pre = Fre_Cur;
            Fre_Cur = new ArrayList<>();
        }
        return res;
    }

    // FM
    private static boolean FM(int minsup, double minpro, double E) {
        if (E >= ub(minsup, minpro)) {
            return true;
        } else if (lb(minsup, minpro) < minsup) {
            return true;
        } else {
            return false;
        }
    }

    // Generation the union set from 2 set
    private static List<Set<String>> generateSet(List<Set<String>> A, Set<String> B) {
        List<Set<String>> result = new ArrayList<>();
        List<String> bList = new ArrayList<>(B);

        for (Set<String> a : A) {
            List<String> aList = new ArrayList<>(a);
            int index = bList.indexOf(aList.get(aList.size() - 1));
            for (int j = index + 1; j < bList.size(); j++) {
                Set<String> newSet = new HashSet<>(a);
                newSet.add(bList.get(j));
                result.add(newSet);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        // Read data
        UD UD = new UD("data1.txt");

        // Call CGEBFucntion and store the result
        List<C> cgebResults = CGEBFucntion(UD, 2, 0.6);

        // Print the results from CGEBFucntion
        System.out.println("CGEBFucntion Results:");
        for (C result : cgebResults) {
            System.out.println(result);
        }

        List<C> apfiMaxResults = APFI_MAX(UD, 2, 0.6);
        System.out.println("APFI_MAX Results:");
        for (C result : apfiMaxResults) {
            System.out.println(result);
        }
    }

}
