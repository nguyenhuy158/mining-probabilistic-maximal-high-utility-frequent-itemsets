import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CGEB {

    // CGEB algorithms
    public static List<Set<String>> CGEBFucntion(UD UD, int minsup, double E) {
        List<Set<String>> F = new ArrayList<>();
        Set<String> elements = new HashSet<>();

        // Seperate the prob out of UD
        List<Set<String>> D = UD.removeProbFromUD();

        // Get unique data
        for (Set<String> transaction : D) {
            for (String item : transaction) {
                elements.add(item);
            }
        }

        // Init the first set from the unique data (F) [A,B,C,D]
        for (String e : elements) {
            Set<String> itemSet = new HashSet<>();
            itemSet.add(e);
            F.add(itemSet);
        }

        List<Set<String>> result = new ArrayList<>();

        // Start checking (F) if it contains in database
        while (!F.isEmpty()) {
            List<Set<String>> L = new ArrayList<>();
            for (Set<String> f : F) {
                int count = 0;
                double prob = 0;
                int temp = 0;
                for (Set<String> transaction : D) {
                    if (transaction.containsAll(f)) {
                        count++;
                        prob += UD.getProb(temp);
                    }
                    temp++;
                }
                if (count != 0)
                    prob = prob / count;
                // If meet requirements, add this set to result (minsup and lb(E(f)))
                if (count >= minsup && prob >= E) {
                    // Adding to L for preparing the next set generation
                    result.add(f);
                }
                L.add(f);
            }

            // If L not empty, start union each other between L and unique set
            if (L.isEmpty()) {
                break;
            } else {
                F = generateSet(L, elements);
            }
        }

        return result;
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

        System.out.println(UD.removeProbFromUD());
        List<Set<String>> res = CGEBFucntion(UD, 1, 0.5);
        for (Set<String> set : res) {
            System.out.println(set);
        }
    }
}
