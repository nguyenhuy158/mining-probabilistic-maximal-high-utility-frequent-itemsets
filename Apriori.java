import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Apriori {

    public static List<Set<String>> aprioriFunction(List<Set<String>> D, int minsup) {
        List<Set<String>> F = new ArrayList<>();
        Set<String> elements = new HashSet<>();

        for (Set<String> transaction : D) {
            for (String item : transaction) {
                elements.add(item);
            }
        }

        for (String e : elements) {
            Set<String> itemSet = new HashSet<>();
            itemSet.add(e);
            F.add(itemSet);
        }

        List<Set<String>> result = new ArrayList<>();

        while (!F.isEmpty()) {
            List<Set<String>> L = new ArrayList<>();
            for (Set<String> f : F) {
                int count = 0;
                for (Set<String> transaction : D) {
                    if (transaction.containsAll(f)) {
                        count++;
                    }
                }
                if (count >= minsup) {
                    L.add(f);
                    result.add(f);
                }
            }

            if (L.isEmpty()) {
                break;
            } else {
                F = generateSet(L, elements);
            }
        }

        return result;
    }

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
        List<Set<String>> D = new ArrayList<>();
        D.add(new HashSet<>(List.of("A", "B", "C")));
        D.add(new HashSet<>(List.of("B", "C")));
        D.add(new HashSet<>(List.of("A", "B")));

        List<Set<String>> res = aprioriFunction(D, 1);
        for (Set<String> set : res) {
            System.out.println(set);
        }
    }
}
