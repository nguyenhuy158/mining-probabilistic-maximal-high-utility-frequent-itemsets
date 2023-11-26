import java.util.*;
import java.util.stream.Collectors;

public class UncertainDatabase {
    List<Transaction> transactions;

    public UncertainDatabase(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public double calculateExpectation(String attributeItem) {
        return transactions.stream()
                .flatMap(t -> t.attributes.stream())
                .filter(a -> a.item.equals(attributeItem))
                .mapToDouble(a -> a.probability)
                .sum();
    }

    public double calculateVariance(String attributeItem, double expectation) {
        double varianceSum = transactions.stream()
                .flatMap(t -> t.attributes.stream())
                .filter(a -> a.item.equals(attributeItem))
                .mapToDouble(a -> a.probability * (1 - a.probability))
                .sum();
        return varianceSum;
    }

    public Set<String> generateCandidates(double supportThresholdT1, double expectationThresholdT2) {
        Set<String> candidates = new HashSet<>();
        Set<String> uniqueAttributes = getUniqueAttributes();

        double varianceThreshold = 0.4;

        for (String attributeItem : uniqueAttributes) {
            double expectation = calculateExpectation(attributeItem);
            double variance = calculateVariance(attributeItem, expectation);

            System.out.println("Attribute: " + attributeItem);
            System.out.println("Expectation: " + expectation);
            System.out.println("Variance: " + variance);
            // Check if the item meets the expected support, probabilistic support, and
            // variance criteria
            if (expectation > supportThresholdT1 && expectation > expectationThresholdT2
                    && variance <= varianceThreshold) {
                candidates.add(attributeItem);
            }
        }

        return candidates;
    }

    private Set<String> getUniqueAttributes() {
        return transactions.stream()
                .flatMap(t -> t.attributes.stream())
                .map(a -> a.item)
                .collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("T1",
                Arrays.asList(new Attribute("A", 0.7), new Attribute("B", 0.6), new Attribute("C", 0.3))));
        transactions.add(new Transaction("T2", Arrays.asList(new Attribute("B", 0.8), new Attribute("C", 0.6))));
        transactions.add(new Transaction("T3", Arrays.asList(new Attribute("A", 0.3), new Attribute("B", 0.8))));

        UncertainDatabase db = new UncertainDatabase(transactions);
        double supportThresholdT1 = 0.5;
        double expectationThresholdT2 = 0.5;

        Set<String> candidates = db.generateCandidates(supportThresholdT1, expectationThresholdT2);

        System.out.println("Candidates: " + candidates);
    }
}