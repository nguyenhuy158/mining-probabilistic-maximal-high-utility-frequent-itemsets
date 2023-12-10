import java.util.*;
import java.util.stream.Collectors;

public class CGEBAlgorithm<T> {

    // Inner class representing an item in a transaction
    static class Item<T> {
        T name;
        double probability;

        public Item(T name, double probability) {
            this.name = name;
            this.probability = probability;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "name=" + name +
                    ", probability=" + probability +
                    '}';
        }
    }

    // Inner class representing a candidate with its expectation, variance, and transaction ID
    static class Candidate<U> {
        private U itemset;
        private double expectation;
        private double variance;
        private int j;

        public Candidate(U itemset, double expectation, double variance, int j) {
            this.itemset = itemset;
            this.expectation = expectation;
            this.variance = variance;
            this.j = j;
        }

        public U getItemset() {
            return itemset;
        }

        public double getExpectation() {
            return expectation;
        }

        public double getVariance() {
            return variance;
        }

        public int getJ() {
            return j;
        }

        // Override toString method for easy printing
        @Override
        public String toString() {
            return "Candidate{" +
                    "itemset=" + itemset +
                    ", expectation=" + expectation +
                    ", variance=" + variance +
                    ", j=" + j +
                    '}';
        }
    }

    // Inner class representing a transaction with an ID and a list of items
    static class Transaction<T> {
        String id;
        List<Item<T>> items;

        public Transaction(String id, List<Item<T>> items) {
            this.id = id;
            this.items = items;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "id='" + id + '\'' +
                    ", items=" + items +
                    '}';
        }
    }

    // Main function implementing the CGEB algorithm
    public List<Candidate<T>> cgebFunction(List<Transaction<T>> D, double minsup, double minpro) {
        List<Candidate<T>> C = new ArrayList<>(); // Candidates for frequent itemsets
        List<T> L = new ArrayList<>(); // Current frequent itemsets

        // Initialize L with single items
        for (Transaction<T> T : D) {
            for (Item<T> item : T.items) {
                if (!L.contains(item.name)) {
                    L.add(item.name);
                }
            }
        }

        // Print L
        System.out.println("L: " + L.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));

        // Main loop
        int i = 1;
        Candidate<T> Ci = null;
        while (true) {
            for (T X : L) {
                double E = 0, Var = 0;
                int count = 0;

                // Iterate through transactions
                for (Transaction<T> Tj : D) {
                    if (containsItem(Tj, X)) {
                        E += getItemProbability(Tj, X);
                        Var += getItemProbability(Tj, X) * (1 - getItemProbability(Tj, X));
                        count++;
                    }
                }

                // Check conditions for frequent itemset
                if (E >= lb(E, minsup, minpro) && count >= minsup) {
                    Ci = new Candidate<>(X, E, Var, i);
                    C.add(Ci);
                    break; // Exit the loop for this item, move to the next item in L
                }
            }

            // Update L according to Ci-1
            L = updateL(C.get(i - 1), L);

            // Check if L is null
            if (L.isEmpty()) {
                break; // Exit the main loop if L is empty
            }

            i++;
        }

        return C;
    }

    // Helper method to check if an item is in a transaction
    private boolean containsItem(Transaction<T> transaction, T item) {
        return transaction.items.stream().anyMatch(i -> i.name.equals(item));
    }

    // Helper method to get the probability of an item in a transaction
    private double getItemProbability(Transaction<T> transaction, T item) {
        return transaction.items.stream().filter(i -> i.name.equals(item)).findFirst().orElseThrow().probability;
    }

    // Helper method to update L according to Ci-1
    private List<T> updateL(Candidate<T> CiMinus1, List<T> L) {
        List<T> updatedL = new ArrayList<>(L);
        updatedL.remove(CiMinus1.getItemset());
        return updatedL;
    }

    // Helper method to calculate the lower bound of expectation
    private double lb(double expectation, double minsup, double minpro) {
        double term1 = 2 * minsup - Math.log(minpro);
        double term2 = Math.sqrt(Math.pow(Math.log(minpro), 2) - 8 * minpro * Math.log(minpro));
        return (term1 - term2) / 2;
    }

    // Main method for testing the CGEB algorithm
    public static void main(String[] args) {

        List<Transaction<String>> D = new ArrayList<>();
        D.add(new Transaction<>("T1", Arrays.asList(
                new Item<>("A", 0.5),
                new Item<>("B", 0.5),
                new Item<>("C", 0.5),
                new Item<>("D", 0.5))));
        D.add(new Transaction<>("T2", Arrays.asList(
                new Item<>("B", 0.6),
                new Item<>("C", 0.6),
                new Item<>("D", 0.6))));
        D.add(new Transaction<>("T3", Arrays.asList(
                new Item<>("A", 0.7),
                new Item<>("B", 0.7),
                new Item<>("D", 0.7))));

        for (Transaction<String> t : D) {
            System.out.println(t.id + ": "
                    + t.items.stream().map(i -> i.name + "(" + i.probability + ")").collect(Collectors.joining(", ")));
        }

        double minsup = 0.5;
        double minpro = 0.5;
        CGEBAlgorithm<String> cgebAlgorithm = new CGEBAlgorithm<>();
        List<Candidate<String>> frequentItemsets = cgebAlgorithm.cgebFunction(D, minsup, minpro);

        System.out.println("Frequent itemsets:");
        for (Candidate<String> candidate : frequentItemsets) {
            System.out.println(candidate);
        }
    }
}
