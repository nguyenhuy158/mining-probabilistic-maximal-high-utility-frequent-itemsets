import java.util.HashSet;
import java.util.Set;

//This class is used for storing set and prob
public class Item {
    private Set<String> item;
    private double probability;

    // Init and get data to object
    public Item(String item, String probability) {
        Set<String> temp = new HashSet<>();
        for (int i = 0; i < item.length(); i++) {
            temp.add(String.valueOf(item.charAt(i)));
        }

        this.item = temp;

        // Conver the string text in to prob for fraction cases(Ex: 3/5 -> 0.6)
        try {
            double prob = Double.parseDouble(probability);
            this.probability = prob;
        } catch (NumberFormatException e) {
            String[] prob = probability.split("/");
            double probRes = Double.parseDouble(prob[0]) / Double.parseDouble(prob[1]);

            String rounded = String.format("%.2f", probRes);
            this.probability = Double.parseDouble(rounded);
        }
    }

    public Set<String> getItem() {
        return this.item;
    }

    public double getProbability() {
        return this.probability;
    }

    public String toString() {
        return String.valueOf(this.item) + "(" + String.valueOf(this.probability) + ")";
    }
}
