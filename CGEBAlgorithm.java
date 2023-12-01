import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CGEBAlgorithm {

    static class Item {
        String name;
        double probability;

        public Item(String name, double probability) {
            this.name = name;
            this.probability = probability;
        }
    }

    static class Transaction {
        String id;
        List<Item> items;

        public Transaction(String id, List<Item> items) {
            this.id = id;
            this.items = items;
        }
    }

    public static List<List<Item>> cgebFunction(List<Transaction> D, double minsup) {
        List<List<Item>> C = new ArrayList<>(); // Candidates for frequent itemsets
        List<List<Item>> L = new ArrayList<>(); // Current frequent itemsets

        // Initialize L with single items
        for (Transaction t : D) {
            for (Item item : t.items) {
                List<Item> singleItemList = new ArrayList<>(Collections.singletonList(item));
                if (!L.contains(singleItemList)) {
                    L.add(singleItemList);
                }
            }
        }

        while (!L.isEmpty()) {
            List<List<Item>> newL = new ArrayList<>();
            for (List<Item> X : L) {
                double E = 0; // Expectation
                double Var = 0; // Variance
                int count = 0; // Count
                for (Transaction T : D) {
                    if (T.items.containsAll(X)) {
                        for (Item item : X) {
                            E += item.probability;
                            Var += item.probability * (1 - item.probability);
                        }
                        count++;
                    }
                }
                if (E >= minsup && count >= D.size() * minsup) {
                    newL.add(X);
                }
            }
            C.addAll(newL);
            // Update L to contain the next level of candidates
            L = generateNextLevel(newL);
        }

        return C;
    }

    private static List<List<Item>> generateNextLevel(List<List<Item>> prevLevel) {
        List<List<Item>> nextLevel = new ArrayList<>();
        // Logic to generate the next level of itemsets from the previous level
        // This would typically involve combining the itemsets from prevLevel
        // while ensuring no duplicates and that the itemsets are one item larger
        return nextLevel;
    }

    public static void main(String[] args) {
        List<Transaction> D = new ArrayList<>();
        D.add(new Transaction("T1", Arrays.asList(new Item("A", 0.8), new Item("B", 0.7))));
        D.add(new Transaction("T2", Arrays.asList(new Item("A", 0.9), new Item("C", 0.6))));
        D.add(new Transaction("T3", Arrays.asList(new Item("B", 0.5), new Item("C", 0.8))));

        // Print the transactions for verification
        for (Transaction transaction : D) {
            System.out.println("Transaction ID: " + transaction.id);
            for (Item item : transaction.items) {
                System.out.println("Item: " + item.name + ", Probability: " + item.probability);
            }
        }
        double minsup = 0.5;
        List<List<Item>> frequentItemsets = cgebFunction(D, minsup);
        for (List<Item> itemset : frequentItemsets) {
            System.out.println(itemset.stream().map(i -> i.name).collect(Collectors.joining(", ")));
        }
    }
}
