package mining.frequentitemsets;

import java.util.Random;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

/**
 * This class is used for storing set and prob
 */
public class Item {
    // Set of items
    private MutableSet<String> item;

    // List of associated random probabilities
    private MutableList<Double> probTemp;

    /**
     * Constructs with full parameters.
     *
     * @param Item   list of element in item.
     */
    public Item(String[] items) {
        // Initialize the item set and probability list
        this.item = Sets.mutable.empty();
        this.probTemp = Lists.mutable.empty();

        // Add each item to the set and generate a random probability for each
        for (String i : items) {
            this.item.add(i);
            this.probTemp.add(probRandom());
        }
    }

    // Getter and Setter
    public MutableSet<String> getItem() {
        return this.item;
    }

    public void setItem(MutableSet<String> item) {
        this.item = item;
    }

    // Override toString()
    @Override
    public String toString() {
        return String.valueOf(this.item);
    }

    /**
     * generates a random double value
     * @return the range of values returned is [0.0, 1.0).
     */
    private double probRandom() {
        Random random = new Random();
        return random.nextDouble();
    }
}
